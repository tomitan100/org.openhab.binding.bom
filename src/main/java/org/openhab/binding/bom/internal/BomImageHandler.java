/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.bom.internal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.bom.internal.image.GifSequenceWriter;
import org.openhab.binding.bom.internal.image.ImageLayer;
import org.openhab.binding.bom.internal.image.ImageLayerConfig;
import org.openhab.binding.bom.internal.image.ImageLayerConfig.LayerGroup;
import org.openhab.binding.bom.internal.image.ImageProcessors;
import org.openhab.binding.bom.internal.image.ImageUtils;
import org.openhab.binding.bom.internal.net.FtpFileComparator;
import org.openhab.binding.bom.internal.net.FtpImageFileFilter;
import org.openhab.binding.bom.internal.properties.Properties;
import org.openhab.binding.bom.internal.properties.PropertiesList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BomImageHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomImageHandler extends BaseThingHandler {
    private static final String TAG_PRODUCT_ID = "${pid}";
    private static final String TAG_SERIES = "${series}";
    private static final int GIF_IMAGE_TYPE = 5;

    private final Logger logger = LoggerFactory.getLogger(BomImageHandler.class);

    private ScheduledFuture<?> monitorImagesJob;

    private BomImageConfiguration config;

    private long latestImageTimestamp = 0;

    private List<ImageLayerConfig> imageLayerConfigs;

    private Properties imagePostProcessingProperties;

    public BomImageHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        config = getConfigAs(BomImageConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);
        parseConfigs();

        monitorImagesJob = scheduler.scheduleWithFixedDelay(this::refreshImage, 0, config.monitoringInterval,
                TimeUnit.MINUTES);
    }

    @Override
    public void dispose() {
        stopRefresh();
        super.dispose();
    }

    private synchronized void stopRefresh() {
        if (monitorImagesJob != null && !monitorImagesJob.isCancelled()) {
            monitorImagesJob.cancel(true);
            monitorImagesJob = null;
            logger.info("Cancelled monitor images refresh job");
        }
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);

        config = getConfigAs(BomImageConfiguration.class);
        parseConfigs();
    }

    private synchronized void parseConfigs() {
        this.imageLayerConfigs = parseImageLayersProperties(config.layersConfiguration);

        if (config.imagePostProcessing != null && config.imagePostProcessing.length() > 0) {
            this.imagePostProcessingProperties = Properties.create(config.imagePostProcessing);
        }

        logger.info("Parsed configuration");
    }

    private void refreshImage() {
        if (config.ftpServer == null || config.ftpServer.length() == 0 || config.imagesPath == null
                || config.imagesPath.length() == 0 || config.productId == null || config.productId.length() == 0) {
            logger.error("FTP server, images path and product ID are required.");
            updateStatus(ThingStatus.UNKNOWN);
            return;
        }

        FTPClient ftp = connect(config.ftpServer.replaceAll("(?i)ftp://", ""), "anonymous", "anonymous");
        logger.debug("Connected to {}", config.ftpServer);

        if (ftp == null) {
            logger.error("Unable to connect to {}", config.ftpServer);
            updateStatus(ThingStatus.OFFLINE);
            return;
        }

        updateStatus(ThingStatus.ONLINE);

        try {
            FTPFileFilter filter = new FtpImageFileFilter(config.productId);

            FTPFile[] imageFtpFiles = listFtpFiles(ftp, config.imagesPath, filter);

            updateChannelsState(ZonedDateTime.now(), null, null, null, null);

            if (imageFtpFiles == null) {
                logger.debug("No new images found");
                return;
            }

            if (filesUpdated(imageFtpFiles)) {
                Arrays.sort(imageFtpFiles, FtpFileComparator.SORT_BY_TIMESTAMP_ASC);

                if (config.generatePngs || config.generateGif) {
                    List<BufferedImage> seriesImages = retrieveImages(ftp, config.imagesPath, imageFtpFiles);
                    List<ImageLayer> imageLayers = retrieveImages(ftp, config.transparenciesPath, imageLayerConfigs);
                    generateImages(seriesImages, imageLayers);
                }

                updateChannels(imageFtpFiles);
            }
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.logout();
                    ftp.disconnect();
                } catch (IOException ex) {
                    logger.warn("Unable to disconnect from FTP server.", ex);
                }
            }
        }
    }

    private void updateChannels(FTPFile[] imageFtpFiles) {
        StringBuilder sourceImages = new StringBuilder();

        for (int i = 0; i < imageFtpFiles.length; i++) {
            sourceImages.append(imageFtpFiles[i].getName());

            if (i + 1 < imageFtpFiles.length) {
                sourceImages.append(",");
            }
        }

        String generatedGif = null;
        String generatedPngs = null;

        if (config.generateGif || config.generatePngs) {
            String filenamePrefix = replaceBindVariables(config.imageOutputFilename);

            if (config.generateGif) {
                generatedGif = filenamePrefix + ".gif";
            }

            if (config.generatePngs) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < imageFtpFiles.length; i++) {
                    sb.append(filenamePrefix).append('.').append(i).append(".png");

                    if (i + 1 < imageFtpFiles.length) {
                        sb.append(",");
                    }
                }

                generatedPngs = sb.toString();
            }
        }

        updateChannelsState(null, ZonedDateTime.now(), sourceImages.toString(), generatedGif, generatedPngs);
    }

    private void updateChannelsState(ZonedDateTime lastCheckDateTime, ZonedDateTime lastUpdateDateTime,
            String sourceImages, String generatedGif, String generatedPngs) {
        getThing().getChannels().stream().forEach(channel -> {
            switch (channel.getUID().getIdWithoutGroup()) {
                case BomImageBindingConstants.CHANNEL_LAST_CHECK_DATE_TIME:
                    if (lastCheckDateTime != null) {
                        updateState(channel.getUID(), new DateTimeType(lastCheckDateTime));
                    }
                    break;
                case BomImageBindingConstants.CHANNEL_LAST_UPDATE_DATE_TIME:
                    if (lastUpdateDateTime != null) {
                        updateState(channel.getUID(), new DateTimeType(lastUpdateDateTime));
                    }
                    break;
                case BomImageBindingConstants.CHANNEL_SOURCE_IMAGES:
                    if (sourceImages != null) {
                        updateState(channel.getUID(), new StringType(sourceImages));
                    }
                    break;
                case BomImageBindingConstants.CHANNEL_GENERATED_GIF:
                    if (generatedGif != null) {
                        updateState(channel.getUID(), new StringType(generatedGif));
                    }
                    break;
                case BomImageBindingConstants.CHANNEL_GENERATED_PNGS:
                    if (generatedPngs != null) {
                        updateState(channel.getUID(), new StringType(generatedPngs));
                    }
                    break;
            }
        });
    }

    private void generateImages(List<BufferedImage> seriesImages, List<ImageLayer> imageLayers) {
        List<ImageLayer> backgroundLayers = imageLayers.stream()
                .filter(layer -> layer.getImageLayerConfig().getLayerGroup() == ImageLayerConfig.LayerGroup.BACKGROUND)
                .collect(Collectors.toList());
        List<ImageLayer> foregroundLayers = imageLayers.stream()
                .filter(layer -> layer.getImageLayerConfig().getLayerGroup() == ImageLayerConfig.LayerGroup.FOREGROUND)
                .collect(Collectors.toList());
        ImageLayer middlegroundLayer = imageLayers.stream().filter(
                layer -> layer.getImageLayerConfig().getLayerGroup() == ImageLayerConfig.LayerGroup.MIDDLEGROUND)
                .findAny().orElse(null);

        BufferedImage backgroundImage = processAndCombineImageLayers(backgroundLayers);
        BufferedImage foregroundImage = processAndCombineImageLayers(foregroundLayers);

        List<BufferedImage> finalImages = new ArrayList<>();

        for (BufferedImage seriesImage : seriesImages) {
            BufferedImage middlegroundImage = ImageProcessors.process(seriesImage,
                    middlegroundLayer.getImageLayerConfig().getProperties());

            BufferedImage finalImage = ImageUtils.mergeBufferedImage(backgroundImage, middlegroundImage);
            finalImage = ImageUtils.mergeBufferedImage(finalImage, foregroundImage);

            if (this.imagePostProcessingProperties != null) {
                finalImage = ImageProcessors.process(finalImage, this.imagePostProcessingProperties);
            }

            finalImages.add(finalImage);
        }

        if (!finalImages.isEmpty()) {
            String outputPath = config.imageOutputPath.charAt(config.imageOutputPath.length() - 1) != File.separatorChar
                    ? config.imageOutputPath + File.separator
                    : config.imageOutputPath;
            String outputFilePath = outputPath + replaceBindVariables(config.imageOutputFilename);

            if (config.generateGif) {
                generateGifv(finalImages, outputFilePath);
            }

            if (config.generatePngs) {
                generatePngs(finalImages, outputFilePath);
            }
        }
    }

    private void generateGifv(List<BufferedImage> images, String outputFilePath) {
        ImageOutputStream out = null;
        GifSequenceWriter writer = null;

        try {
            out = new FileImageOutputStream(new File(outputFilePath + ".gif"));
            writer = new GifSequenceWriter(out, GIF_IMAGE_TYPE, config.gifImageDelay, config.gifImageLoop);

            for (BufferedImage image : images) {
                writer.writeToSequence(image);
            }

            logger.info("Generated GIF {}.gif", outputFilePath);
        } catch (IOException ex) {
            logger.error("Unable to generate GIF " + outputFilePath + ".gif", ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                logger.error("Unable to close output file " + outputFilePath + ".gif", ex);
            }
        }
    }

    private void generatePngs(List<BufferedImage> images, String outputFilePath) {
        for (int i = 0; i < images.size(); i++) {
            String outputPath = outputFilePath + "." + i + ".png";
            try {
                ImageIO.write(images.get(i), "png", new File(outputPath));
                logger.info("Generated PNG {}", outputPath);
            } catch (IOException ex) {
                logger.error("Unable to write PNG " + outputPath, ex);
            }
        }
    }

    private BufferedImage processAndCombineImageLayers(List<ImageLayer> imageLayers) {
        BufferedImage result = null;
        boolean first = true;

        for (ImageLayer imageLayer : imageLayers) {
            if (!first) {
                result = ImageUtils.mergeBufferedImage(result, ImageProcessors.process(imageLayer));
            } else {
                result = ImageProcessors.process(imageLayer);
                first = false;
            }
        }

        return result;
    }

    private List<BufferedImage> retrieveImages(FTPClient ftp, String dirPath, FTPFile[] imageFtpFiles) {
        List<BufferedImage> images = new ArrayList<>();
        String imagesDirPath = dirPath.charAt(dirPath.length() - 1) != '/' ? dirPath + "/" : dirPath;
        InputStream in = null;

        for (FTPFile ftpFile : imageFtpFiles) {
            String remoteFilePath = imagesDirPath + ftpFile.getName();

            try {
                logger.debug("Downloading {}", remoteFilePath);
                in = ftp.retrieveFileStream(remoteFilePath);

                BufferedImage downloadedImage = ImageIO.read(in);

                if (ftp.completePendingCommand()) {
                    images.add(downloadedImage);
                }
            } catch (IOException ex) {
                logger.error("Unable to retrieve " + remoteFilePath, ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        logger.warn("Unable to close stream", ex);
                    }
                }
            }
        }

        return images;
    }

    private List<ImageLayer> retrieveImages(FTPClient ftp, String dirPath, List<ImageLayerConfig> layerConfigs) {
        final List<ImageLayer> imageLayers = new ArrayList<>();
        String imagesDirPath = dirPath.charAt(dirPath.length() - 1) != '/' ? dirPath + "/" : dirPath;

        layerConfigs.stream().forEach(layerConfig -> {
            if (layerConfig.getLayerGroup() == ImageLayerConfig.LayerGroup.MIDDLEGROUND) {
                imageLayers.add(new ImageLayer(layerConfig, null));
            } else {
                String imagePath = layerConfig.getImagePath();
                String imagePathLc = imagePath.toLowerCase();

                if (imagePathLc.indexOf("http") == 0 || imagePathLc.indexOf("ftp") == 0) {
                    // TODO
                } else {
                    InputStream in = null;
                    String remoteFilePath = imagesDirPath + imagePath;

                    try {
                        logger.debug("Downloading {}", remoteFilePath);
                        in = ftp.retrieveFileStream(remoteFilePath);
                        BufferedImage downloadedImage = ImageIO.read(in);

                        if (ftp.completePendingCommand()) {
                            imageLayers.add(new ImageLayer(layerConfig, downloadedImage));
                        } else {
                            logger.warn("Unable to retrieve {}" + remoteFilePath);
                        }
                    } catch (IOException ex) {
                        logger.warn("Unable to retrieve " + remoteFilePath, ex);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException ex) {
                                logger.warn("Unable to close image stream " + remoteFilePath, ex);
                            }
                        }
                    }
                }
            }
        });

        return imageLayers;
    }

    private boolean filesUpdated(FTPFile[] files) {
        long maxTimestamp = 0;

        for (FTPFile file : files) {
            long timestamp = file.getTimestamp().getTimeInMillis();
            if (timestamp > maxTimestamp) {
                maxTimestamp = timestamp;
            }
        }

        if (maxTimestamp > this.latestImageTimestamp) {
            this.latestImageTimestamp = maxTimestamp;

            return true;
        }

        return false;
    }

    private FTPFile[] listFtpFiles(FTPClient ftp, String path, FTPFileFilter filter) {
        try {
            return ftp.listFiles(path, filter);
        } catch (IOException ex) {
            logger.error("Unable to retrieve file list from " + path, ex);
        }

        return null;
    }

    private FTPClient connect(String ftpServer, String username, String password) {
        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(ftpServer);
            ftp.login(username, password);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            return ftp;
        } catch (IOException ex) {
            logger.error("Unable to connect to FTP server " + ftpServer, ex);
        }

        return null;
    }

    private List<ImageLayerConfig> parseImageLayersProperties(String propertiesList) {
        List<ImageLayerConfig> imageLayers = new ArrayList<>();

        PropertiesList propLayers = PropertiesList.create(replaceBindVariables(propertiesList));

        if (propLayers.size() > 0) {
            boolean foundMiddleground = false;

            for (Properties layerProperties : propLayers) {
                String image = layerProperties.get(ImageLayerConfig.KEY_IMAGE);

                if (image != null) {
                    LayerGroup layerGroup;

                    if (TAG_SERIES.equalsIgnoreCase(image)) {
                        layerGroup = LayerGroup.MIDDLEGROUND;
                        foundMiddleground = true;
                    } else if (!foundMiddleground) {
                        layerGroup = LayerGroup.BACKGROUND;
                    } else {
                        layerGroup = LayerGroup.FOREGROUND;
                    }

                    layerProperties.remove(ImageLayerConfig.KEY_IMAGE);

                    imageLayers.add(new ImageLayerConfig(image, layerGroup, layerProperties));
                }
            }
        }

        return imageLayers;
    }

    private String replaceBindVariables(String value) {
        return value != null && value.contains(TAG_PRODUCT_ID) ? value.replace(TAG_PRODUCT_ID, config.productId)
                : value;
    }
}
