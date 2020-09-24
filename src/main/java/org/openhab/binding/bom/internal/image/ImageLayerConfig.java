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
package org.openhab.binding.bom.internal.image;

import org.openhab.binding.bom.internal.properties.Properties;

/**
 * The {@link ImageLayerConfig} class contains the image layer configuration.
 *
 * @author Thomas Tan - Initial contribution
 */
public class ImageLayerConfig {
    private final String imagePath;
    private final ImageType type;
    private final ImageLayerGroup layerGroup;
    private final ImageGeneratorType generatorType;

    private final Properties properties;

    public ImageLayerConfig(String imagePath, ImageType type, ImageLayerGroup layerGroup, Properties properties) {
        this(imagePath, type, layerGroup, null, properties);
    }

    public ImageLayerConfig(String imagePath, ImageType type, ImageLayerGroup layerGroup,
            ImageGeneratorType generatorType, Properties properties) {
        this.imagePath = imagePath;
        this.type = type;
        this.layerGroup = layerGroup;
        this.generatorType = generatorType;
        this.properties = properties;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ImageType getType() {
        return type;
    }

    public ImageLayerGroup getLayerGroup() {
        return layerGroup;
    }

    public ImageGeneratorType getGeneratorType() {
        return generatorType;
    }

    public Properties getProperties() {
        return properties;
    }
}
