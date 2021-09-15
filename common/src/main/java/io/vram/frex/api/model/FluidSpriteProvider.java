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

package io.vram.frex.api.model;

import io.vram.frex.impl.model.SimpleFluidSpriteProvider;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Get the sprites for a fluid being rendered at a given position.
 * For optimal performance, the sprites should be loaded as part of a
 * resource reload and *not* looked up every time the method is called!
 *
 * @param view  The world view pertaining to the fluid. May be null!
 * @param pos   The position of the fluid in the world. May be null!
 * @param state The current state of the fluid.
 * @return An array of size two or three: the first entry contains the "still" sprite,
 * while the second entry contains the "flowing" sprite. If the array is size three,
 * the third sprite is the "overlay" sprite and its presence indicates an overlay
 * sprite should be used.
 */
@FunctionalInterface
public interface FluidSpriteProvider {
	Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state);

	static FluidSpriteProvider of(String stillSpriteName, String flowingSpriteName, @Nullable String overlaySpriteName) {
		return SimpleFluidSpriteProvider.of(new Identifier(stillSpriteName), new Identifier(flowingSpriteName), overlaySpriteName == null ? null : new Identifier(overlaySpriteName));
	}

	static FluidSpriteProvider of(String stillSpriteName, String flowingSpriteName) {
		return of(stillSpriteName, flowingSpriteName, null);
	}

	static FluidSpriteProvider of(Identifier stillSpriteName, Identifier flowingSpriteName, @Nullable Identifier overlaySpriteName) {
		return SimpleFluidSpriteProvider.of(stillSpriteName, flowingSpriteName, overlaySpriteName);
	}

	static FluidSpriteProvider of(Identifier stillSpriteName, Identifier flowingSpriteName) {
		return of(stillSpriteName, flowingSpriteName, null);
	}

	FluidSpriteProvider WATER_SPRITES = of("minecraft:blocks/water_still", "minecraft:blocks/water_flow", "minecraft:blocks/water_overlay");
	FluidSpriteProvider LAVA_SPRITES = of("minecraft:blocks/lava_still", "minecraft:blocks/lava_flow");
}
