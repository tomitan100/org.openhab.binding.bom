/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
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
 * The {@link BomImageConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomImageConfiguration {
    public String ftpServer;
    public String imagesPath;
    public String productId;
    public Integer monitoringInterval;
    public String transparenciesPath;
    public String layersConfiguration;

    public boolean generatePngs;
    public boolean generateGif;
    public Integer gifImageDelay;
    public boolean gifImageLoop;
    public String imagePostProcessing;
    public String imageOutputPath;
    public String imageOutputFilename;
}
