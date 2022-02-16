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
package org.openhab.binding.bom.internal.properties;

import java.util.ArrayList;

/**
 * The {@link PropertiesList} class contains a list of {@link Properties}
 * used across the whole binding.
 *
 * @author Thomas Tan - Initial contribution
 */
public class PropertiesList extends ArrayList<Properties> {
    private static final long serialVersionUID = -1783928196136374968L;

    private static final String SET_SEPARATOR = ";";

    public static PropertiesList create(String propertiesList) {
        PropertiesList propList = new PropertiesList();

        String[] sets = propertiesList.split(SET_SEPARATOR);

        for (String set : sets) {
            propList.add(Properties.create(set.trim()));
        }

        return propList;
    }
}
