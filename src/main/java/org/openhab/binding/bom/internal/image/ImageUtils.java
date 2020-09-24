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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ImageUtils} class contains a collection of image manipulation utilities
 *
 * @author Thomas Tan - Initial contribution
 */
public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    private static final float softenFactor = 0.01f;
    private static final float[] softenArray = { 0, softenFactor, 0, softenFactor, 1 - (softenFactor * 4), softenFactor,
            0, softenFactor, 0 };

    public static BufferedImage loadImage(String path) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new FileInputStream(path));
        } catch (Exception ex) {
            logger.error("Unable to load image \"{}\"", path, ex);
        }

        return img;
    }

    public static BufferedImage createTranslucentImage(BufferedImage srcImage, float alpha) {
        BufferedImage fxImg = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), Transparency.TRANSLUCENT);

        Graphics2D fxg = fxImg.createGraphics();

        // Set the Graphics composite to Alpha
        fxg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Draw the LOADED img into the prepared receiver image
        fxg.drawImage(srcImage, null, 0, 0);

        fxg.dispose();

        return fxImg;
    }

    public static BufferedImage processTransparency(BufferedImage srcImage, Color transparentColor) {
        Graphics2D g = srcImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(srcImage, null, 0, 0);
        g.dispose();

        for (int i = 0; i < srcImage.getHeight(); i++) {
            for (int j = 0; j < srcImage.getWidth(); j++) {
                if (srcImage.getRGB(j, i) == transparentColor.getRGB()) {
                    srcImage.setRGB(j, i, 0x8F1C1C);
                }
            }
        }

        return srcImage;
    }

    public static BufferedImage flipHorizontal(BufferedImage srcImage) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();

        g.drawImage(srcImage, 0, 0, width, height, width, 0, 0, height, null);
        g.dispose();

        return bufferedImage;
    }

    public static BufferedImage flipVertical(BufferedImage srcImage) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, srcImage.getColorModel().getTransparency());
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(srcImage, 0, 0, width, height, 0, height, width, 0, null);
        g.dispose();

        return bufferedImage;
    }

    public static BufferedImage rotate(BufferedImage srcImage, int angle) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.rotate(Math.toRadians(angle), width / 2, height / 2);
        g.drawImage(srcImage, null, 0, 0);

        return bufferedImage;
    }

    public static BufferedImage resizeImage(BufferedImage srcImage, int newWidth, int newHeight) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage bufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(srcImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
        g.dispose();

        return bufferedImage;
    }

    public static BufferedImage resizeImageHighQuality(BufferedImage img, int scaledWidth, int scaledHeight) {
        Image resizedImage = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        // This code ensures that all the pixels in the image are loaded.
        Image temp = new ImageIcon(resizedImage).getImage();

        // Create the buffered image.
        BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Copy image to buffered image.
        Graphics g = bufferedImage.createGraphics();

        // Clear background and paint the image.
        g.setColor(Color.white);
        g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
        g.drawImage(temp, 0, 0, null);
        g.dispose();

        Kernel kernel = new Kernel(3, 3, softenArray);
        ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        bufferedImage = cOp.filter(bufferedImage, null);

        return bufferedImage;
    }

    public static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        BufferedImage bufferedImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);

        Graphics g = bufferedImage.createGraphics();
        g.drawImage(src, 0, 0, rect.width, rect.height, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
                null);
        g.dispose();

        return bufferedImage;
    }

    public static BufferedImage transform(BufferedImage src, int x, int y) {
        int width = src.getWidth() + x;
        int height = src.getHeight() + y;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(src, x, y, null);

        return bufferedImage;
    }

    public static BufferedImage transform(BufferedImage src, int width, int height, int x, int y) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(src, x, y, null);

        return bufferedImage;
    }

    public static BufferedImage resizeCanvas(BufferedImage src, int width, int height, int x, int y, Color bgColor) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = bufferedImage.createGraphics();

        if (bgColor != null) {
            g.setColor(bgColor);
            g.fillRect(0, 0, width, height);
        }

        g.drawImage(src, x, y, null);
        g.dispose();

        return bufferedImage;
    }

    public static byte[] toByteArray(BufferedImage image, String format) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] imageByteArray = null;

        try {
            ImageIO.write(image, format, os);
            os.flush();
            imageByteArray = os.toByteArray();
        } catch (IOException ex) {
            logger.error("Unable to convert to byte array", ex);
        } finally {
            try {
                os.close();
            } catch (IOException ex) {
                logger.error("Unable to close output stream", ex);
            }
        }

        return imageByteArray;
    }

    public static BufferedImage merge(BufferedImage img1, BufferedImage img2) {
        int w = Math.max(img1.getWidth(), img2.getWidth());
        int h = Math.max(img1.getHeight(), img2.getHeight());
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, 0, 0, null);

        return combined;
    }

    public static BufferedImage copy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);

        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
