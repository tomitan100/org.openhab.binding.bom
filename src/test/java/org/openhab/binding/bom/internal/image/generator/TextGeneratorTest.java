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
package org.openhab.binding.bom.internal.image.generator;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;
import org.openhab.binding.bom.internal.properties.Properties;

/**
 * Unit tests for {@link TextGenerator}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class TextGeneratorTest {
    private static boolean hasPixelMatching(BufferedImage image, IntPredicate predicate) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (predicate.test(image.getRGB(x, y))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Test
    public void testGenerateDrawsTextInRequestedColor() {
        TextGenerator generator = new TextGenerator();

        BufferedImage image = generator.generate(200, 60,
                Properties.create("text=PERTH, font-color=#FF0000, position=10 40, font-size=24"));

        assertEquals(200, image.getWidth());
        assertEquals(60, image.getHeight());

        assertTrue(hasPixelMatching(image, rgb -> (rgb >>> 24) > 0 && (rgb & 0x00FF0000) > 0xC00000),
                "expected at least one opaque red pixel from rendered text");
    }

    @Test
    public void testGenerateWithoutPropertiesStillProducesImage() {
        BufferedImage image = new TextGenerator().generate(50, 50, Properties.create(""));

        assertEquals(50, image.getWidth());
        assertEquals(50, image.getHeight());
    }
}
