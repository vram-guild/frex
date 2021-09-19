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

package io.vram.frex.impl.model;

import java.util.function.Function;

import io.vram.frex.api.model.FluidSpriteProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;

public class SimpleFluidSpriteProvider implements FluidSpriteProvider {
	private final ResourceLocation stillSpriteName;
	private final ResourceLocation flowingSpriteName;
	private final ResourceLocation overlaySpriteName;
	private TextureAtlasSprite[] sprites = null;

	private SimpleFluidSpriteProvider(ResourceLocation stillSpriteName, ResourceLocation flowingSpriteName, @Nullable ResourceLocation overlaySpriteName) {
		this.stillSpriteName = stillSpriteName;
		this.flowingSpriteName = flowingSpriteName;
		this.overlaySpriteName = overlaySpriteName;
		RELOAD_LIST.add(this);
	}

	@Override
	public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
		var result = sprites;

		if (result == null) {
			final boolean overlay = overlaySpriteName != null;
			result = new TextureAtlasSprite[overlay ? 3 : 2];
			final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
			result[0] = atlas.apply(stillSpriteName);
			result[1] = atlas.apply(flowingSpriteName);

			if (overlay) {
				result[2] = atlas.apply(overlaySpriteName);
			}

			sprites = result;
		}

		return result;
	}

	private void onReload() {
		sprites = null;
	}

	public static FluidSpriteProvider of(ResourceLocation stillSpriteName, ResourceLocation flowingSpriteName, @Nullable ResourceLocation overlaySpriteName) {
		return new SimpleFluidSpriteProvider(stillSpriteName, flowingSpriteName, overlaySpriteName);
	}

	private static final ObjectArrayList<SimpleFluidSpriteProvider> RELOAD_LIST = new ObjectArrayList<>();

	public static void reload() {
		for (final var p : RELOAD_LIST) {
			p.onReload();
		}
	}
}
