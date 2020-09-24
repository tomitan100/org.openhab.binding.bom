/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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

import java.time.ZonedDateTime;

/**
 * The {@link Forecast} class contains forecast values
 *
 * @author Thomas Tan - Initial contribution
 */
public class Forecast {
    private ZonedDateTime zonedDatetime;
    private String iconCode;
    private Double minTemperature;
    private Double maxTemperature;
    private String precis = "";
    private String precipitation = "";
    private Double minPrecipitation;
    private Double maxPrecipitation;

    public ZonedDateTime getZonedDatetime() {
        return zonedDatetime;
    }

    public void setZonedDatetime(ZonedDateTime zonedDatetime) {
        this.zonedDatetime = zonedDatetime;
    }

    public String getIconCode() {
        return iconCode;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getPrecis() {
        return precis;
    }

    public void setPrecis(String precis) {
        this.precis = precis;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public Double getMinPrecipitation() {
        return minPrecipitation;
    }

    public void setMinPrecipitation(Double minPrecipitation) {
        this.minPrecipitation = minPrecipitation;
    }

    public Double getMaxPrecipitation() {
        return maxPrecipitation;
    }

    public void setMaxPrecipitation(Double maxPrecipitation) {
        this.maxPrecipitation = maxPrecipitation;
    }
}
