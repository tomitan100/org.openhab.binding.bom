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

import java.util.Comparator;

import org.apache.commons.net.ftp.FTPFile;

/**
 * The {@link FtpFileComparator} class defines sorting comparator.
 *
 * @author Thomas Tan - Initial contribution
 */
public class FtpFileComparator {
    public static final Comparator<FTPFile> SORT_BY_TIMESTAMP_ASC = new Comparator<FTPFile>() {
        @Override
        public int compare(FTPFile file1, FTPFile file2) {
            return file1.getTimestamp().compareTo(file2.getTimestamp());
        }
    };
}
