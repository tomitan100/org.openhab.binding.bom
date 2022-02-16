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

/**
 * The {@link BomConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomConfiguration {
    public String ftpPath;

    public String observationProductId;

    public String weatherStationId;

    public String precisForecastProductId;

    public String areaId;

    public String cityTownForecastProductId;

    public Integer observationRefreshInterval;

    public Integer forecastRefreshInterval;

    public boolean retainMinMaxTemperature;
}
