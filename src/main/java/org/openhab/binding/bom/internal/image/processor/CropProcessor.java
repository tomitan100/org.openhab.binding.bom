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
package org.openhab.binding.bom.internal.image.processor;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.openhab.binding.bom.internal.image.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CropProcessor} class crops an image into the specified size
 *
 * @author Thomas Tan - Initial contribution
 */
public class CropProcessor extends ImageProcessor {
    private final Logger logger = LoggerFactory.getLogger(CropProcessor.class);

    @Override
    public BufferedImage process(BufferedImage image, String properties) {
        String[] params = parseParams(properties);

        if (params == null || params.length != 4) {
            logger.warn("Invalid parameters provided to crop image \"{}\"", properties);
            return image;
        }

        try {
            Rectangle rectangle = new Rectangle(Integer.parseInt(params[0]), Integer.parseInt(params[1]),
                    Integer.parseInt(params[2]), Integer.parseInt(params[3]));

            return ImageUtils.cropImage(image, rectangle);
        } catch (NumberFormatException ex) {
            logger.warn("Invalid parameters provided to crop image \"{}\"", properties);
        }

        return image;
    }

}
