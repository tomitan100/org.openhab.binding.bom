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
package org.openhab.binding.bom.internal.image;

import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;

/**
 * The {@link SeriesImageLayer} class defines the image layer with timestamp.
 *
 * @author Thomas Tan - Initial contribution
 */
public class SeriesImageLayer extends ImageLayer {
    private final ZonedDateTime timestamp;

    public SeriesImageLayer(ImageLayerConfig imageLayerConfig, BufferedImage image, ZonedDateTime timestamp) {
        super(imageLayerConfig, image);
        this.timestamp = timestamp;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
