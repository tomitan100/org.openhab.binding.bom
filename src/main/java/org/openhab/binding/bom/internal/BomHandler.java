/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bom.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The {@link BomHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(BomHandler.class);

    private final String[] WEATHER_ICON_MAP = { "sunny", "clear", "mostly-sunny", "cloudy", "unknown", "hazy",
            "light-rain", "unknown", "windy", "fog", "shower", "rain", "dusty", "frost", "snow", "storm",
            "light-shower", "heavy-shower", "cyclone" };

    @Nullable
    private ScheduledFuture<?> observationRefreshJob;

    @Nullable
    private ScheduledFuture<?> forecastRefreshJob;

    @Nullable
    private BomConfiguration config;

    public BomHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        config = getConfigAs(BomConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);

        startRefresh();
    }

    private void startRefresh() {
        observationRefreshJob = scheduler.scheduleWithFixedDelay(this::refreshObservation, 0,
                config.observationRefreshInterval, TimeUnit.MINUTES);
        forecastRefreshJob = scheduler.scheduleWithFixedDelay(this::refreshForecast, 0, config.forecastRefreshInterval,
                TimeUnit.MINUTES);
    }

    private void stopRefresh() {
        if (observationRefreshJob != null && !observationRefreshJob.isCancelled()) {
            logger.info("Cancelling observation refresh job");
            observationRefreshJob.cancel(true);
            observationRefreshJob = null;
        }

        if (forecastRefreshJob != null && !forecastRefreshJob.isCancelled()) {
            logger.info("Cancelling forecast refresh job");
            forecastRefreshJob.cancel(true);
            forecastRefreshJob = null;
        }
    }

    private void refreshObservation() {
        if (config.observationFtpPath == null || config.weatherStationId == null) {
            logger.warn("Observation FTP path or weather station ID is required");
            updateStatus(ThingStatus.UNINITIALIZED);
            return;
        }

        logger.debug("Processing observation data from FTP path: {}, weather station ID: {}", config.observationFtpPath,
                config.weatherStationId);

        InputStream inputStream = null;

        try {
            logger.debug("Retrieving observation data from " + config.observationFtpPath);

            URLConnection urlConnection = new URL(config.observationFtpPath).openConnection();
            inputStream = urlConnection.getInputStream();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(inputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String stationXPath = "/product/observations/station[@wmo-id='" + config.weatherStationId + "']";
            String observationDateTimeStr = getNodeAttribute(xmlDocument, xPath, stationXPath + "/period/@time-local");
            ZonedDateTime observationZonedDateTime = ZonedDateTime.parse(observationDateTimeStr,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            String elementXPath = stationXPath + "/period/level/element";

            Double minTemperature = getDouble(xmlDocument, xPath, elementXPath + "[@type='minimum_air_temperature']");
            Double maxTemperature = getDouble(xmlDocument, xPath, elementXPath + "[@type='maximum_air_temperature']");
            Double airTemperature = getDouble(xmlDocument, xPath, elementXPath + "[@type='air_temperature']");
            Double dewPoint = getDouble(xmlDocument, xPath, elementXPath + "[@type='dew_point']");
            Double relativeHumidity = getDouble(xmlDocument, xPath, elementXPath + "[@type='rel-humidity']");
            Double pressure = getDouble(xmlDocument, xPath, elementXPath + "[@type='pres']");
            String windDirection = getString(xmlDocument, xPath, elementXPath + "[@type='wind_dir']");
            Double windDirectionDegrees = getDouble(xmlDocument, xPath, elementXPath + "[@type='wind_dir_deg']");
            Double windSpeedKmh = getDouble(xmlDocument, xPath, elementXPath + "[@type='wind_spd_kmh']");
            Double windSpeedKnots = getDouble(xmlDocument, xPath, elementXPath + "[@type='wind_spd']");
            Double rainfall = getDouble(xmlDocument, xPath, elementXPath + "[@type='rainfall']");

            getThing().getChannelsOfGroup(BomBindingConstants.CHANNEL_GROUP_TODAY).stream().forEach(channel -> {
                switch (channel.getUID().getIdWithoutGroup()) {
                    case BomBindingConstants.CHANNEL_MIN_TEMPERATURE:
                        updateChannelState(channel.getUID(), minTemperature);
                        break;
                    case BomBindingConstants.CHANNEL_MAX_TEMPERATURE:
                        updateChannelState(channel.getUID(), maxTemperature);
                        break;
                    case BomBindingConstants.CHANNEL_AIR_TEMPERATURE:
                        updateChannelState(channel.getUID(), airTemperature);
                        break;
                    case BomBindingConstants.CHANNEL_DEW_POINT:
                        updateChannelState(channel.getUID(), dewPoint);
                        break;
                    case BomBindingConstants.CHANNEL_RELATIVE_HUMIDITY:
                        updateChannelState(channel.getUID(), relativeHumidity);
                        break;
                    case BomBindingConstants.CHANNEL_PRESSURE:
                        updateChannelState(channel.getUID(), pressure);
                        break;
                    case BomBindingConstants.CHANNEL_WIND_DIRECTION:
                        updateChannelState(channel.getUID(), windDirection);
                        break;
                    case BomBindingConstants.CHANNEL_WIND_DIRECTION_DEGREES:
                        updateChannelState(channel.getUID(), windDirectionDegrees);
                        break;
                    case BomBindingConstants.CHANNEL_WIND_SPEED_KMH:
                        updateChannelState(channel.getUID(), windSpeedKmh);
                        break;
                    case BomBindingConstants.CHANNEL_WIND_SPEED_KNOTS:
                        updateChannelState(channel.getUID(), windSpeedKnots);
                        break;
                    case BomBindingConstants.CHANNEL_RAINFALL:
                        updateChannelState(channel.getUID(), rainfall);
                        break;
                    case BomBindingConstants.CHANNEL_OBSERVATION_DATE_TIME:
                        updateChannelState(channel.getUID(), observationZonedDateTime);
                        break;
                }
            });

            updateStatus(ThingStatus.ONLINE);
        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            logger.error("Unable to process observation data", ex);
            updateStatus(ThingStatus.OFFLINE);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Unable to close input streram", e);
                }
            }
        }
    }

    private void refreshForecast() {
        if (config.forecastFtpPath == null || config.areaId == null) {
            logger.warn("Forecast FTP path or area ID is required");
            updateStatus(ThingStatus.UNINITIALIZED);
            return;
        }

        logger.info("Processing forecast data from FTP path: {}, area ID: {}", config.forecastFtpPath, config.areaId);

        InputStream inputStream = null;

        try {
            URLConnection urlConnection = new URL(config.forecastFtpPath).openConnection();
            inputStream = urlConnection.getInputStream();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(inputStream);
            XPath xPath = XPathFactory.newInstance().newXPath();

            String areaXPath = "/product/forecast/area[@aac='" + config.areaId + "']";
            Node areaNode = (Node) xPath.compile(areaXPath).evaluate(xmlDocument, XPathConstants.NODE);

            String parentAreaCode = areaNode.getAttributes().getNamedItem("parent-aac").getNodeValue();
            String parentAreaXPath = "/product/forecast/area[@aac='" + parentAreaCode + "']";

            NodeList nodes = (NodeList) xPath.compile(areaXPath + "/forecast-period").evaluate(xmlDocument,
                    XPathConstants.NODESET);

            if (nodes != null && nodes.getLength() > 0) {
                int idx = 0;
                for (int i = 0; i < nodes.getLength() && idx < BomBindingConstants.NUMBER_OF_FORECASTS; i++) {
                    Node node = nodes.item(i);

                    if (node.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    String dateStr = node.getAttributes().getNamedItem("start-time-local").getNodeValue();
                    ZonedDateTime zonedDatetime = ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                    String iconCode = null;
                    Double minTemperature = null;
                    Double maxTemperature = null;
                    String precis = "";
                    String precipitation = "";
                    String uvAlert = "N/A";
                    String forecast = getString(xmlDocument, xPath,
                            parentAreaXPath + "/forecast-period[@index='" + idx + "']/text[@type='forecast']");

                    for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                        Node childNode = node.getChildNodes().item(j);

                        if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }

                        String type = childNode.getAttributes().getNamedItem("type").getNodeValue();

                        switch (type) {
                            case "forecast_icon_code":
                                iconCode = childNode.getTextContent();
                            case "precis":
                                precis = childNode.getTextContent();
                                break;
                            case "air_temperature_minimum":
                                minTemperature = Double.parseDouble(childNode.getTextContent());
                                break;
                            case "air_temperature_maximum":
                                maxTemperature = Double.parseDouble(childNode.getTextContent());
                                break;
                            case "probability_of_precipitation":
                                precipitation = childNode.getTextContent();
                                break;
                            case "uv_alert":
                                uvAlert = childNode.getTextContent();
                                break;
                        }
                    }

                    updateForecastState(BomBindingConstants.CHANNEL_GROUP_DAY_PREFIX + (idx + 1), zonedDatetime,
                            iconCode, precis, forecast, minTemperature, maxTemperature, precipitation, uvAlert);

                    idx++;
                }

                logger.info("Completed processing forecast data.");
            }
        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException ex) {
            logger.error("Unable to process forecast data", ex);
            updateStatus(ThingStatus.OFFLINE);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Unable to close input streram", e);
                }
            }
        }
    }

    private void updateForecastState(String channelGroupId, ZonedDateTime zonedDateTime, String iconCode, String precis,
            String forecast, Double minTemperature, Double maxTemperature, String precipitation, String uvAlert) {

        getThing().getChannelsOfGroup(channelGroupId).stream().forEach(channel -> {
            switch (channel.getUID().getIdWithoutGroup()) {
                case BomBindingConstants.CHANNEL_ICON:
                    if (iconCode != null) {
                        int iconIdx = Integer.parseInt(iconCode) - 1;

                        if (iconIdx >= 0 && iconIdx < WEATHER_ICON_MAP.length) {
                            updateState(channel.getUID(), new StringType(WEATHER_ICON_MAP[iconIdx]));
                        }
                    }
                    break;
                case BomBindingConstants.CHANNEL_DATE_TIME:
                    updateChannelState(channel.getUID(), zonedDateTime);
                    break;
                case BomBindingConstants.CHANNEL_PRECIS:
                    updateChannelState(channel.getUID(), precis);
                    break;
                case BomBindingConstants.CHANNEL_MIN_TEMPERATURE:
                    updateChannelState(channel.getUID(), minTemperature);
                    break;
                case BomBindingConstants.CHANNEL_MAX_TEMPERATURE:
                    updateChannelState(channel.getUID(), maxTemperature);
                    break;
                case BomBindingConstants.CHANNEL_PRECIPITATION:
                    updateChannelState(channel.getUID(), precipitation);
                    break;
                case BomBindingConstants.CHANNEL_FORECAST:
                    updateChannelState(channel.getUID(), forecast);
                    break;
                case BomBindingConstants.CHANNEL_UV_ALERT:
                    updateChannelState(channel.getUID(), uvAlert);
                    break;
            }
        });
    }

    private void updateChannelState(ChannelUID uid, String value) {
        if (value != null) {
            updateState(uid, new StringType(value));
        }
    }

    private void updateChannelState(ChannelUID uid, Double value) {
        if (value != null) {
            updateState(uid, new DecimalType(value));
        }
    }

    private void updateChannelState(ChannelUID uid, ZonedDateTime dateTime) {
        if (dateTime != null) {
            updateState(uid, new DateTimeType(dateTime));
        }
    }

    private Double getDouble(Document document, XPath xPath, String path) throws XPathExpressionException {
        return (Double) xPath.compile(path + "/text()").evaluate(document, XPathConstants.NUMBER);
    }

    private String getString(Document document, XPath xPath, String path) throws XPathExpressionException {
        return (String) xPath.compile(path + "/text()").evaluate(document, XPathConstants.STRING);
    }

    private String getNodeAttribute(Document document, XPath xPath, String path) throws XPathExpressionException {
        return xPath.compile(path).evaluate(document);
    }
}
