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

import java.util.ArrayList;

/**
 * The {@link Properties} class contain map of key and values.
 *
 * @author Thomas Tan - Initial contribution
 */
public class Properties extends ArrayList<Property> {
    private static final long serialVersionUID = 1L;

    private static final String PAIR_SEPARATOR = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";

    public static Properties create(String properties) {
        Properties props = new Properties();

        if (properties != null) {
            String[] pairs = properties.split(PAIR_SEPARATOR);

            for (String pair : pairs) {
                String[] kv = pair.split(KEY_VALUE_SEPARATOR);

                if (kv.length == 2) {
                    props.add(Property.forProperty(kv[0].trim(), kv[1].trim()));
                }
            }
        }

        return props;
    }

    public static Properties copy(Properties sourceProperties) {
        return (Properties) sourceProperties.clone();
    }

    private Properties() {
        super();
    }

    private Properties(Properties otherProperties) {
        super(otherProperties);
    }

    public String getValue(String key) {
        Property pair = this.stream().filter(prop -> key.equals(prop.getKey())).findAny().orElse(null);

        return pair != null ? pair.getValue() : null;
    }

    public String getValue(String key, String defaultValue) {
        Property pair = this.stream().filter(prop -> key.equals(prop.getKey())).findAny().orElse(null);

        return pair != null ? pair.getValue() : defaultValue;
    }
}
