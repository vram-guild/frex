/*
 * This file is part of FREX and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
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
 */

package io.vram.frex.api.rendertype;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.impl.material.RenderTypeUtilImpl;

/**
 * Use for mapping vanilla render types to render materials.
 * Should be reliable for vanilla materials used in terrain and
 * most others, depending on material features implemented by the
 * renderer. Custom render type that use custom shards cannot be
 * supported because there is no practical way to inspect what GL
 * state a custom shard controls.
 */
@NonExtendable
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

	static RenderMaterial toMaterial(RenderType renderType) {
		return toMaterial(renderType, false);
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

	static int inferPreset(RenderType layer) {
		final var compositeState = ((CompositeRenderType) layer).state;

		if (compositeState.transparencyState == RenderStateShard.TRANSLUCENT_TRANSPARENCY) {
			return MaterialConstants.PRESET_TRANSLUCENT;
		} else if (VanillaShaderInfo.get(compositeState.shaderState).cutout() != MaterialConstants.CUTOUT_NONE) {
			final TextureStateShard tex = (TextureStateShard) compositeState.textureState;
			return tex.mipmap ? MaterialConstants.PRESET_CUTOUT_MIPPED : MaterialConstants.PRESET_CUTOUT;
		} else {
			return MaterialConstants.PRESET_SOLID;
		}
	}
}
