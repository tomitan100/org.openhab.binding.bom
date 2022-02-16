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
package org.openhab.binding.bom.internal.net;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;

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

    protected boolean timestampMatches(FTPFile ftpFile) {
        Matcher matcher = Constants.FILE_DATE_TIME_PATTERN.matcher(ftpFile.getName());

        if (matcher.matches()) {
            LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1), Constants.FILE_DATE_TIME_FORMATTER);
            ZonedDateTime sourceTimestamp = localDateTime.atZone(Constants.ZONE_ID_UTC);

            return !(sourceTimestamp.isBefore(dateTimeRange.getStartDateTime())
                    || sourceTimestamp.isAfter(dateTimeRange.getEndDateTime()));
        }

        return false;
    }

    @Override
    public boolean accept(FTPFile ftpFile) {
        return filenameMatches(ftpFile) && timestampMatches(ftpFile);
    }
}
