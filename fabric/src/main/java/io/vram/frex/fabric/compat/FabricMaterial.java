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

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.util.TriState;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialView;
import io.vram.frex.api.material.RenderMaterial;

public class FabricMaterial implements net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial {
	public static FabricMaterial of(RenderMaterial wrapped) {
		return new FabricMaterial(wrapped);
	}

	public static BlendMode blendModeFromPreset(int preset) {
		return switch (preset) {
			case MaterialConstants.PRESET_SOLID -> BlendMode.SOLID;
			case MaterialConstants.PRESET_CUTOUT_MIPPED -> BlendMode.CUTOUT_MIPPED;
			case MaterialConstants.PRESET_CUTOUT -> BlendMode.CUTOUT;
			case MaterialConstants.PRESET_TRANSLUCENT -> BlendMode.TRANSLUCENT;
			default -> BlendMode.DEFAULT;
		};
	}

	public static BlendMode blendModeFromMaterial(MaterialView mat) {
		if (mat.transparency() != MaterialConstants.TRANSPARENCY_NONE) {
			return BlendMode.TRANSLUCENT;
		} else if (mat.cutout() == MaterialConstants.CUTOUT_NONE) {
			return BlendMode.SOLID;
		} else {
			return mat.unmipped() ? BlendMode.CUTOUT : BlendMode.CUTOUT_MIPPED;
		}
	}

	final RenderMaterial wrapped;
	final BlendMode blendMode;

	protected FabricMaterial(RenderMaterial wrapped) {
		this.wrapped = wrapped;

		if (wrapped.preset() == MaterialConstants.PRESET_NONE) {
			blendMode = blendModeFromMaterial(wrapped);
		} else {
			blendMode = blendModeFromPreset(wrapped.preset());
		}
	}

	@Override
	public BlendMode blendMode() {
		return blendMode;
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
