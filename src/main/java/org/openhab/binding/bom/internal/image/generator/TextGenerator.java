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
package org.openhab.binding.bom.internal.image.generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.bom.internal.properties.Properties;

/**
 * The {@link TextGenerator} class draws text onto the image
 *
 * @author Thomas Tan - Initial contribution
 */
public class TextGenerator extends ImageGenerator {
    @Override
    public BufferedImage generate(int width, int height, Properties properties) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        String text = getString(properties.getValue("text"), "?");
        String hexColor = getString(properties.getValue("font-color"), "#000000");
        String fontFace = getString(properties.getValue("font-face"), "Arial");
        int fontSize = getInt(properties.getValue("font-size"), 20);
        String[] coord = parseParams(properties.getValue("position"));

        int posX = 0;
        int posY = 0;

        if (coord != null && coord.length == 2) {
            posX = getInt(coord[0], 0);
            posY = getInt(coord[1], 0);
        }

        Graphics graphics = bufferedImage.getGraphics();

        graphics.setColor(Color.decode(hexColor));
        graphics.setFont(getFont(new Font(fontFace, Font.PLAIN, fontSize), properties));

        graphics.drawString(text, posX, posY);

        return bufferedImage;
    }

    private Font getFont(Font font, Properties properties) {
        Map<TextAttribute, Object> attributes = new HashMap<>();

        if ("italic".equalsIgnoreCase(properties.getValue("font-style"))) {
            attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
        }

        if ("bold".equalsIgnoreCase(properties.getValue("font-weight"))) {
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        }

        if ("underline".equalsIgnoreCase(properties.getValue("text-decoration"))) {
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        }

        if (!attributes.isEmpty()) {
            return font.deriveFont(attributes);
        }

        return font;
    }

    private int getInt(String value, int defaultValue) {
        try {
            return StringUtils.isBlank(value) ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String getString(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
