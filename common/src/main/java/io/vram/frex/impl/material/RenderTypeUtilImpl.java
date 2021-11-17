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

package io.vram.frex.impl.material;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.EmptyTextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.rendertype.RenderTypeExclusion;
import io.vram.frex.api.rendertype.VanillaShaderInfo;

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

		final VanillaShaderInfo sd = VanillaShaderInfoImpl.get(compositeState.shaderState);

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
			return RenderMaterial.fromId(RenderMaterial.MISSING_MATERIAL_KEY);
		}

		final MaterialFinder finder = MaterialFinder.threadLocal();
		copyRenderTypeAttributes(finder, renderType);
		finder.foilOverlay(foilOverlay);
		final RenderMaterial result = finder.find();

		return result;
	}
}
