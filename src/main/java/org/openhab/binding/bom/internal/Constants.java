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
package org.openhab.binding.bom.internal;

import java.time.ZoneId;

/**
 * The {@link Constnats} class define constants.
 *
 * @author Thomas Tan - Initial contribution
 */
public class Constants {
    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    public static final String PROP_KEY_IMAGE = "image";

    public static final String PROP_KEY_TEXT = "text";
}
