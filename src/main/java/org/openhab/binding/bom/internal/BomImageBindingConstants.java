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
package org.openhab.binding.bom.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link BomImageBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Thomas Tan - Initial contribution
 */
@NonNullByDefault
public class BomImageBindingConstants {
    private static final String BINDING_ID = "bom";

    public static final ThingTypeUID THING_TYPE_IMAGE = new ThingTypeUID(BINDING_ID, "image");

    public static final String CHANNEL_LAST_CHECK_DATE_TIME = "lastCheckDateTime";
    public static final String CHANNEL_LAST_UPDATE_DATE_TIME = "lastUpdateDateTime";
    public static final String CHANNEL_SOURCE_IMAGES = "sourceImages";
    public static final String CHANNEL_GENERATED_GIF = "generatedGif";
    public static final String CHANNEL_GENERATED_PNGS = "generatedPngs";
}
