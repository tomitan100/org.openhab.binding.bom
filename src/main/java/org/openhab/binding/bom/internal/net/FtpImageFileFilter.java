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

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

/**
 * The {@link FtpImageFileFilter} class defines the FTP image filter.
 *
 * @author Thomas Tan - Initial contribution
 */
public class FtpImageFileFilter implements FTPFileFilter {
    private String filePrefix;

    public FtpImageFileFilter(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    @Override
    public boolean accept(FTPFile ftpFile) {
        return ftpFile.getName().indexOf(filePrefix) == 0 && !ftpFile.getName().equals(filePrefix + ".gif");
    }
}
