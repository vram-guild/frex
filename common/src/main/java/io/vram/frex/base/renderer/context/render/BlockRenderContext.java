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

package io.vram.frex.base.renderer.context.render;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.model.util.ColorUtil;
import io.vram.frex.base.renderer.context.input.BaseBlockInputContext;
import io.vram.frex.base.renderer.util.EncoderUtil;

public abstract class BlockRenderContext<T extends BlockAndTintGetter> extends BakedRenderContext<BaseBlockInputContext<T>> {
	/**
	 * For use by chunk builder - avoids another threadlocal.
	 */
	public final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

	@Override
	protected BaseBlockInputContext<T> createInputContext() {
		return new BaseBlockInputContext<>();
	}

	/**
	 * @param seed  pass -1 for default behavior
	 */
	public void prepareForBlock(BakedModel model, BlockState blockState, BlockPos blockPos, long seed, int overlay) {
		inputContext.prepareForBlock(model, blockState, blockPos, seed, overlay);
		prepareForBlock(blockState);
	}

	public void prepareForBlock(BakedModel model, BlockState blockState, BlockPos blockPos) {
		inputContext.prepareForBlock(model, blockState, blockPos);
		prepareForBlock(blockState);
	}

	private void prepareForBlock(BlockState blockState) {
		materialMap = MaterialMap.get(blockState);
		gameObject = blockState;
	}

	public void prepareForFluid(BlockState blockState, BlockPos blockPos) {
		inputContext.prepareForFluid(blockState, blockPos);
		final var fluidState = blockState.getFluidState();
		materialMap = MaterialMap.get(fluidState);
		gameObject = fluidState;
	}

	@Override
	protected void applyMaterialDefaults() {
		super.applyMaterialDefaults();

		if (finder.emissiveIsDefault()) {
			finder.emissive(inputContext.isEmissiveRendering());
		}
	}

	/**
	 * Simple diffuse shading routine for CPU-side vanilla lighting models without AO shading.
	 */
	protected void applySimpleDiffuseShade() {
		final var blockView = inputContext.blockView();

		if (emitter.material().disableDiffuse()) {
			// if diffuse is disabled, some dimensions can still have an ambient shading value.
			final float shade = blockView.getShade(Direction.UP, false);

			if (shade != 1.0f) {
				emitter.vertexColor(0, ColorUtil.multiplyRGB(emitter.vertexColor(0), shade));
				emitter.vertexColor(1, ColorUtil.multiplyRGB(emitter.vertexColor(1), shade));
				emitter.vertexColor(2, ColorUtil.multiplyRGB(emitter.vertexColor(2), shade));
				emitter.vertexColor(3, ColorUtil.multiplyRGB(emitter.vertexColor(3), shade));
			}
		} else {
			if (emitter.hasVertexNormals()) {
				// different shade value per vertex
				emitter.vertexColor(0, ColorUtil.multiplyRGB(emitter.vertexColor(0), EncoderUtil.normalShade(emitter.packedNormal(0), blockView, true)));
				emitter.vertexColor(1, ColorUtil.multiplyRGB(emitter.vertexColor(1), EncoderUtil.normalShade(emitter.packedNormal(1), blockView, true)));
				emitter.vertexColor(2, ColorUtil.multiplyRGB(emitter.vertexColor(2), EncoderUtil.normalShade(emitter.packedNormal(2), blockView, true)));
				emitter.vertexColor(3, ColorUtil.multiplyRGB(emitter.vertexColor(3), EncoderUtil.normalShade(emitter.packedNormal(3), blockView, true)));
			} else {
				// same shade value for all vertices
				final float shade = blockView.getShade(emitter.lightFace(), true);

				emitter.vertexColor(0, ColorUtil.multiplyRGB(emitter.vertexColor(0), shade));
				emitter.vertexColor(1, ColorUtil.multiplyRGB(emitter.vertexColor(1), shade));
				emitter.vertexColor(2, ColorUtil.multiplyRGB(emitter.vertexColor(2), shade));
				emitter.vertexColor(3, ColorUtil.multiplyRGB(emitter.vertexColor(3), shade));
			}
		}
	}
}
