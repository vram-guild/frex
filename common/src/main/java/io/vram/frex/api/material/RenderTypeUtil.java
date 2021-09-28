/*
 * Copyright © Contributing Authors
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

import net.minecraft.client.renderer.RenderType;

import io.vram.frex.impl.material.RenderTypeUtilImpl;

/**
 * Use for mapping vanilla render types to render materials.
 * Should be reliable for vanilla materials used in terrain and
 * most others, depending on material features implemented by the
 * renderer. Custom render type that use custom shards cannot be
 * supported because there is no practical way to inspect what GL
 * state a custom shard controls.
 */
public interface RenderTypeUtil {
	/**
	 * Attempts to populate the material finder with attributes that duplicate
	 * the given vanilla render type.
	 *
	 * @param finder Material finder instance to be changed.
	 * @param renderType Vanilla render type to replicate.
	 * @return {@code true} if successful, {@code false} if the render type has
	 * attributes that cannot be inspected or cannot be handled by the current renderer.
	 */
	static boolean toMaterialFinder(MaterialFinder finder, RenderType renderType) {
		return RenderTypeUtilImpl.toMaterialFinder(finder, renderType);
	}

	/**
	 * Attempts to find or create a render material that duplicates the given
	 * vanilla render type.
	 *
	 * @param renderType Vanilla render type to replicate.
	 * @param foilOverlay Adds enchantment foil overlay if true.
	 * @return Corresponding render material, or the material registered to
	 *  {@link RenderMaterial#MISSING_MATERIAL_KEY} if this is not possible.
	 */
	static RenderMaterial toMaterial(RenderType renderType, boolean foilOverlay) {
		return RenderTypeUtilImpl.toMaterial(renderType, foilOverlay);
	}
}
