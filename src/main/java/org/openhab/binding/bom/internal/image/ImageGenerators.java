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
package org.openhab.binding.bom.internal.image;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.bom.internal.Constants;
import org.openhab.binding.bom.internal.image.generator.ImageGenerator;
import org.openhab.binding.bom.internal.image.generator.TextGenerator;

/**
 * The {@link ImageGenerators} class contains available graphics generator.
 *
 * This class is source from the web, uncertain of who the author is as it is copied everywhere.
 *
 * @author Various
 */
public class ImageGenerators {
    private static final Map<String, ImageGenerator> generators = new HashMap<>();

    static {
        generators.put(Constants.PROP_KEY_TEXT, new TextGenerator());
    }

    public static ImageGenerator get(String name) {
        return generators.get(name);
    }
}
