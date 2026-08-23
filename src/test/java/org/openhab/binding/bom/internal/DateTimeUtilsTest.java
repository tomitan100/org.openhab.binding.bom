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
package org.openhab.binding.bom.internal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DateTimeUtils}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class DateTimeUtilsTest {
    @Test
    public void testParseSeconds() {
        assertEquals(Long.valueOf(45), DateTimeUtils.parseDuration("45s"));
    }

    @Test
    public void testParseMinutes() {
        assertEquals(Long.valueOf(60), DateTimeUtils.parseDuration("1m"));
        assertEquals(Long.valueOf(5400), DateTimeUtils.parseDuration("90m"));
    }

    @Test
    public void testParseHours() {
        assertEquals(Long.valueOf(3600), DateTimeUtils.parseDuration("1h"));
        assertEquals(Long.valueOf(7200), DateTimeUtils.parseDuration("2h"));
    }

    @Test
    public void testParseDays() {
        assertEquals(Long.valueOf(86400), DateTimeUtils.parseDuration("1d"));
        assertEquals(Long.valueOf(172800), DateTimeUtils.parseDuration("2d"));
    }

    @Test
    public void testParseNegativeDuration() {
        assertEquals(Long.valueOf(-300), DateTimeUtils.parseDuration("-5m"));
    }

    @Test
    public void testInvalidDurationsReturnNull() {
        assertNull(DateTimeUtils.parseDuration("abc"));
        assertNull(DateTimeUtils.parseDuration("5x"));
        assertNull(DateTimeUtils.parseDuration(""));
        assertNull(DateTimeUtils.parseDuration("m"));
    }
}
