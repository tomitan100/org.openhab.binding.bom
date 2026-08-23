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

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DateTimeRange}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class DateTimeRangeTest {
    @Test
    public void testGettersReturnConstructorValues() {
        ZonedDateTime start = ZonedDateTime.now().minusHours(24);
        ZonedDateTime end = ZonedDateTime.now();

        DateTimeRange range = new DateTimeRange(start, end);

        assertEquals(start, range.getStartDateTime());
        assertEquals(end, range.getEndDateTime());
    }

    @Test
    public void testRangeContainsTimestampsBetweenStartAndEnd() {
        ZonedDateTime start = ZonedDateTime.parse("2026-08-22T09:00:00+00:00[UTC]");
        ZonedDateTime end = ZonedDateTime.parse("2026-08-23T09:00:00+00:00[UTC]");

        DateTimeRange range = new DateTimeRange(start, end);

        assertTrue(range.getStartDateTime().isBefore(range.getEndDateTime()));
        assertFalse(ZonedDateTime.parse("2026-08-21T00:00:00+00:00[UTC]").isAfter(range.getStartDateTime())
                && !ZonedDateTime.parse("2026-08-21T00:00:00+00:00[UTC]").isAfter(range.getEndDateTime()));
    }
}
