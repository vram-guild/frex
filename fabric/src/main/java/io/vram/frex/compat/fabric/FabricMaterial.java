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
import io.vram.frex.api.material.RenderMaterial;

@SuppressWarnings("deprecation")
public class FabricMaterial implements grondag.frex.api.material.RenderMaterial {
	public static FabricMaterial of(RenderMaterial wrapped) {
		return new FabricMaterial(wrapped);
	}

	final RenderMaterial wrapped;

	protected FabricMaterial(RenderMaterial wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public final int spriteDepth() {
		return 1;
	}

	@Override
	public @Nullable BlendMode blendMode() {
		switch (wrapped.preset()) {
			case MaterialConstants.PRESET_CUTOUT:
				return BlendMode.CUTOUT;
			case MaterialConstants.PRESET_CUTOUT_MIPPED:
				return BlendMode.CUTOUT_MIPPED;
			case MaterialConstants.PRESET_NONE:
				return null;
			case MaterialConstants.PRESET_SOLID:
				return BlendMode.SOLID;
			case MaterialConstants.PRESET_TRANSLUCENT:
				return BlendMode.TRANSLUCENT;
			case MaterialConstants.PRESET_DEFAULT:
			default:
				return BlendMode.DEFAULT;
		}
	}

	@Override
	public boolean blur() {
		return wrapped.blur();
	}

	@Override
	public MaterialCondition condition() {
		return wrapped.condition();
	}

	@Override
	public boolean cull() {
		return wrapped.cull();
	}

	@Override
	public int cutout() {
		return wrapped.cutout();
	}

	@Override
	public int decal() {
		return wrapped.decal();
	}

	@Override
	public int depthTest() {
		return wrapped.depthTest();
	}

	@Override
	public boolean disableAo() {
		return wrapped.disableAo();
	}

	@Override
	public boolean disableColorIndex() {
		return wrapped.disableColorIndex();
	}

	@Override
	public boolean disableDiffuse() {
		return wrapped.disableDiffuse();
	}

	@Override
	public boolean discardsTexture() {
		return wrapped.discardsTexture();
	}

	@Override
	public boolean emissive() {
		return wrapped.emissive();
	}

	@Override
	public boolean flashOverlay() {
		return wrapped.flashOverlay();
	}

	@Override
	public boolean fog() {
		return wrapped.fog();
	}

	@Override
	public ResourceLocation fragmentShaderId() {
		return wrapped.fragmentShaderId();
	}

	@Override
	public String fragmentShader() {
		return wrapped.fragmentShader();
	}

	@Override
	public boolean hurtOverlay() {
		return wrapped.hurtOverlay();
	}

	@Override
	public boolean lines() {
		return wrapped.lines();
	}

	@Override
	public boolean sorted() {
		return wrapped.sorted();
	}

	@Override
	public int target() {
		return wrapped.target();
	}

	@Override
	public ResourceLocation textureId() {
		return wrapped.textureId();
	}

	@Override
	public String texture() {
		return wrapped.texture();
	}

	@Override
	public int transparency() {
		return wrapped.transparency();
	}

	@Override
	public boolean unmipped() {
		return wrapped.unmipped();
	}

	@Override
	public ResourceLocation vertexShaderId() {
		return wrapped.vertexShaderId();
	}

	@Override
	public String vertexShader() {
		return wrapped.vertexShader();
	}

	@Override
	public int writeMask() {
		return wrapped.writeMask();
	}

	@Override
	public String renderLayerName() {
		return wrapped.label();
	}

	@Override
	public boolean castShadows() {
		return wrapped.castShadows();
	}
}
