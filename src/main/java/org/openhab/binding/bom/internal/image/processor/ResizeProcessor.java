/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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

import java.awt.image.BufferedImage;

import org.openhab.binding.bom.internal.image.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ResizeProcessor} class resizes an image to the desired size.
 *
 * @author Thomas Tan - Initial contribution
 */
public class ResizeProcessor extends ImageProcessor {
    private final Logger logger = LoggerFactory.getLogger(ResizeProcessor.class);

    @Override
    public BufferedImage process(BufferedImage image, String properties) {
        String[] params = parseParams(properties);

        if (params.length != 2) {
            logger.warn("Invalid parameters provided to resize image \"{}\"", properties);
            return image;
        }

        try {
            return ImageUtils.resizeImage(image, Integer.parseInt(params[0].trim()),
                    Integer.parseInt(params[1].trim()));
        } catch (NumberFormatException ex) {
            logger.warn("Invalid parameters provided to resize image \"{}\"", properties);
        }

        return image;
    }
}
