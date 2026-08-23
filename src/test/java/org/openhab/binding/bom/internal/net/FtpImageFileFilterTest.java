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

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.Calendar;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.jupiter.api.Test;
import org.openhab.binding.bom.internal.DateTimeRange;

/**
 * Unit tests for {@link FtpImageFileFilter} and {@link FtpRegexImageFileFilter}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class FtpImageFileFilterTest {
    private static final DateTimeRange RANGE = new DateTimeRange(
            ZonedDateTime.parse("2026-08-22T00:00:00Z"), ZonedDateTime.parse("2026-08-23T23:59:59Z"));

    private FTPFile ftpFile(String name, int year, int month, int day, int hour, int minute) {
        FTPFile ftpFile = new FTPFile();
        ftpFile.setName(name);
        ftpFile.setType(FTPFile.FILE_TYPE);

        Calendar timestamp = Calendar.getInstance();
        timestamp.set(year, month - 1, day, hour, minute, 0);
        ftpFile.setTimestamp(timestamp);

        return ftpFile;
    }

    @Test
    public void testAcceptsFileWithPrefixAndTimestampInRange() {
        FtpImageFileFilter filter = new FtpImageFileFilter("IDR701", RANGE);

        assertTrue(filter.accept(ftpFile("IDR701.202608230900.png", 2026, 8, 23, 9, 0)));
    }

    @Test
    public void testRejectsFileOutsideTimeRange() {
        FtpImageFileFilter filter = new FtpImageFileFilter("IDR701", RANGE);

        assertFalse(filter.accept(ftpFile("IDR701.202608010900.png", 2026, 8, 1, 9, 0)));
    }

    @Test
    public void testRejectsFileWithoutTimestamp() {
        FtpImageFileFilter filter = new FtpImageFileFilter("IDR701", RANGE);

        assertFalse(filter.accept(ftpFile("IDR701.latest.png", 2026, 8, 23, 9, 0)));
    }

    @Test
    public void testRejectsWrongPrefix() {
        FtpImageFileFilter filter = new FtpImageFileFilter("IDR701", RANGE);

        // prefix must match at position zero
        assertFalse(filter.accept(ftpFile("xIDR701.202608230900.png", 2026, 8, 23, 9, 0)));

        FTPFile other = ftpFile("IDR802.202608230900.png", 2026, 8, 23, 9, 0);
        assertFalse(filter.accept(other));
    }

    @Test
    public void testRejectsGifFiles() {
        FtpImageFileFilter filter = new FtpImageFileFilter("IDR701", RANGE);

        assertFalse(filter.accept(ftpFile("IDR701.202608230900.gif", 2026, 8, 23, 9, 0)));
    }

    @Test
    public void testRegexFilterMatchesFullPattern() {
        FtpRegexImageFileFilter filter = new FtpRegexImageFileFilter("IDE00135\\.\\d{12}\\.TIR\\..*", RANGE);

        assertTrue(filter.accept(ftpFile("IDE00135.202608230900.TIR.png", 2026, 8, 23, 9, 0)));
        assertFalse(filter.accept(ftpFile("IDE00135.radar.quads.jpg", 2026, 8, 23, 9, 0)));
        assertFalse(filter.accept(ftpFile("IDE00136.202608230900.TIR.png", 2026, 8, 23, 9, 0)));
        assertFalse(filter.accept(ftpFile("IDE00135.202608230900.TIR.gif", 2026, 8, 23, 9, 0)));
    }

    @Test
    public void testRangeBoundaryIsInclusive() {
        DateTimeRange range = new DateTimeRange(ZonedDateTime.parse("2026-08-23T09:00:00Z"),
                ZonedDateTime.parse("2026-08-23T09:30:00Z"));
        FtpImageFileFilter filter = new FtpImageFileFilter("IDR701", range);

        assertTrue(filter.accept(ftpFile("IDR701.202608230900.png", 2026, 8, 23, 9, 0)));
        assertTrue(filter.accept(ftpFile("IDR701.202608230930.png", 2026, 8, 23, 9, 30)));
        assertFalse(filter.accept(ftpFile("IDR701.202608230931.png", 2026, 8, 23, 9, 31)));
    }
}
