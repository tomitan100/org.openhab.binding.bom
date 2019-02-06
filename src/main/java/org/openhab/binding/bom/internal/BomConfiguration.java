/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bom.internal;

/**
 * The {@link BomConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomConfiguration {
    public String observationFtpPath;

    public String weatherStationId;

    public Integer observationRefreshInterval;

    public String forecastFtpPath;

    public String areaId;

    public Integer forecastRefreshInterval;
}
