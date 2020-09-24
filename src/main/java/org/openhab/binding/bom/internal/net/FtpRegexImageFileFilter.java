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
package org.openhab.binding.bom.internal.net;

import org.apache.commons.net.ftp.FTPFile;
import org.openhab.binding.bom.internal.DateTimeRange;

/**
 * The {@link FtpRegexImageFileFilter} class defines the FTP image filter that uses regex as filter.
 *
 * @author Thomas Tan - Initial contribution
 */
public class FtpRegexImageFileFilter extends FtpImageFileFilter {
    public FtpRegexImageFileFilter(String filenamePattern, DateTimeRange dateTimeRange) {
        super(filenamePattern, dateTimeRange);
    }

    @Override
    protected boolean filenameMatches(FTPFile ftpFile) {
        return !ftpFile.getName().contains(".gif") && ftpFile.getName().matches(filenamePattern);
    }
}
