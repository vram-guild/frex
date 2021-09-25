/*
 *  Copyright 2019, 2020 grondag
 *
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

package io.vram.frex.impl.material;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.renderer.Renderer;
import io.vram.frex.api.rendertype.RenderTypeExclusion;
import io.vram.frex.api.rendertype.VanillaShaderData;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.EmptyTextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

@Internal
public final class RenderTypeUtilImpl {
	private RenderTypeUtilImpl() { }

	public static boolean toMaterialFinder(MaterialFinder finder, RenderType renderType) {
		if (RenderTypeExclusion.isExcluded(renderType)) {
			return false;
		}

		copyRenderTypeAttributes(finder, renderType);
		return true;
	}

	private static void copyRenderTypeAttributes(MaterialFinder finder, RenderType renderType) {
		final var compositeState = ((CompositeRenderType) renderType).state;
		final EmptyTextureStateShard texBase = compositeState.textureState;

		final VanillaShaderData sd = VanillaShaderData.get(compositeState.shaderState);

		if (texBase != null && texBase instanceof TextureStateShard) {
			final TextureStateShard tex = (TextureStateShard) compositeState.textureState;
			finder.texture(tex.texture.orElse(null));
			finder.unmipped(!tex.mipmap);
			finder.blur(tex.blur);
		}

		finder.transparency(RenderTypeShardHelper.toMaterialTransparency(compositeState.transparencyState));
		finder.depthTest(RenderTypeShardHelper.toMaterialDepthTest(compositeState.depthTestState));
		finder.cull(compositeState.cullState == RenderStateShard.CULL);
		finder.writeMask(RenderTypeShardHelper.toMaterialWriteMask(compositeState.writeMaskState));
		finder.decal(RenderTypeShardHelper.toMaterialDecal(compositeState.layeringState));
		finder.target(RenderTypeShardHelper.toMaterialTarget(compositeState.outputState));
		finder.lines(compositeState.lineState != RenderStateShard.DEFAULT_LINE);
		finder.fog(sd.fog());
		finder.disableDiffuse(!sd.diffuse());
		finder.cutout(sd.cutout());
		finder.sorted(renderType.sortOnUpload);
		finder.label(renderType.name);

		// vanilla sets these as part of draw process but we don't want special casing
		if (renderType == RenderType.solid() || renderType == RenderType.cutoutMipped() || renderType == RenderType.cutout() || renderType == RenderType.translucent()) {
			finder.cull(true);
			finder.texture(TextureAtlas.LOCATION_BLOCKS);
			finder.writeMask(MaterialConstants.WRITE_MASK_COLOR_DEPTH);
			finder.disableAo(false);
		} else {
			finder.disableAo(true);
		}
	}

	public static RenderMaterial toMaterial(RenderType renderType, boolean foilOverlay) {
		if (RenderTypeExclusion.isExcluded(renderType)) {
			return Renderer.get().materialById(RenderMaterial.MISSING_MATERIAL_KEY);
		}

		final MaterialFinder finder = MaterialFinder.threadLocal();
		copyRenderTypeAttributes(finder, renderType);
		finder.foilOverlay(foilOverlay);
		final RenderMaterial result = finder.find();

		return result;
	}
}
