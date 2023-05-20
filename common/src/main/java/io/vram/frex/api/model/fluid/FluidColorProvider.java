/*
 * This file is part of FREX and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
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
 */

package io.vram.frex.api.model.fluid;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.FluidState;

import io.vram.frex.impl.model.BiomeLookupHelper;

/**
 * Get the tint color for a fluid being rendered at a given position.
 *
 * @param view  The world view pertaining to the fluid. May be null!
 * @param pos   The position of the fluid in the world. May be null!
 * @param state The current state of the fluid.
 * @return The tint color of the fluid.
 */
@FunctionalInterface
public interface FluidColorProvider {
	int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state);

	static FluidColorProvider of(int color) {
		return (v, p, s) -> color;
	}

	int DEFAULT_WATER_COLOR = BiomeLookupHelper.getHolderOrThrow(Biomes.OCEAN).value().getWaterColor();

	FluidColorProvider WHITE_COLOR = (v, p, s) -> -1;
	FluidColorProvider WATER_COLOR = (v, p, s) -> v == null || p == null ? DEFAULT_WATER_COLOR : BiomeColors.getAverageWaterColor(v, p);
}
