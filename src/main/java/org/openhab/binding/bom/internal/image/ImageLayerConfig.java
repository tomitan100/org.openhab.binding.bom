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

import org.openhab.binding.bom.internal.properties.Properties;

/**
 * The {@link ImageLayerConfig} class contains the image layer configuration.
 *
 * @author Thomas Tan - Initial contribution
 */
public class ImageLayerConfig {
    public enum LayerGroup {
        BACKGROUND,
        MIDDLEGROUND,
        FOREGROUND
    }

    public static final String KEY_IMAGE = "image";

    private final String imagePath;
    private final LayerGroup layerGroup;

    private final Properties properties;

    public ImageLayerConfig(String imagePath, LayerGroup layerGroup, Properties properties) {
        this.imagePath = imagePath;
        this.layerGroup = layerGroup;
        this.properties = properties;
    }

    public String getImagePath() {
        return imagePath;
    }

    public LayerGroup getLayerGroup() {
        return layerGroup;
    }

    public Properties getProperties() {
        return properties;
    }
}
