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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ImageUtils}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class ImageUtilsTest {
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
    public void testMergeUsesLargestDimensionsAndDrawsSecondImageOnTop() {
        BufferedImage blue = solidImage(6, 6, Color.BLUE);
        BufferedImage red = solidImage(4, 4, Color.RED);

        BufferedImage merged = ImageUtils.merge(blue, red);

        assertEquals(6, merged.getWidth());
        assertEquals(6, merged.getHeight());
        // the second image is drawn on top of the first one
        assertColorEquals(Color.RED, merged.getRGB(0, 0));
        assertColorEquals(Color.BLUE, merged.getRGB(5, 5));
    }

    @Test
    public void testResizeImage() {
        BufferedImage resized = ImageUtils.resizeImage(solidImage(4, 4, Color.RED), 8, 12);

        assertEquals(8, resized.getWidth());
        assertEquals(12, resized.getHeight());
        assertColorEquals(Color.RED, resized.getRGB(4, 6));
    }

    @Test
    public void testCropImage() {
        BufferedImage source = solidImage(6, 6, Color.RED);
        Graphics2D graphics = source.createGraphics();
        graphics.setColor(Color.GREEN);
        graphics.fillRect(3, 3, 3, 3);
        graphics.dispose();

        BufferedImage cropped = ImageUtils.cropImage(source, new Rectangle(2, 2, 4, 4));

        assertEquals(4, cropped.getWidth());
        assertEquals(4, cropped.getHeight());
        assertColorEquals(Color.RED, cropped.getRGB(0, 0));
        assertColorEquals(Color.GREEN, cropped.getRGB(2, 2));
    }

    @Test
    public void testTransformShiftsContent() {
        BufferedImage source = solidImage(4, 4, Color.MAGENTA);

        BufferedImage transformed = ImageUtils.transform(source, 2, 3);

        assertEquals(6, transformed.getWidth());
        assertEquals(7, transformed.getHeight());
        assertColorEquals(Color.MAGENTA, transformed.getRGB(2, 3));

        // area outside the shifted content stays transparent
        assertEquals(0, transformed.getRGB(0, 0) >>> 24);
    }

    @Test
    public void testResizeCanvasWithBackground() {
        BufferedImage resizedCanvas = ImageUtils.resizeCanvas(solidImage(4, 4, Color.RED), 10, 10, 3, 3,
                Color.BLACK);

        assertEquals(10, resizedCanvas.getWidth());
        assertEquals(10, resizedCanvas.getHeight());
        assertColorEquals(Color.BLACK, resizedCanvas.getRGB(0, 0));
        assertColorEquals(Color.RED, resizedCanvas.getRGB(3, 3));
    }

    @Test
    public void testCreateTranslucentImageReducesAlpha() {
        BufferedImage translucent = ImageUtils.createTranslucentImage(solidImage(4, 4, Color.RED), 0.5f);

        int alpha = translucent.getRGB(1, 1) >>> 24;

        assertTrue(alpha > 100 && alpha < 155, "expected alpha around 127 but was " + alpha);
    }

    @Test
    public void testCopyIsIndependent() {
        BufferedImage source = solidImage(4, 4, Color.RED);
        BufferedImage copy = ImageUtils.copy(source);

        assertEquals(source.getWidth(), copy.getWidth());
        assertEquals(source.getHeight(), copy.getHeight());
        assertNotSame(source, copy);
        assertColorEquals(Color.RED, copy.getRGB(2, 2));

        Graphics2D graphics = copy.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(2, 2, 1, 1);
        graphics.dispose();

        assertColorEquals(Color.RED, source.getRGB(2, 2));
    }
}
