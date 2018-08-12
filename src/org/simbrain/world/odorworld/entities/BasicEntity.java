/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.world.odorworld.entities;

import org.simbrain.world.odorworld.OdorWorld;

/**
 * <b>BasicEntity</b> represents a static entity in the Odor World environment,
 * with a static image file. (TODO: Improve doc)
 */
public class BasicEntity extends OdorWorldEntity {

    /**
     * Construct a default entity.
     *
     * @param world
     */
    public BasicEntity(final OdorWorld world) {
        super(world);
    }

    /**
     * Construct a basic entity with a single image location.
     *
     * @param type image location
     * @param world
     */
    public BasicEntity(final EntityType type, final OdorWorld world) {
        super(world);
        setEntityType(type);
    }

    @Override
    public void update() {
    }

}
