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
 * Unit tests for {@link Forecast}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class ForecastTest {
    @Test
    public void testSettersAndGetters() {
        ZonedDateTime now = ZonedDateTime.now();

        Forecast forecast = new Forecast();
        forecast.setZonedDatetime(now);
        forecast.setIconCode("1");
        forecast.setPrecis("Sunny.");
        forecast.setMinTemperature(9.5);
        forecast.setMaxTemperature(19.0);
        forecast.setPrecipitation("60%");
        forecast.setMinPrecipitation(0.0);
        forecast.setMaxPrecipitation(1.2);

        assertEquals(now, forecast.getZonedDatetime());
        assertEquals("1", forecast.getIconCode());
        assertEquals("Sunny.", forecast.getPrecis());
        assertEquals(Double.valueOf(9.5), forecast.getMinTemperature());
        assertEquals(Double.valueOf(19.0), forecast.getMaxTemperature());
        assertEquals("60%", forecast.getPrecipitation());
        assertEquals(Double.valueOf(0.0), forecast.getMinPrecipitation());
        assertEquals(Double.valueOf(1.2), forecast.getMaxPrecipitation());
    }

    @Test
    public void testDefaults() {
        Forecast forecast = new Forecast();

        assertNull(forecast.getZonedDatetime());
        assertNull(forecast.getIconCode());
        assertNull(forecast.getMinTemperature());
        assertNull(forecast.getMaxTemperature());
        assertNull(forecast.getMinPrecipitation());
        assertNull(forecast.getMaxPrecipitation());
        assertEquals("", forecast.getPrecis());
        assertEquals("", forecast.getPrecipitation());
    }
}
