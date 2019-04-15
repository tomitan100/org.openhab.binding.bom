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

/**
 * The {@link ImageLayer} class defines the image layer
 *
 * @author Thomas Tan - Initial contribution
 */
public class ImageLayer {
    private final ImageLayerConfig imageLayerConfig;
    private BufferedImage image;

    public ImageLayer(ImageLayerConfig imageLayerConfig) {
        this.imageLayerConfig = imageLayerConfig;
        this.image = null;
    }

    public ImageLayer(ImageLayerConfig imageLayerConfig, BufferedImage image) {
        this.imageLayerConfig = imageLayerConfig;
        this.image = image;
    }

    public ImageLayerConfig getImageLayerConfig() {
        return imageLayerConfig;
    }

    public BufferedImage getImage() {
        return image;
    }
}
