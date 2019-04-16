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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

import org.apache.commons.lang3.StringUtils;
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
import org.openhab.binding.bom.internal.image.ImageGenerators;
import org.openhab.binding.bom.internal.image.ImageLayer;
import org.openhab.binding.bom.internal.image.ImageLayerConfig;
import org.openhab.binding.bom.internal.image.ImageLayerGroup;
import org.openhab.binding.bom.internal.image.ImageProcessors;
import org.openhab.binding.bom.internal.image.ImageType;
import org.openhab.binding.bom.internal.image.ImageUtils;
import org.openhab.binding.bom.internal.image.SeriesImageLayer;
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
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss z");

    private final Logger logger = LoggerFactory.getLogger(BomImageHandler.class);

    private BomImageDownloader imageDownloader = new BomImageDownloader();

    private ScheduledFuture<?> monitorImagesJob;

    private BomImageConfiguration config;

    private long latestImageTimestamp = 0;

    private List<ImageLayerConfig> imageLayerConfigs;

    private Properties imagePostProcessingProperties;
    private Properties localTimestampProperties;

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

        latestImageTimestamp = 0;

        config = getConfigAs(BomImageConfiguration.class);
        parseConfigs();

        logger.info("Reloaded configuration");

        refreshImage();
    }

    private synchronized void parseConfigs() {
        this.imageLayerConfigs = parseImageLayersProperties(config.layersConfiguration);

        if (StringUtils.isNotBlank(config.imagePostProcessing)) {
            this.imagePostProcessingProperties = Properties.create(config.imagePostProcessing);
        }

        if (config.embedLocalTimestamp && StringUtils.isNotBlank(config.localTimestampProperties)) {
            this.localTimestampProperties = Properties.create(config.localTimestampProperties);
        }
    }

    private synchronized void refreshImage() {
        if (StringUtils.isBlank(config.ftpServer) || StringUtils.isBlank(config.imagesPath)
                || StringUtils.isBlank(config.productId)) {
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
                    List<SeriesImageLayer> seriesImageLayers = imageDownloader.retrieveSeriesImages(ftp,
                            config.imagesPath, imageFtpFiles, imageLayerConfigs);
                    List<ImageLayer> imageLayers = imageDownloader.retrieveImages(ftp, config.transparenciesPath,
                            imageLayerConfigs);
                    generateImages(seriesImageLayers, imageLayers);
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

    private void generateImages(List<SeriesImageLayer> seriesImageLayers, List<ImageLayer> imageLayers) {
        List<ImageLayer> backgroundLayers = imageLayers.stream()
                .filter(layer -> layer.getImageLayerConfig().getLayerGroup() == ImageLayerGroup.BACKGROUND)
                .collect(Collectors.toList());
        List<ImageLayer> foregroundLayers = imageLayers.stream()
                .filter(layer -> layer.getImageLayerConfig().getLayerGroup() == ImageLayerGroup.FOREGROUND)
                .collect(Collectors.toList());

        BufferedImage backgroundImage = processAndCombineImageLayers(backgroundLayers);
        BufferedImage foregroundImage = processAndCombineImageLayers(foregroundLayers);

        List<BufferedImage> finalImages = new ArrayList<>();

        for (SeriesImageLayer seriesImageLayer : seriesImageLayers) {
            BufferedImage middlegroundImage = ImageProcessors.process(seriesImageLayer.getImage(),
                    seriesImageLayer.getImageLayerConfig().getProperties());

            BufferedImage finalImage = ImageUtils.merge(backgroundImage, middlegroundImage);
            finalImage = ImageUtils.merge(finalImage, foregroundImage);

            if (config.embedLocalTimestamp) {
                finalImage = embedLocalTimestamp(finalImage, seriesImageLayer.getTimestamp());
            }

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

    private BufferedImage embedLocalTimestamp(BufferedImage baseImage, ZonedDateTime timestamp) {
        Properties clonedProperties = this.localTimestampProperties != null
                ? Properties.copy(this.localTimestampProperties)
                : Properties.create(null);

        String format = clonedProperties.get("format");
        DateTimeFormatter formatter = StringUtils.isBlank(format) ? DEFAULT_DATE_TIME_FORMATTER
                : DateTimeFormatter.ofPattern(format);

        StringBuilder sb = new StringBuilder();
        String prefix = clonedProperties.get("prefix");
        String suffix = clonedProperties.get("stuffix");

        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix.trim()).append(" ");
        }

        if (StringUtils.isNotEmpty(suffix)) {
            sb.append(" ").append(suffix);
        }

        sb.append(formatter.format(timestamp));

        clonedProperties.put("text", sb.toString());

        BufferedImage textImage = ImageGenerators.get("text").generate(baseImage.getWidth(), baseImage.getHeight(),
                clonedProperties);

        return textImage != null ? ImageUtils.merge(baseImage, textImage) : baseImage;
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
                result = ImageUtils.merge(result, ImageProcessors.process(imageLayer));
            } else {
                result = ImageProcessors.process(imageLayer);
                first = false;
            }
        }

        return result;
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
                    ImageType imageType;
                    ImageLayerGroup imageLayerGroup;

                    if (TAG_SERIES.equalsIgnoreCase(image)) {
                        imageType = ImageType.SERIES;
                        imageLayerGroup = ImageLayerGroup.MIDDLEGROUND;
                        foundMiddleground = true;
                    } else if (!foundMiddleground) {
                        imageType = ImageType.STATIC;
                        imageLayerGroup = ImageLayerGroup.BACKGROUND;
                    } else {
                        imageType = ImageType.STATIC;
                        imageLayerGroup = ImageLayerGroup.FOREGROUND;
                    }

                    layerProperties.remove(ImageLayerConfig.KEY_IMAGE);

                    imageLayers.add(new ImageLayerConfig(image, imageType, imageLayerGroup, layerProperties));
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
