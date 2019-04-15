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
package org.openhab.binding.bom.internal.image;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.bom.internal.image.processor.CanvasProcessor;
import org.openhab.binding.bom.internal.image.processor.CropProcessor;
import org.openhab.binding.bom.internal.image.processor.ImageProcessor;
import org.openhab.binding.bom.internal.image.processor.OpacityProcessor;
import org.openhab.binding.bom.internal.image.processor.PositionProcessor;
import org.openhab.binding.bom.internal.image.processor.ResizeProcessor;
import org.openhab.binding.bom.internal.properties.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ImageProcessors} class processes images according to the operations provided in the properties map.
 *
 * @author Thomas Tan - Initial contribution
 */
public class ImageProcessors {
    private final static Logger logger = LoggerFactory.getLogger(ImageProcessors.class);

    private final static Map<String, ImageProcessor> imageProcessors = new HashMap<>();

    static {
        imageProcessors.put("canvas", new CanvasProcessor());
        imageProcessors.put("crop", new CropProcessor());
        imageProcessors.put("opacity", new OpacityProcessor());
        imageProcessors.put("position", new PositionProcessor());
        imageProcessors.put("resize", new ResizeProcessor());
    }

    public static BufferedImage process(ImageLayer imageLayer) {
        return process(imageLayer.getImage(), imageLayer.getImageLayerConfig().getProperties());
    }

    public static BufferedImage process(BufferedImage image, Properties properties) {
        BufferedImage result = image;

        for (String operation : properties.keySet()) {
            result = process(result, operation, properties.get(operation));
        }

        return result;
    }

    public static BufferedImage process(BufferedImage image, String operation, String properties) {
        ImageProcessor imageProcessor = imageProcessors.get(operation);

        if (imageProcessor != null) {
            return imageProcessor.process(image, properties);
        }

        logger.warn("Invalid image processing operation \"{}\"=\"{}\"", operation, properties);

        return null;
    }
}
