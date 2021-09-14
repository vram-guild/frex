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

import io.vram.frex.api.model.FluidAppearance;
import io.vram.frex.api.model.FluidColorProvider;
import io.vram.frex.api.model.FluidSpriteProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class FluidAppearanceRegistryImpl {
	private static final Object2ObjectOpenHashMap<Fluid, FluidAppearance> MAP = new Object2ObjectOpenHashMap<>();
	private static final ObjectArrayList<SimpleFluidSpriteProvider> RELOAD_LIST = new ObjectArrayList<>();

	public static FluidAppearance get(Fluid fluid) {
		return MAP.get(fluid);
	}

	private static void register(Fluid fluid, FluidAppearance appearance) {
		MAP.put(fluid, appearance);
	}

	public static void register(Fluid fluid, FluidColorProvider colorProvider, FluidSpriteProvider spriteProvider) {
		register(fluid, new FluidAppearanceImpl(colorProvider, spriteProvider));
	}

	public static void register(Fluid fluid, FluidColorProvider colorProvider, Identifier stillSpriteName, Identifier flowingSpriteName) {
		register(fluid, new FluidAppearanceImpl(colorProvider, new SimpleFluidSpriteProvider(stillSpriteName, flowingSpriteName)));
	}

	public static void reload() {
		for (final var p : RELOAD_LIST) {
			p.reload();
		}
	}

	private static class SimpleFluidSpriteProvider implements FluidSpriteProvider {
		private final Identifier stillSpriteName;
		private final Identifier flowingSpriteName;
		private Sprite[] sprites = null;

		private SimpleFluidSpriteProvider(Identifier stillSpriteName, Identifier flowingSpriteName) {
			this.stillSpriteName = stillSpriteName;
			this.flowingSpriteName = flowingSpriteName;
			RELOAD_LIST.add(this);
		}

		@Override
		public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
			var result = sprites;

			if (result == null) {
				result = new Sprite[2];
				final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
				result[0] = atlas.apply(stillSpriteName);
				result[1] = atlas.apply(flowingSpriteName);
				sprites = result;
			}

			return result;
		}

		private void reload() {
			sprites = null;
		}
	}
}
