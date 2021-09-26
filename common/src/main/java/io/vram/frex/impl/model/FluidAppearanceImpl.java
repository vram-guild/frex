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

import io.vram.frex.api.model.fluid.FluidAppearance;
import io.vram.frex.api.model.fluid.FluidColorProvider;
import io.vram.frex.api.model.fluid.FluidSpriteProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

@Internal
public class FluidAppearanceImpl implements FluidAppearance {
	private final FluidColorProvider colorProvider;
	private final FluidSpriteProvider spriteProvider;

	FluidAppearanceImpl(FluidColorProvider colorProvider, FluidSpriteProvider spriteProvider) {
		this.colorProvider = colorProvider;
		this.spriteProvider = spriteProvider;
	}

	@Override
	public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		return colorProvider.getFluidColor(view, pos, state);
	}

	@Override
	public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
		return spriteProvider.getFluidSprites(view, pos, state);
	}

	private static final Object2ObjectOpenHashMap<Fluid, FluidAppearance> MAP = new Object2ObjectOpenHashMap<>();

	public static FluidAppearance get(Fluid fluid) {
		return MAP.get(fluid);
	}

	public static void register(FluidAppearance appearance, Fluid[] fluids) {
		for (final var f : fluids) {
			MAP.put(f, appearance);
		}
	}

	public static FluidAppearance of(FluidColorProvider colorProvider, FluidSpriteProvider spriteProvider) {
		return new FluidAppearanceImpl(colorProvider, spriteProvider);
	}
}
