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
package org.openhab.binding.bom.internal.image.processor;

import java.awt.image.BufferedImage;

import org.openhab.binding.bom.internal.image.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OpacityProcessor} class changes the opacity of the image.
 *
 * @author Thomas Tan - Initial contribution
 */
public class OpacityProcessor extends ImageProcessor {
    private final Logger logger = LoggerFactory.getLogger(OpacityProcessor.class);

    @Override
    public BufferedImage process(BufferedImage image, String properties) {
        try {
            return ImageUtils.createTranslucentImage(image, Float.parseFloat(properties.trim()));
        } catch (NumberFormatException ex) {
            logger.warn("Invalid parameter provided to create translucent image \"{}\"", properties);
        }

        return image;
    }
}
