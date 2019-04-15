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

import java.awt.image.BufferedImage;

import org.apache.commons.lang3.StringUtils;

/**
 * The {@link ImageProcessor} interface.
 *
 * @author Thomas Tan - Initial contribution
 */
public abstract class ImageProcessor {
    public abstract BufferedImage process(BufferedImage image, String properties);

    protected String[] parseParams(String properties) {
        if (StringUtils.isBlank(properties)) {
            return null;
        }

        return properties.replaceAll("\\s+", " ").trim().split(" ");
    }
}
