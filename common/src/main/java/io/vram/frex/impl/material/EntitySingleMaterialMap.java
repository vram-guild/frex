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

package io.vram.frex.impl.material;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.world.entity.Entity;

import io.vram.frex.api.material.EntityMaterialMap;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;

@Internal
class EntitySingleMaterialMap implements EntityMaterialMap {
	private final MaterialTransform transform;
	private final BiPredicate<Entity, RenderMaterial> predicate;

	EntitySingleMaterialMap(BiPredicate<Entity, RenderMaterial> predicate, MaterialTransform transform) {
		this.predicate = predicate;
		this.transform = transform;
	}

	@Override
	public RenderMaterial getMapped(RenderMaterial material, Entity entity, MaterialFinder finder) {
		return predicate.test(entity, material) ? transform.transform(material, finder) : material;
	}
}
