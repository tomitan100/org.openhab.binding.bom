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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

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

    private static final String URL_PROTOCOL_PATTERN = "^(?i)(?:ftp|https?|file)://.*";
    private static final String TIFF_FILE_PATTERN = "(?i).*\\.tiff?$";

    public List<SeriesImageLayer> retrieveSeriesImages(FTPClient ftp, String dirPath, FTPFile[] imageFtpFiles,
            List<ImageLayerConfig> layerConfigs, Integer tiffImageIndex) {
        List<SeriesImageLayer> seriesImages = new ArrayList<>();
        String imagesDirPath = dirPath.charAt(dirPath.length() - 1) != '/' ? dirPath + "/" : dirPath;

        ImageLayerConfig seriesImageConfig = layerConfigs.stream()
                .filter(layerConfig -> layerConfig.getType() == ImageType.SERIES).findAny().orElse(null);

        for (FTPFile ftpFile : imageFtpFiles) {
            String remoteFilePath = imagesDirPath + ftpFile.getName();

            logger.debug("Downloading {}", remoteFilePath);

            // TIFF file handling
            if (ftpFile.getName().matches(TIFF_FILE_PATTERN)) {
                BufferedImage downloadedImage = retrieveTiffImage(ftp, remoteFilePath, tiffImageIndex);

                if (downloadedImage != null) {
                    seriesImages.add(new SeriesImageLayer(seriesImageConfig, downloadedImage, getTimestamp(ftpFile)));
                }
            } else {
                InputStream in = null;

                try {
                    in = ftp.retrieveFileStream(remoteFilePath);

                    BufferedImage downloadedImage = ImageIO.read(in);

                    if (ftp.completePendingCommand()) {
                        seriesImages
                                .add(new SeriesImageLayer(seriesImageConfig, downloadedImage, getTimestamp(ftpFile)));
                    }
                } catch (IOException ex) {
                    logger.error("Unable to retrieve " + remoteFilePath + ": ", ex);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            logger.warn("Unable to close stream: ", ex);
                        }
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

                if (imagePath.matches(URL_PROTOCOL_PATTERN)) {
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

    private BufferedImage retrieveTiffImage(FTPClient ftp, String remoteFilePath, Integer tiffImageIndex) {
        FileOutputStream out = null;
        File tmpFile = null;

        try {
            tmpFile = File.createTempFile("bom-tiff", null);
            out = new FileOutputStream(tmpFile);

            try {
                if (ftp.retrieveFile(remoteFilePath, out)) {
                    return readTiff(remoteFilePath, tmpFile, tiffImageIndex);
                }
            } catch (IOException ex) {
                logger.warn("Unable to download TIFF file " + remoteFilePath + " from server: ", ex);
            }
        } catch (IOException ex) {
            logger.warn("Unable to create temporary file to for downloaded TIFF " + remoteFilePath + ": ", ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    logger.warn("Unable to close output stream of temporary file: ", ex);
                }
            }

            if (tmpFile != null) {
                tmpFile.delete();
            }
        }

        return null;
    }

    private BufferedImage readTiff(String filename, File file, Integer tiffImageIndex) {
        ImageReader reader = null;
        ImageInputStream imageInputStream = null;
        try {
            imageInputStream = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);

            if (!readers.hasNext()) {
                logger.error("No reader for TIFF file!");
                throw new IllegalArgumentException("No reader for TIFF file!");
            }

            reader = readers.next();
            reader.setInput(imageInputStream);

            ImageReadParam param = reader.getDefaultReadParam();

            int numberOfImages = reader.getNumImages(false);
            int imageIndex = tiffImageIndex == null ? numberOfImages - 1 : tiffImageIndex;

            if (imageIndex < 0 && imageIndex >= numberOfImages) {
                logger.warn("Invalid TIFF image index " + tiffImageIndex + " for file " + filename
                        + ".  Valid is is >= 0 and < " + numberOfImages);
                return null;
            }

            return reader.read(imageIndex, param);
        } catch (IOException ex) {
            logger.error("Unable to read TIFF image file " + filename, ex);
        } finally {
            if (reader != null) {
                reader.dispose();
            }

            if (imageInputStream != null) {
                try {
                    imageInputStream.close();
                } catch (IOException ex) {
                    logger.error("Unable to close TIFF image file " + filename, ex);
                }
            }
        }

        return null;
    }

    private ZonedDateTime getTimestamp(FTPFile ftpFile) {
        ZonedDateTime sourceTimestamp;

        Matcher matcher = Constants.FILE_DATE_TIME_PATTERN.matcher(ftpFile.getName());

        if (matcher.matches()) {
            LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1), Constants.FILE_DATE_TIME_FORMATTER);
            sourceTimestamp = localDateTime.atZone(Constants.ZONE_ID_UTC);
        } else {
            logger.warn("Unable to parse date time from filename - using timestamp from file instead.");
            Calendar timestamp = ftpFile.getTimestamp();
            sourceTimestamp = ZonedDateTime.of(timestamp.get(Calendar.YEAR), timestamp.get(Calendar.MONTH) + 1,
                    timestamp.get(Calendar.DAY_OF_MONTH), timestamp.get(Calendar.HOUR_OF_DAY),
                    timestamp.get(Calendar.MINUTE), timestamp.get(Calendar.SECOND), 0, Constants.ZONE_ID_UTC);
        }

        return sourceTimestamp.withZoneSameInstant(ZoneId.systemDefault());
    }
}
