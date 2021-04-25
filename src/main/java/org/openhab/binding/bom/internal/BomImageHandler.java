/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
import org.openhab.binding.bom.internal.net.FtpRegexImageFileFilter;
import org.openhab.binding.bom.internal.properties.Properties;
import org.openhab.binding.bom.internal.properties.PropertiesList;
import org.openhab.binding.bom.internal.properties.Property;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BomImageHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomImageHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BomImageHandler.class);

    private static final String TAG_PRODUCT_ID = "${pid}";

    private static final String TAG_SERIES = "${series}";

    private static final int GIF_IMAGE_TYPE = 5;

    private static final int SERIES_HARD_LIMIT = 100;

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss z");

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
        this.imagePostProcessingProperties = Properties.create(config.imagePostProcessing);
        this.localTimestampProperties = Properties
                .create(config.embedLocalTimestamp ? config.localTimestampProperties : null);
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
        updateChannelsState(ZonedDateTime.now(), null, null, null, null);

        DateTimeRange dateTimeRange = getDateTimeRange(config.dateRange);

        try {
            FTPFileFilter filter;

            if (StringUtils.isNotBlank(config.filenameRegex)) {
                filter = new FtpRegexImageFileFilter(config.filenameRegex, dateTimeRange);
            } else {
                filter = new FtpImageFileFilter(config.productId, dateTimeRange);
            }

            FTPFile[] imageFtpFiles = listFtpFiles(ftp, config.imagesPath, filter);

            if (imageFtpFiles == null) {
                logger.debug("No new images found");
                return;
            }

            if (imageFtpFiles.length > SERIES_HARD_LIMIT) {
                logger.warn("The number of files have hit a hard limit of " + SERIES_HARD_LIMIT
                        + ".  Refine your product ID with regular expression.  Processing will not continue.");
                return;
            }

            if (filesUpdated(imageFtpFiles)) {
                Arrays.sort(imageFtpFiles, FtpFileComparator.SORT_BY_TIMESTAMP_ASC);

                if (config.generatePngs || config.generateGif) {
                    List<SeriesImageLayer> seriesImageLayers = imageDownloader.retrieveSeriesImages(ftp,
                            config.imagesPath, imageFtpFiles, imageLayerConfigs, config.tiffImageIndex);
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

    private DateTimeRange getDateTimeRange(String dateRange) {
        String range = dateRange.toLowerCase().trim();
        Long durationInSeconds = 24L * 60L * 60L;

        if (range.indexOf("last_") == 0) {
            Long duration = DateTimeUtils.parseDuration(dateRange.substring(5));

            if (duration != null) {
                durationInSeconds = duration;
            }

            range = "";
        }

        switch (range) {
            case "today":
                ZonedDateTime nowToday = ZonedDateTime.now(Constants.ZONE_ID_UTC);
                return new DateTimeRange(nowToday.toLocalDate().atStartOfDay(Constants.ZONE_ID_UTC), nowToday);
            case "yesterday":
                ZonedDateTime startOfToday = ZonedDateTime.now(Constants.ZONE_ID_UTC).toLocalDate()
                        .atStartOfDay(Constants.ZONE_ID_UTC);
                return new DateTimeRange(startOfToday.minusDays(1), startOfToday);
            default:
                ZonedDateTime nowDefault = ZonedDateTime.now(Constants.ZONE_ID_UTC);
                return new DateTimeRange(nowDefault.minusSeconds(durationInSeconds), nowDefault);
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

            BufferedImage finalImage = middlegroundImage;

            if (backgroundImage != null) {
                finalImage = ImageUtils.merge(backgroundImage, middlegroundImage);
            }

            if (foregroundImage != null) {
                finalImage = ImageUtils.merge(finalImage, foregroundImage);
            }

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

        String format = clonedProperties.getValue("format");
        DateTimeFormatter formatter = format == null ? DEFAULT_DATE_TIME_FORMATTER
                : DateTimeFormatter.ofPattern(format);

        ZonedDateTime adjustedTimestamp = getAdjustedTimestamp(timestamp,
                clonedProperties.getValue("adjust-timestamp"));

        StringBuilder sb = new StringBuilder();

        sb.append(formatter.format(adjustedTimestamp));

        clonedProperties.add(Property.forProperty(Constants.PROP_KEY_TEXT, sb.toString()));

        BufferedImage textImage = ImageGenerators.get(Constants.PROP_KEY_TEXT).generate(baseImage.getWidth(),
                baseImage.getHeight(), clonedProperties);

        return textImage != null ? ImageUtils.merge(baseImage, textImage) : baseImage;
    }

    private ZonedDateTime getAdjustedTimestamp(ZonedDateTime timestamp, String timeAdjustment) {
        if (StringUtils.isBlank(timeAdjustment)) {
            return timestamp;
        }

        Long timeValue = DateTimeUtils.parseDuration(timeAdjustment.trim());

        if (timeValue != null) {
            return timestamp.plusSeconds(timeValue);
        }

        logger.warn("Invalid adjust-timestamp value \"{}\"", timeAdjustment);

        return timestamp;
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
            logger.error("Unable to generate GIF {}.gif", outputFilePath, ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                logger.error("Unable to close output file {}.gif", outputFilePath, ex);
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
                logger.error("Unable to write PNG {}", outputPath, ex);
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
            logger.error("Unable to retrieve file list from {}", path, ex);
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
            ftp.setRemoteVerificationEnabled(false);

            return ftp;
        } catch (IOException ex) {
            logger.error("Unable to connect to FTP server {}", ftpServer, ex);
        }

        return null;
    }

    private List<ImageLayerConfig> parseImageLayersProperties(String propertiesList) {
        List<ImageLayerConfig> imageLayers = new ArrayList<>();

        PropertiesList propLayers = PropertiesList.create(replaceBindVariables(propertiesList));

        if (propLayers.size() > 0) {
            boolean foundMiddleground = false;

            for (Properties layerProperties : propLayers) {
                String image = layerProperties.getValue(Constants.PROP_KEY_IMAGE);

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
