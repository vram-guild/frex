/*
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
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
