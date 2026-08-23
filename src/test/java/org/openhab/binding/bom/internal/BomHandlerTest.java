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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandlerCallback;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.State;
import org.mockito.Mockito;

/**
 * Tests for {@link BomHandler}. The handler is exercised end-to-end against
 * BOM-style XML fixtures served from a file:// URL so no network or FTP server
 * is required.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomHandlerTest {
    private static final String[] OBSERVATION_CHANNEL_IDS = { BomBindingConstants.CHANNEL_WEATHER_STATION,
            BomBindingConstants.CHANNEL_OBSERVATION_DATE_TIME, BomBindingConstants.CHANNEL_APPARENT_TEMPERATURE,
            BomBindingConstants.CHANNEL_AIR_TEMPERATURE, BomBindingConstants.CHANNEL_DEW_POINT,
            BomBindingConstants.CHANNEL_RELATIVE_HUMIDITY, BomBindingConstants.CHANNEL_PRESSURE,
            BomBindingConstants.CHANNEL_WIND_DIRECTION, BomBindingConstants.CHANNEL_WIND_DIRECTION_DEGREES,
            BomBindingConstants.CHANNEL_WIND_SPEED_KMH, BomBindingConstants.CHANNEL_WIND_SPEED_KNOTS,
            BomBindingConstants.CHANNEL_RAINFALL, BomBindingConstants.CHANNEL_RAINFALL_24_HOUR };

    private static final String[] FORECAST_CHANNEL_IDS = { BomBindingConstants.CHANNEL_DATE_TIME,
            BomBindingConstants.CHANNEL_ICON, BomBindingConstants.CHANNEL_PRECIS,
            BomBindingConstants.CHANNEL_FORECAST, BomBindingConstants.CHANNEL_MIN_TEMPERATURE,
            BomBindingConstants.CHANNEL_MAX_TEMPERATURE, BomBindingConstants.CHANNEL_PRECIPITATION,
            BomBindingConstants.CHANNEL_MIN_PRECIPITATION, BomBindingConstants.CHANNEL_MAX_PRECIPITATION,
            BomBindingConstants.CHANNEL_UV_ALERT };

    private Thing thing;

    private BomHandler handler;

    private ThingHandlerCallback callback;

    private List<ChannelUID> updatedChannelUIDs;

    private List<State> updatedStates;

    private List<ThingStatusInfo> statusUpdates;

    @BeforeEach
    public void setUp() throws Exception {
        Path fixturesDir = Paths.get(getClass().getResource("/xml").toURI());

        ThingUID thingUID = new ThingUID(BomBindingConstants.THING_TYPE_WEATHER, "test");
        org.openhab.core.thing.internal.ThingImpl thingImpl = new org.openhab.core.thing.internal.ThingImpl(
                BomBindingConstants.THING_TYPE_WEATHER, thingUID);
        thingImpl.setConfiguration(new Configuration(new HashMap<>()));

        List<Channel> channels = new ArrayList<>();

        for (String groupId : new String[] { "day1", "day2", "day3", "day4" }) {
            for (String channelId : OBSERVATION_CHANNEL_IDS) {
                channels.add(channel(thingUID, groupId, channelId));
            }

            for (String channelId : FORECAST_CHANNEL_IDS) {
                channels.add(channel(thingUID, groupId, channelId));
            }
        }

        for (Channel channel : channels) {
            thingImpl.addChannel(channel);
        }

        thing = thingImpl;

        callback = mock(ThingHandlerCallback.class);
        updatedChannelUIDs = new ArrayList<>();
        updatedStates = new ArrayList<>();
        statusUpdates = new ArrayList<>();

        handler = new BomHandler(thing);
        handler.setCallback(callback);

        Mockito.doAnswer(invocation -> {
            updatedChannelUIDs.add(invocation.getArgument(0));
            updatedStates.add(invocation.getArgument(1));
            return null;
        }).when(callback).stateUpdated(any(ChannelUID.class), any(State.class));

        Mockito.doAnswer(invocation -> {
            statusUpdates.add(invocation.getArgument(1));
            return null;
        }).when(callback).statusUpdated(any(Thing.class), any(ThingStatusInfo.class));

        setConfig(defaultConfiguration(fixturesDir.toUri().toString()));
    }

    private Channel channel(ThingUID thingUID, String groupId, String channelId) {
        return ChannelBuilder.create(new ChannelUID(thingUID, groupId, channelId), "Number").build();
    }

    private BomConfiguration defaultConfiguration(String ftpPath) {
        BomConfiguration configuration = new BomConfiguration();
        configuration.ftpPath = ftpPath;
        configuration.observationProductId = "IDW60920";
        configuration.weatherStationId = "94608";
        configuration.precisForecastProductId = "IDW14199";
        configuration.cityTownForecastProductId = "IDW12300";
        configuration.areaId = "WA_PT053";
        return configuration;
    }

    private void setConfig(BomConfiguration configuration) throws Exception {
        Field field = BomHandler.class.getDeclaredField("config");
        field.setAccessible(true);
        field.set(handler, configuration);
    }

    private Map<String, State> collectUpdatedStates(String group) {
        Map<String, State> statesByKey = new HashMap<>();

        for (int i = 0; i < updatedChannelUIDs.size(); i++) {
            ChannelUID uid = updatedChannelUIDs.get(i);

            if (group == null || group.equals(uid.getGroupId())) {
                statesByKey.put(uid.getGroupId() + "." + uid.getIdWithoutGroup(), updatedStates.get(i));
            }
        }

        return statesByKey;
    }

    private void assertDecimal(Map<String, State> states, String key, double expected) {
        State state = states.get(key);

        assertNotNull(state, "no state was set for channel " + key);
        assertTrue(state instanceof DecimalType, "state for " + key + " is not numeric: " + state.getClass());
        assertEquals(expected, ((DecimalType) state).doubleValue(), 0.001);
    }

    private void assertString(Map<String, State> states, String key, String expected) {
        State state = states.get(key);

        assertNotNull(state, "no state was set for channel " + key);
        assertEquals(expected, ((StringType) state).toString());
    }

    private void assertDateTime(Map<String, State> states, String key, ZonedDateTime expected) {
        State state = states.get(key);

        assertNotNull(state, "no state was set for channel " + key);
        assertTrue(state instanceof DateTimeType, "state for " + key + " is not a DateTimeType");
        assertEquals(expected.toInstant(), ((DateTimeType) state).getZonedDateTime().toInstant());
    }

    @Test
    public void testRefreshObservationParsesStationDataAndGoesOnline() throws Exception {
        handler.refreshObservation();

        Map<String, State> states = collectUpdatedStates("day1");

        assertDecimal(states, "day1.airTemperature", 14.5);
        assertDecimal(states, "day1.apparentTemperature", 12.1);
        assertDecimal(states, "day1.dewPoint", 9.0);
        assertDecimal(states, "day1.relativeHumidity", 68.0);
        assertDecimal(states, "day1.pressure", 1018.2);
        assertString(states, "day1.windDirection", "SW");
        assertDecimal(states, "day1.windDirectionDegrees", 225.0);
        assertDecimal(states, "day1.windSpeedKmh", 15.0);
        assertDecimal(states, "day1.windSpeedKnots", 8.0);
        assertDecimal(states, "day1.rainfall", 0.2);
        assertDecimal(states, "day1.rainfall24Hour", 4.0);
        assertString(states, "day1.weatherStation", "PERTH METRO");
        assertDateTime(states, "day1.observationDateTime", ZonedDateTime.parse("2026-08-23T09:00:00+08:00"));

        assertFalse(statusUpdates.isEmpty());
        assertEquals(ThingStatus.ONLINE, statusUpdates.get(statusUpdates.size() - 1).getStatus());
    }

    @Test
    public void testRefreshForecastParsesPrecisAndCityForecasts() throws Exception {
        handler.refreshForecast();

        Map<String, State> day1 = collectUpdatedStates("day1");

        // precis forecast data (icon code 1 => sunny)
        assertDateTime(day1, "day1.dateTime", ZonedDateTime.parse("2026-08-23T10:00:00+08:00"));
        assertString(day1, "day1.icon", "sunny");
        assertString(day1, "day1.precis", "Sunny.");
        assertDecimal(day1, "day1.minTemperature", 9.0);
        assertDecimal(day1, "day1.maxTemperature", 19.0);
        assertString(day1, "day1.precipitation", "0%");
        assertDecimal(day1, "day1.minPrecipitation", 0.0);
        assertDecimal(day1, "day1.maxPrecipitation", 0.2);

        // city/town forecast text and UV alert
        assertString(day1, "day1.forecast", "Partly cloudy. The chance of a shower.");
        assertTrue(((StringType) day1.get("day1.uvAlert")).toString().contains("Sun protection"));

        // second and third days (icon codes 11 => shower, 4 => cloudy)
        assertString(collectUpdatedStates("day2"), "day2.icon", "shower");
        assertString(collectUpdatedStates("day2"), "day2.forecast", "Sunny afternoon.");
        assertString(collectUpdatedStates("day3"), "day3.icon", "cloudy");

        // only three periods exist in the fixture, so day4 must not receive updates
        assertTrue(collectUpdatedStates(null).keySet().stream().noneMatch(key -> key.startsWith("day4.")));
    }

    @Test
    public void testCityForecastFallsBackToParentAreaWhenTextIsMissing() throws Exception {
        BomConfiguration configuration = defaultConfiguration(
                Paths.get(getClass().getResource("/xml").toURI()).toUri().toString());
        configuration.areaId = "WA_FB777";
        setConfig(configuration);

        handler.refreshForecast();

        // WA_FB777 has no forecast text of its own; texts come from parent area WA_FA003
        assertString(collectUpdatedStates("day1"), "day1.forecast", "Parent forecast text day one.");
        assertString(collectUpdatedStates("day2"), "day2.forecast", "Parent forecast text day two.");
    }

    @Test
    public void testRetainMinMaxTemperatureBorrowsTodaysValuesFromTomorrow() throws Exception {
        BomConfiguration configuration = defaultConfiguration(
                Paths.get(getClass().getResource("/xml").toURI()).toUri().toString());
        configuration.precisForecastProductId = "IDW14198";
        configuration.cityTownForecastProductId = "";
        configuration.retainMinMaxTemperature = true;
        setConfig(configuration);

        handler.refreshPrecisForecast();

        Map<String, State> day1 = collectUpdatedStates("day1");

        // today has no min/max in the evening feed; they must be borrowed from tomorrow
        assertDecimal(day1, "day1.maxTemperature", 18.0);
        assertDecimal(day1, "day1.minTemperature", 7.0);
    }

    @Test
    public void testMissingRequiredConfigurationSetsThingOffline() throws Exception {
        BomConfiguration configuration = defaultConfiguration(
                Paths.get(getClass().getResource("/xml").toURI()).toUri().toString());
        configuration.weatherStationId = "";
        setConfig(configuration);

        handler.refreshObservation();

        assertTrue(updatedStates.isEmpty());
        assertFalse(statusUpdates.isEmpty());
        assertEquals(ThingStatus.OFFLINE, statusUpdates.get(statusUpdates.size() - 1).getStatus());
    }

    @Test
    public void testUnreachableProductSetsThingOffline() throws Exception {
        BomConfiguration configuration = defaultConfiguration(
                Paths.get(getClass().getResource("/xml").toURI()).toUri().toString());
        configuration.observationProductId = "IDW00000";
        setConfig(configuration);

        handler.refreshObservation();

        assertTrue(updatedStates.isEmpty());
        assertEquals(ThingStatus.OFFLINE, statusUpdates.get(statusUpdates.size() - 1).getStatus());
    }
}
