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

package io.vram.frex.base.renderer.material;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;

public abstract class BaseMaterialFinder extends BaseMaterialView implements MaterialFinder {
	@Override
	public BaseMaterialFinder blur(boolean blur) {
		bits0 = BLUR.setValue(blur, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder castShadows(boolean castShadows) {
		bits0 = DISABLE_SHADOWS.setValue(!castShadows, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder clear() {
		bits0 = DEFAULT_BITS_0;
		bits1 = DEFAULT_BITS_1;
		label = DEFAULT_LABEL;
		return this;
	}

	@Override
	public BaseMaterialFinder conditionIndex(int index) {
		bits1 = CONDITION.setValue(index, bits1);
		return this;
	}

	public BaseMaterialFinder copyFrom(BaseMaterialView template) {
		bits0 = template.bits0;
		bits1 = template.bits1;
		label = template.label;
		return this;
	}

	@Override
	public BaseMaterialFinder copyFrom(RenderMaterial material) {
		return copyFrom((BaseMaterialView) material);
	}

	@Override
	public BaseMaterialFinder cull(boolean cull) {
		bits0 = CULL.setValue(cull, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder cutout(int cutout) {
		bits0 = CUTOUT.setValue(cutout, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder decal(int decal) {
		bits0 = DECAL.setValue(decal, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder depthTest(int depthTest) {
		bits0 = DEPTH_TEST.setValue(depthTest, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder disableAo(boolean disableAo) {
		bits0 = DISABLE_AO.setValue(disableAo, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder disableColorIndex(boolean disable) {
		bits0 = DISABLE_COLOR_INDEX.setValue(disable, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder disableDiffuse(boolean disableDiffuse) {
		bits0 = DISABLE_DIFFUSE.setValue(disableDiffuse, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder discardsTexture(boolean discardsTexture) {
		bits0 = DISCARDS_TEXTURE.setValue(discardsTexture, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder emissive(boolean emissive) {
		bits0 = EMISSIVE.setValue(emissive, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder flashOverlay(boolean flashOverlay) {
		bits0 = FLASH_OVERLAY.setValue(flashOverlay, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder fog(boolean fog) {
		bits0 = FOG.setValue(fog, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder foilOverlay(boolean enableGlint) {
		bits0 = ENABLE_GLINT.setValue(enableGlint, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder hurtOverlay(boolean hurtOverlay) {
		bits0 = HURT_OVERLAY.setValue(hurtOverlay, bits0);
		return this;
	}

	@Override
	public MaterialFinder label(String label) {
		this.label = label;
		return this;
	}

	@Override
	public BaseMaterialFinder lines(boolean lines) {
		bits0 = LINES.setValue(lines, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder preset(int preset) {
		bits0 = PRESET.setValue(preset, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder shaderIndex(int shaderIndex) {
		bits1 = SHADER.setValue(shaderIndex, bits1);
		return this;
	}

	@Override
	public BaseMaterialFinder sorted(boolean sorted) {
		bits0 = SORTED.setValue(sorted, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder target(int target) {
		bits0 = TARGET.setValue(target, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder textureIndex(int index) {
		bits1 = TEXTURE.setValue(index, bits1);
		return this;
	}

	@Override
	public BaseMaterialFinder transparency(int transparency) {
		bits0 = TRANSPARENCY.setValue(transparency, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder unmipped(boolean unmipped) {
		bits0 = UNMIPPED.setValue(unmipped, bits0);
		return this;
	}

	@Override
	public BaseMaterialFinder writeMask(int writeMask) {
		bits0 = WRITE_MASK.setValue(writeMask, bits0);
		return this;
	}
}
