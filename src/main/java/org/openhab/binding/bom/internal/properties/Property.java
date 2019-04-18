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
package org.openhab.binding.bom.internal.properties;

/**
 * The {@link Property} class defines a single key and value property
 * used across the whole binding.
 *
 * @author Thomas Tan - Initial contribution
 */
public class Property {
    private String key;
    private String value;

    public static Property forProperty(String key, String value) {
        return new Property(key, value);
    }

    private Property(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
