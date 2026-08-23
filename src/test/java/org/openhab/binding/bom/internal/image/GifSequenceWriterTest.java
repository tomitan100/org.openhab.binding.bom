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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GifSequenceWriter}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class GifSequenceWriterTest {
    private BufferedImage frame(int size, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, size, size);
        graphics.dispose();

        return image;
    }

    @Test
    public void testWritesAnimatedGifWithAllFrames() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(outputStream);
        GifSequenceWriter writer = new GifSequenceWriter(imageOutputStream, BufferedImage.TYPE_BYTE_INDEXED, 1000,
                true);

        try {
            writer.writeToSequence(frame(32, Color.RED));
            writer.writeToSequence(frame(32, Color.GREEN));
            writer.writeToSequence(frame(32, Color.BLUE));
        } finally {
            writer.close();
            imageOutputStream.close();
        }

        byte[] gifBytes = outputStream.toByteArray();

        assertTrue(gifBytes.length > 0);
        assertEquals("GIF8", new String(gifBytes, 0, 4));

        try (ImageInputStream inputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(gifBytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);

            assertTrue(readers.hasNext());

            ImageReader reader = readers.next();
            reader.setInput(inputStream);

            assertEquals(3, reader.getNumImages(true));

            for (int i = 0; i < 3; i++) {
                BufferedImage frameImage = reader.read(i);

                assertEquals(32, frameImage.getWidth());
                assertEquals(32, frameImage.getHeight());
            }

            reader.dispose();
        }
    }
}
