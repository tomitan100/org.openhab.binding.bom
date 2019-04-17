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
package org.openhab.binding.bom.internal.net;

import java.time.ZonedDateTime;
import java.util.Calendar;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.openhab.binding.bom.internal.Constants;
import org.openhab.binding.bom.internal.DateTimeRange;

/**
 * The {@link FtpImageFileFilter} class defines the FTP image filter.
 *
 * @author Thomas Tan - Initial contribution
 */
public class FtpImageFileFilter implements FTPFileFilter {
    protected String filenamePattern;
    protected DateTimeRange dateTimeRange;

    public FtpImageFileFilter(String filenamePattern, DateTimeRange dateTimeRange) {
        this.filenamePattern = filenamePattern;
        this.dateTimeRange = dateTimeRange;
    }

    protected boolean filenameMatches(FTPFile ftpFile) {
        return !ftpFile.getName().contains(".gif") && ftpFile.getName().indexOf(filenamePattern) == 0;
    }

    @Override
    public boolean accept(FTPFile ftpFile) {
        if (filenameMatches(ftpFile)) {
            ZonedDateTime sourceTimestamp = ZonedDateTime.of(ftpFile.getTimestamp().get(Calendar.YEAR),
                    ftpFile.getTimestamp().get(Calendar.MONTH) + 1, ftpFile.getTimestamp().get(Calendar.DAY_OF_MONTH),
                    ftpFile.getTimestamp().get(Calendar.HOUR_OF_DAY), ftpFile.getTimestamp().get(Calendar.MINUTE),
                    ftpFile.getTimestamp().get(Calendar.SECOND), 0, Constants.ZONE_ID_UTC);

            return !(sourceTimestamp.isBefore(dateTimeRange.getStartDateTime())
                    || sourceTimestamp.isAfter(dateTimeRange.getEndDateTime()));
        }

        return false;
    }
}
