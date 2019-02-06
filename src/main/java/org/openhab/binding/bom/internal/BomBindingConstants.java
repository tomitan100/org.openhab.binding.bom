/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bom.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link BomBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Thomas Tan - Initial contribution
 */
@NonNullByDefault
public class BomBindingConstants {

    private static final String BINDING_ID = "bom";

    public static final ThingTypeUID THING_TYPE_WEATHER = new ThingTypeUID(BINDING_ID, "weather");

    public static final String CHANNEL_DATE_TIME = "dateTime";
    public static final String CHANNEL_ICON = "icon";
    public static final String CHANNEL_PRECIS = "precis";
    public static final String CHANNEL_FORECAST = "forecast";
    public static final String CHANNEL_MIN_TEMPERATURE = "minTemperature";
    public static final String CHANNEL_MAX_TEMPERATURE = "maxTemperature";
    public static final String CHANNEL_PRECIPITATION = "precipitation";
    public static final String CHANNEL_UV_ALERT = "uvAlert";
    public static final String CHANNEL_OBSERVATION_DATE_TIME = "observationDateTime";
    public static final String CHANNEL_AIR_TEMPERATURE = "airTemperature";
    public static final String CHANNEL_DEW_POINT = "dewPoint";
    public static final String CHANNEL_RELATIVE_HUMIDITY = "relativeHumidity";
    public static final String CHANNEL_PRESSURE = "pressure";
    public static final String CHANNEL_WIND_DIRECTION = "windDirection";
    public static final String CHANNEL_WIND_DIRECTION_DEGREES = "windDirectionDegrees";
    public static final String CHANNEL_WIND_SPEED_KMH = "winSpeedKmh";
    public static final String CHANNEL_WIND_SPEED_KNOTS = "windSpeedKnots";
    public static final String CHANNEL_RAINFALL = "rainfall";

    public static final String CHANNEL_GROUP_TODAY = "day1";
    public static final String CHANNEL_GROUP_DAY_PREFIX = "day";

    public static final int NUMBER_OF_FORECASTS = 8;
}
