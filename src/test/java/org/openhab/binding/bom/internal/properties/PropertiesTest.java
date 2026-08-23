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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Properties}, {@link Property} and {@link PropertiesList}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class PropertiesTest {
    @Test
    public void testCreateParsesKeyValuePairs() {
        Properties properties = Properties.create("format=dd/MM/yyyy, font-size=16");

        assertEquals(2, properties.size());
        assertEquals("dd/MM/yyyy", properties.getValue("format"));
        assertEquals("16", properties.getValue("font-size"));
    }

    @Test
    public void testCreateTrimsKeysAndValues() {
        Properties properties = Properties.create(" text = Hello World ");

        assertEquals(1, properties.size());
        assertEquals("Hello World", properties.getValue("text"));
        assertEquals("Hello World", properties.get(0).getValue());
        assertEquals("text", properties.get(0).getKey());
    }

    @Test
    public void testGetValueMissingKeyReturnsNull() {
        Properties properties = Properties.create("a=1");

        assertNull(properties.getValue("b"));
    }

    @Test
    public void testGetValueWithDefault() {
        Properties properties = Properties.create("a=1");

        assertEquals("1", properties.getValue("a", "fallback"));
        assertEquals("fallback", properties.getValue("b", "fallback"));
    }

    @Test
    public void testCreateIgnoresMalformedPairs() {
        Properties properties = Properties.create("novalue, a=1, too=many=equals");

        assertEquals(1, properties.size());
        assertEquals("1", properties.getValue("a"));
    }

    @Test
    public void testCreateNullReturnsEmpty() {
        assertEquals(0, Properties.create(null).size());
    }

    @Test
    public void testCopyIsIndependent() {
        Properties source = Properties.create("a=1");
        Properties copy = Properties.copy(source);

        assertEquals(source, copy);
        assertNotSame(source, copy);
    }

    @Test
    public void testPropertiesListSplitsOnSemicolon() {
        PropertiesList layers = PropertiesList.create("image=a.png; image=${series}; image=b.png");

        assertEquals(3, layers.size());
        assertEquals("a.png", layers.get(0).getValue("image"));
        assertEquals("${series}", layers.get(1).getValue("image"));
        assertEquals("b.png", layers.get(2).getValue("image"));
    }
}
