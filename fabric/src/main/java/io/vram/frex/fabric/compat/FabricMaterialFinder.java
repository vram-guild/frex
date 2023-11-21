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

package io.vram.frex.fabric.compat;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialView;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.util.TriState;

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
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder blendMode(@Nullable BlendMode blendMode) {
		if (blendMode == null) {
			wrapped.preset(MaterialConstants.PRESET_NONE);
		} else {
			switch (blendMode) {
				case CUTOUT -> wrapped.preset(MaterialConstants.PRESET_CUTOUT);
				case CUTOUT_MIPPED -> wrapped.preset(MaterialConstants.PRESET_CUTOUT_MIPPED);
				case SOLID -> wrapped.preset(MaterialConstants.PRESET_SOLID);
				case TRANSLUCENT -> wrapped.preset(MaterialConstants.PRESET_TRANSLUCENT);
				default -> wrapped.preset(MaterialConstants.PRESET_DEFAULT);
			}
		}

		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder disableColorIndex(boolean disable) {
		wrapped.disableColorIndex(disable);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder disableDiffuse(boolean disable) {
		wrapped.disableDiffuse(disable);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder disableAo(int spriteIndex, boolean disable) {
		wrapped.disableAo(disable);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder ambientOcclusion(TriState mode) {
		switch (mode) {
			case TRUE, FALSE -> wrapped.disableAo(!mode.get());
			case DEFAULT -> wrapped.resetDisableAo();
		}

		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder glint(TriState mode) {
		switch (mode) {
			case TRUE, FALSE -> wrapped.foilOverlay(mode.get());
			case DEFAULT -> wrapped.resetFoilOverlay();
		}

		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder copyFrom(MaterialView material) {
		wrapped.copyFrom(((FabricMaterial) material).wrapped);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder emissive(boolean isEmissive) {
		wrapped.emissive(isEmissive);
		return this;
	}

	@Override
	public BlendMode blendMode() {
		if (wrapped.preset() == MaterialConstants.PRESET_NONE) {
			return FabricMaterial.blendModeFromMaterial(wrapped);
		} else {
			return FabricMaterial.blendModeFromPreset(wrapped.preset());
		}
	}

	@Override
	public boolean disableColorIndex() {
		return wrapped.disableColorIndex();
	}

	@Override
	public boolean emissive() {
		return wrapped.emissive();
	}

	@Override
	public boolean disableDiffuse() {
		return wrapped.disableDiffuse();
	}

	@Override
	public TriState ambientOcclusion() {
		if (wrapped.disableAoIsDefault()) {
			return TriState.DEFAULT;
		}

		return TriState.of(!wrapped.disableAo());
	}

	@Override
	public TriState glint() {
		if (wrapped.foilOverlayIsDefault()) {
			return TriState.DEFAULT;
		}

		return TriState.of(wrapped.foilOverlay());
	}
}
