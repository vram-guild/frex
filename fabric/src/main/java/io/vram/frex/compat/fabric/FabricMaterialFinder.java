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

package io.vram.frex.compat.fabric;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;

import io.vram.frex.api.material.MaterialCondition;
import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;

import grondag.frex.api.material.RenderMaterial;

@SuppressWarnings("deprecation")
public class FabricMaterialFinder implements grondag.frex.api.material.MaterialFinder {
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
	public grondag.frex.api.material.MaterialFinder clear() {
		wrapped.clear();
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder spriteDepth(int depth) {
		// NOOP
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder blendMode(@Nullable BlendMode blendMode) {
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
	public grondag.frex.api.material.MaterialFinder blendMode(int spriteIndex, BlendMode blendMode) {
		return blendMode(blendMode);
	}

	@Override
	public grondag.frex.api.material.MaterialFinder disableColorIndex(int spriteIndex, boolean disable) {
		wrapped.disableColorIndex(disable);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder disableDiffuse(int spriteIndex, boolean disable) {
		wrapped.disableDiffuse(disable);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder disableAo(int spriteIndex, boolean disable) {
		wrapped.disableAo(disable);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder emissive(int spriteIndex, boolean isEmissive) {
		wrapped.emissive(isEmissive);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder disableColorIndex(boolean disable) {
		wrapped.disableColorIndex(disable);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder disableDiffuse(boolean disable) {
		wrapped.disableDiffuse(disable);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder disableAo(boolean disable) {
		wrapped.disableAo(disable);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder emissive(boolean isEmissive) {
		wrapped.emissive(isEmissive);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder copyFrom(RenderMaterial material) {
		wrapped.copyFrom(((FabricMaterial) material).wrapped);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder blur(boolean blur) {
		wrapped.blur(blur);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder condition(MaterialCondition condition) {
		wrapped.condition(condition);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder cull(boolean cull) {
		wrapped.cull(cull);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder cutout(int cutout) {
		wrapped.cutout(cutout);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder decal(int decal) {
		wrapped.decal(decal);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder depthTest(int depthTest) {
		wrapped.depthTest(depthTest);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder discardsTexture(boolean discardsTexture) {
		wrapped.discardsTexture(discardsTexture);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder flashOverlay(boolean flashOverlay) {
		wrapped.flashOverlay(flashOverlay);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder fog(boolean enable) {
		wrapped.fog(enable);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder hurtOverlay(boolean hurtOverlay) {
		wrapped.hurtOverlay(hurtOverlay);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder lines(boolean lines) {
		wrapped.lines(lines);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder shader(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId) {
		wrapped.shader(vertexSourceId, fragmentSourceId);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder shader(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId, @Nullable ResourceLocation depthVertexSourceId, @Nullable ResourceLocation depthFragmentSourceId) {
		wrapped.shader(vertexSourceId, fragmentSourceId, depthVertexSourceId, depthFragmentSourceId);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder sorted(boolean sorted) {
		wrapped.sorted(sorted);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder target(int target) {
		wrapped.target(target);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder texture(@Nullable ResourceLocation id) {
		wrapped.texture(id);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder transparency(int transparency) {
		wrapped.transparency(transparency);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder unmipped(boolean unmipped) {
		wrapped.unmipped(unmipped);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder writeMask(int writeMask) {
		wrapped.writeMask(writeMask);
		return this;
	}

	@Override
	public grondag.frex.api.material.MaterialFinder castShadows(boolean castShadows) {
		wrapped.castShadows(castShadows);
		return this;
	}
}
