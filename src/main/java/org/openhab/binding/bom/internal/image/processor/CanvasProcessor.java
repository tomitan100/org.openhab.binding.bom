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

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.openhab.binding.bom.internal.image.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CanvasProcessor} class adjust the canvas size
 *
 * @author Thomas Tan - Initial contribution
 */
public class CanvasProcessor extends ImageProcessor {
    private final Logger logger = LoggerFactory.getLogger(CanvasProcessor.class);

    @Override
    public BufferedImage process(BufferedImage image, String properties) {
        // width height x y background-colour
        String[] params = parseParams(properties);

        if (params == null || params.length < 4) {
            logger.warn("Invalid parameters provided to resize the canvas \"{}\"", properties);
            return image;
        }

        try {
            int width = Integer.parseInt(params[0]);
            int height = Integer.parseInt(params[1]);
            int x = Integer.parseInt(params[2]);
            int y = Integer.parseInt(params[3]);
            Color bgColor = params.length > 4 ? Color.decode(params[4]) : null;

            return ImageUtils.resizeCanvas(image, width, height, x, y, bgColor);
        } catch (Exception ex) {
            logger.warn("Invalid parameters provided to resize canvas \"{}\"", properties);
        }

        return image;
    }

}
