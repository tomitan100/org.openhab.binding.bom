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
package org.openhab.binding.bom.internal.image;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;
import org.openhab.binding.bom.internal.properties.Properties;

/**
 * Unit tests for {@link ImageProcessors} and the individual image processors.
 *
 * @author Thomas Tan - Initial contribution
 */
public class ImageProcessorsTest {
    private BufferedImage solidImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();

        return image;
    }

    private static void assertColorEquals(Color expected, int rgb) {
        assertEquals(expected.getRGB() & 0x00FFFFFF, rgb & 0x00FFFFFF);
    }

    @Test
    public void testResizeOperation() {
        BufferedImage result = ImageProcessors.process(solidImage(4, 4, Color.RED),
                Properties.create("resize=8 12"));

        assertEquals(8, result.getWidth());
        assertEquals(12, result.getHeight());
    }

    @Test
    public void testCropOperation() {
        BufferedImage source = solidImage(6, 6, Color.RED);
        Graphics2D graphics = source.createGraphics();
        graphics.setColor(Color.GREEN);
        graphics.fillRect(3, 3, 3, 3);
        graphics.dispose();

        BufferedImage result = ImageProcessors.process(source, Properties.create("crop=2 2 4 4"));

        assertEquals(4, result.getWidth());
        assertEquals(4, result.getHeight());
        assertColorEquals(Color.GREEN, result.getRGB(1, 1));
    }

    @Test
    public void testPositionOperation() {
        BufferedImage result = ImageProcessors.process(solidImage(4, 4, Color.BLUE),
                Properties.create("position=3 5"));

        assertEquals(7, result.getWidth());
        assertEquals(9, result.getHeight());
        assertColorEquals(Color.BLUE, result.getRGB(3, 5));
    }

    @Test
    public void testCanvasOperationWithBackground() {
        BufferedImage result = ImageProcessors.process(solidImage(4, 4, Color.RED),
                Properties.create("canvas=10 10 1 1 #00FF00"));

        assertEquals(10, result.getWidth());
        assertEquals(10, result.getHeight());
        assertColorEquals(Color.GREEN, result.getRGB(0, 0));
        assertColorEquals(Color.RED, result.getRGB(1, 1));
    }

    @Test
    public void testOpacityOperation() {
        BufferedImage result = ImageProcessors.process(solidImage(4, 4, Color.RED),
                Properties.create("opacity=0.5"));

        int alpha = result.getRGB(1, 1) >>> 24;

        assertTrue(alpha > 100 && alpha < 155, "expected alpha around 127 but was " + alpha);
    }

    @Test
    public void testChainedOperationsAreAppliedInOrder() {
        BufferedImage result = ImageProcessors.process(solidImage(4, 4, Color.RED),
                Properties.create("opacity=0.5, resize=8 8"));

        assertEquals(8, result.getWidth());
        assertEquals(8, result.getHeight());

        int alpha = result.getRGB(4, 4) >>> 24;

        assertTrue(alpha > 100 && alpha < 155, "expected translucent pixel but was " + alpha);
    }

    @Test
    public void testUnknownOperationReturnsNull() {
        assertNull(ImageProcessors.process(solidImage(4, 4, Color.RED), "unknown_op", "1 2"));
    }
}
