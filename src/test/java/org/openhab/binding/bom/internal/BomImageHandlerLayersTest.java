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

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openhab.binding.bom.internal.image.ImageLayerGroup;
import org.openhab.binding.bom.internal.image.ImageLayerConfig;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.internal.ThingImpl;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.binding.bom.internal.BomImageBindingConstants;

/**
 * Tests for the layer configuration parsing logic of {@link BomImageHandler}.
 *
 * @author Thomas Tan - Initial contribution
 */
public class BomImageHandlerLayersTest {
    private BomImageHandler handler;

    @BeforeEach
    public void setUp() throws Exception {
        ThingTypeUID thingTypeUID = BomImageBindingConstants.THING_TYPE_IMAGE;
        ThingUID thingUID = new ThingUID(thingTypeUID, "radar");
        handler = new BomImageHandler(new ThingImpl(thingTypeUID, thingUID));

        BomImageConfiguration configuration = new BomImageConfiguration();
        configuration.productId = "IDR701";
        setPrivateField("config", configuration);
    }

    private void setPrivateField(String name, Object value) throws Exception {
        Field field = BomImageHandler.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(handler, value);
    }

    @SuppressWarnings("unchecked")
    private List<ImageLayerConfig> parse(String layersConfiguration) throws Exception {
        Method method = BomImageHandler.class.getDeclaredMethod("parseImageLayersProperties", String.class);
        method.setAccessible(true);

        return (List<ImageLayerConfig>) method.invoke(handler, layersConfiguration);
    }

    @Test
    public void testLayersBeforeSeriesAreBackgroundAndAfterAreForeground() throws Exception {
        List<ImageLayerConfig> layers = parse(
                "image=transparencies/border.png; image=${series}; image=logo.png");

        assertEquals(3, layers.size());

        assertEquals("transparencies/border.png", layers.get(0).getImagePath());
        assertEquals(ImageLayerGroup.BACKGROUND, layers.get(0).getLayerGroup());

        // ${series} is kept as-is and becomes the middleground series layer;
        // it is resolved to timestamped product files later during download
        assertEquals("${series}", layers.get(1).getImagePath());
        assertEquals(ImageLayerGroup.MIDDLEGROUND, layers.get(1).getLayerGroup());

        assertEquals("logo.png", layers.get(2).getImagePath());
        assertEquals(ImageLayerGroup.FOREGROUND, layers.get(2).getLayerGroup());
    }

    @Test
    public void testMultipleStaticLayersWithoutSeriesAreAllBackground() throws Exception {
        List<ImageLayerConfig> layers = parse("image=a.png; image=b.png");

        assertEquals(2, layers.size());
        assertEquals(ImageLayerGroup.BACKGROUND, layers.get(0).getLayerGroup());
        assertEquals(ImageLayerGroup.BACKGROUND, layers.get(1).getLayerGroup());
    }

    @Test
    public void testLayerPropertiesArePreserved() throws Exception {
        List<ImageLayerConfig> layers = parse("image=text.png, opacity=0.5, crop=0 0 512 512");

        assertEquals(1, layers.size());
        assertEquals("text.png", layers.get(0).getImagePath());
        assertEquals("0.5", layers.get(0).getProperties().getValue("opacity"));
        assertEquals("0 0 512 512", layers.get(0).getProperties().getValue("crop"));
    }

    @Test
    public void testEmptyConfigurationYieldsNoLayers() throws Exception {
        assertTrue(parse("").isEmpty());
    }
}
