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

package io.vram.frex.api.model;

import java.util.Random;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.world.BlockEntityRenderData;

@FunctionalInterface
public interface BlockModel {
	// WIP: find way to expose biome info
	void renderAsBlock(BlockInputContext input, ModelOuputContext output);

	public interface BlockInputContext {
		BlockAndTintGetter blockView();

		BlockState blockState();

		BlockPos pos();

		Random random();

		/**
		 * In terrain rendering this will hold the result of functions
		 * registered via {@link BlockEntityRenderData#registerProvider(net.minecraft.world.level.block.entity.BlockEntityType, java.util.function.Function)}
		 * for the block entity at the given position.
		 *
		 * <p>If outside of terrain rendering, or if no function is registered,
		 * or if no BlockEntity is present at the given position, will return null.
		 * @return Result of a registered block entity render data function, or null if none
		 * registered or not applicable.
		 */
		@Nullable Object blockEntityRenderData(BlockPos pos);
	}
}
