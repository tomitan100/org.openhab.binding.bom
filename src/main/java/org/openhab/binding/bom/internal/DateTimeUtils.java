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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DateTimeUtils} class defines utility methods for date/time.
 *
 * @author Thomas Tan - Initial contribution
 */
public class DateTimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);

    private static final Pattern DURATION_PATTERN = Pattern.compile("(-?\\d+)([dhms])");

    public static Long parseDuration(String duration) {
        Matcher matcher = DURATION_PATTERN.matcher(duration.trim());

        if (matcher.matches()) {
            try {
                Long timeValue = Long.parseLong(matcher.group(1));
                String timeUnit = matcher.group(2);

                switch (timeUnit) {
                    case "d":
                        return timeValue * 60L * 60L * 24L;
                    case "h":
                        return timeValue * 60L * 60L;
                    case "m":
                        return timeValue * 60L;
                    case "s":
                        return timeValue;
                    default:
                        logger.info("Invalid duration \"{}\"", duration);
                        return null;
                }
            } catch (NumberFormatException ex) {
                logger.info("Invalid duration \"{}\"", duration);
            }
        }

        return null;
    }
}
