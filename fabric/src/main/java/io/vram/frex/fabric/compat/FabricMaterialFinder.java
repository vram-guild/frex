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

package io.vram.frex.fabric.compat;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;

public class FabricMaterialFinder implements net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder {
	public static FabricMaterialFinder of(MaterialFinder wrapped) {
		return new FabricMaterialFinder(wrapped);
	}

	final MaterialFinder wrapped;

	protected FabricMaterialFinder(MaterialFinder wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public RenderMaterial find() {
		return FabricMaterial.of(wrapped.find());
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder clear() {
		wrapped.clear();
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder spriteDepth(int depth) {
		// NOOP
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder blendMode(int spriteIndex, @Nullable BlendMode blendMode) {
		if (blendMode == null) {
			wrapped.preset(MaterialConstants.PRESET_NONE);
		} else {
			switch (blendMode) {
				case CUTOUT:
					wrapped.preset(MaterialConstants.PRESET_CUTOUT);
					break;
				case CUTOUT_MIPPED:
					wrapped.preset(MaterialConstants.PRESET_CUTOUT_MIPPED);
					break;
				case SOLID:
					wrapped.preset(MaterialConstants.PRESET_SOLID);
					break;
				case TRANSLUCENT:
					wrapped.preset(MaterialConstants.PRESET_TRANSLUCENT);
					break;
				case DEFAULT:
				default:
					wrapped.preset(MaterialConstants.PRESET_DEFAULT);
			}
		}

		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder disableColorIndex(int spriteIndex, boolean disable) {
		wrapped.disableColorIndex(disable);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder disableDiffuse(int spriteIndex, boolean disable) {
		wrapped.disableDiffuse(disable);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder disableAo(int spriteIndex, boolean disable) {
		wrapped.disableAo(disable);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder emissive(int spriteIndex, boolean isEmissive) {
		wrapped.emissive(isEmissive);
		return this;
	}
}
