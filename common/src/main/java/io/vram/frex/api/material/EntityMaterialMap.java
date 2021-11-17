/*
 * Copyright © Original Authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional copyright and licensing notices may apply for content that was
 * included from other projects. For more information, see ATTRIBUTION.md.
 */

package io.vram.frex.api.material;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import io.vram.frex.impl.material.MaterialMapLoader;

/**
 * Transforms materials for entities.
 * Requires FREX material extensions.
 */
@FunctionalInterface
public interface EntityMaterialMap {
	RenderMaterial getMapped(RenderMaterial material, Entity entity, MaterialFinder finder);

	static EntityMaterialMap get(EntityType<?> entityType) {
		return MaterialMapLoader.INSTANCE.get(entityType);
	}

	EntityMaterialMap IDENTITY = (m, e, f) -> m;
}
