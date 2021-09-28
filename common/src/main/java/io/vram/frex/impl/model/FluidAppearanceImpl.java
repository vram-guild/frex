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

package io.vram.frex.impl.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import io.vram.frex.api.model.fluid.FluidAppearance;
import io.vram.frex.api.model.fluid.FluidColorProvider;
import io.vram.frex.api.model.fluid.FluidSpriteProvider;

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
