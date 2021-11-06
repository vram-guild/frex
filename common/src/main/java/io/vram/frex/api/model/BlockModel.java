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

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.InputContext.Type;
import io.vram.frex.api.model.util.FaceUtil;
import io.vram.frex.api.world.BlockEntityRenderData;
import io.vram.frex.impl.model.ModelLookups;

@FunctionalInterface
public interface BlockModel extends DynamicModel {
	void renderAsBlock(BlockInputContext input, QuadSink output);

	@Override
	default void renderDynamic(InputContext input, QuadSink output) {
		if (input.type() == Type.BLOCK) {
			renderAsBlock((BlockInputContext) input, output);
		}
	}

	public interface BlockInputContext extends BakedInputContext {
		@Override
		default Type type() {
			return Type.BLOCK;
		}

		BlockAndTintGetter blockView();

		/**
		 * Use to retrieve Biome at a given block position.
		 *
		 * <p>Conceptually, this function belongs as part of {@link #blockView()}, but it is not
		 * part of the {{@link BlockAndTintGetter} type and in some block render contexts nothing more
		 * is available.  A custom block view type would add excessive complexity to implementations,
		 * and so biome access is exposed here.
		 *
		 * <p>This may be a cached value, depending on implementation. If biome information is
		 * unavailable in the current context, will always return the plains biome.
		 *
		 * @return Biome at given position, or plains biome if biome information is unavailable.
		 */
		default Biome getBiome(BlockPos pos) {
			return RegistryAccess.builtin().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS);
		}

		/**
		 * True when {@link #getBiome(BlockPos)} returns meaningful results.
		 */
		default boolean hasBiomeAccess() {
			return false;
		}

		boolean isFluidModel();

		@Override
		BlockState blockState();

		@Override
		BlockPos pos();

		@Override
		Random random();

		@Override
		boolean cullTest(int faceId);

		@Override
		default boolean cullTest(Direction face) {
			return cullTest(FaceUtil.toFaceIndex(face));
		}

		/**
		 * In terrain rendering this will hold the result of functions
		 * registered via {@link BlockEntityRenderData#registerProvider(net.minecraft.world.level.block.entity.BlockEntityType, java.util.function.Function)}
		 * for the block entity at the given position.
		 *
		 * <p>If outside of terrain rendering, or if no function is registered,
		 * or if no BlockEntity is present at the given position, will return null.
		 *
		 * @return Result of a registered block entity render data function, or null if none
		 * registered or not applicable.
		 */
		@Nullable Object blockEntityRenderData(BlockPos pos);
	}

	/**
	 * Result can be safely cast to BlockModel.  Return type is BakedModel
	 * because it may still be needed for model-level attributes.
	 */
	static BakedModel get(BlockState blockState) {
		return ModelLookups.BLOCK_MODEL_SHAPER.getBlockModel(blockState);
	}
}
