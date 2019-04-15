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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.openhab.binding.bom.internal.image.ImageLayer;
import org.openhab.binding.bom.internal.image.ImageLayerConfig;
import org.openhab.binding.bom.internal.image.ImageType;
import org.openhab.binding.bom.internal.image.SeriesImageLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BomImageDownloader} class is a utility class to download images.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomImageDownloader {
    private final Logger logger = LoggerFactory.getLogger(BomImageDownloader.class);

    private static final long BOM_REPORT_TIME_DELAY_MINS = 5;

    public List<SeriesImageLayer> retrieveSeriesImages(FTPClient ftp, String dirPath, FTPFile[] imageFtpFiles,
            List<ImageLayerConfig> layerConfigs) {
        List<SeriesImageLayer> seriesImages = new ArrayList<>();
        String imagesDirPath = dirPath.charAt(dirPath.length() - 1) != '/' ? dirPath + "/" : dirPath;
        InputStream in = null;

        ImageLayerConfig seriesImageConfig = layerConfigs.stream()
                .filter(layerConfig -> layerConfig.getType() == ImageType.SERIES).findAny().orElse(null);

        for (FTPFile ftpFile : imageFtpFiles) {
            String remoteFilePath = imagesDirPath + ftpFile.getName();

            try {
                logger.debug("Downloading {}", remoteFilePath);
                in = ftp.retrieveFileStream(remoteFilePath);

                BufferedImage downloadedImage = ImageIO.read(in);

                if (ftp.completePendingCommand()) {
                    seriesImages.add(new SeriesImageLayer(seriesImageConfig, downloadedImage,
                            getTimestamp(ftpFile.getTimestamp())));
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

        return seriesImages;
    }

    public List<ImageLayer> retrieveImages(FTPClient ftp, String dirPath, List<ImageLayerConfig> layerConfigs) {
        final List<ImageLayer> imageLayers = new ArrayList<>();
        String imagesDirPath = dirPath.charAt(dirPath.length() - 1) != '/' ? dirPath + "/" : dirPath;

        layerConfigs.stream().forEach(layerConfig -> {
            if (layerConfig.getType() == ImageType.STATIC) {
                String imagePath = layerConfig.getImagePath();

                if (imagePath.matches("^(?i)(?:ftp|https?|file)://.*")) {
                    try {
                        logger.debug("Downloading {}", imagePath);
                        BufferedInputStream in = new BufferedInputStream(new URL(imagePath).openStream());
                        BufferedImage downloadedImage = ImageIO.read(in);
                        imageLayers.add(new ImageLayer(layerConfig, downloadedImage));
                    } catch (IOException ex) {
                        logger.warn("Unable to retrieve " + imagePath, ex);
                    }
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
            } else {
                imageLayers.add(new ImageLayer(layerConfig));
            }
        });

        return imageLayers;

    }

    private ZonedDateTime getTimestamp(Calendar timestamp) {
        ZonedDateTime sourceTimestamp = ZonedDateTime.of(timestamp.get(Calendar.YEAR),
                timestamp.get(Calendar.MONTH) + 1, timestamp.get(Calendar.DAY_OF_MONTH),
                timestamp.get(Calendar.HOUR_OF_DAY), timestamp.get(Calendar.MINUTE), timestamp.get(Calendar.SECOND), 0,
                ZoneId.of("UTC"));

        return sourceTimestamp.withZoneSameInstant(ZoneId.systemDefault()).minusMinutes(BOM_REPORT_TIME_DELAY_MINS);
    }
}
