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
package org.openhab.binding.bom.internal;

import static org.openhab.binding.bom.internal.BomBindingConstants.THING_TYPE_WEATHER;
import static org.openhab.binding.bom.internal.BomImageBindingConstants.THING_TYPE_IMAGE;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link BomHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Thomas Tan - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.bom", service = ThingHandlerFactory.class)
public class BomHandlerFactory extends BaseThingHandlerFactory {
    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return THING_TYPE_WEATHER.equals(thingTypeUID) || THING_TYPE_IMAGE.equals(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_WEATHER.equals(thingTypeUID)) {
            return new BomHandler(thing);
        } else if (THING_TYPE_IMAGE.equals(thingTypeUID)) {
            return new BomImageHandler(thing);
        }

        return null;
    }
}
