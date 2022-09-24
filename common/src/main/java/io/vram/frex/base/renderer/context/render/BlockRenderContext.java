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

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.base.renderer.context.input.BaseBlockInputContext;

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
	 * @param blockState
	 * @param blockPos
	 * @param modelAO
	 * @param seed       pass -1 for default behavior
	 */
	public void prepareForBlock(BakedModel model, BlockState blockState, BlockPos blockPos, long seed, int overlay) {
		inputContext.prepareForBlock(model, blockState, blockPos, seed, overlay);
		prepareForBlock(blockState, model.useAmbientOcclusion());
	}

	public void prepareForBlock(BakedModel model, BlockState blockState, BlockPos blockPos) {
		inputContext.prepareForBlock(model, blockState, blockPos);
		prepareForBlock(blockState, model.useAmbientOcclusion());
	}

	private void prepareForBlock(BlockState blockState, boolean modelAO) {
		materialMap = MaterialMap.get(blockState);
		gameObject = blockState;
	}

	public void prepareForFluid(BlockState blockState, BlockPos blockPos, boolean modelAO) {
		inputContext.prepareForFluid(blockState, blockPos);
		final var fluidState = blockState.getFluidState();
		materialMap = MaterialMap.get(fluidState);
		gameObject = fluidState;
	}

	@Override
	protected void resolveMaterial() {
		final MaterialFinder finder = this.finder;

		int bm = finder.preset();

		if (bm == MaterialConstants.PRESET_NONE) {
			return;
		}

		// TODO: honor tri-state from material when implemented
		if (inputContext.overlay() != OverlayTexture.NO_OVERLAY) {
			finder.overlay(inputContext.overlay());
		}

		// TODO: honor tri-state from material when implemented
		if (inputContext.isEmissiveRendering()) {
			finder.emissive(true);
		}

		if (bm == MaterialConstants.PRESET_DEFAULT) {
			bm = inputContext.defaultPreset();
		}

		switch (bm) {
			case MaterialConstants.PRESET_CUTOUT: {
				finder.transparency(MaterialConstants.TRANSPARENCY_NONE)
					.cutout(MaterialConstants.CUTOUT_HALF)
					.unmipped(true)
					.target(MaterialConstants.TARGET_MAIN)
					.sorted(false);
				break;
			}
			case MaterialConstants.PRESET_CUTOUT_MIPPED:
				finder
					.transparency(MaterialConstants.TRANSPARENCY_NONE)
					.cutout(MaterialConstants.CUTOUT_HALF)
					.unmipped(false)
					.target(MaterialConstants.TARGET_MAIN)
					.sorted(false);
				break;
			case MaterialConstants.PRESET_TRANSLUCENT:
				finder.transparency(MaterialConstants.TRANSPARENCY_TRANSLUCENT)
					.cutout(MaterialConstants.CUTOUT_NONE)
					.unmipped(false)
					.target(MaterialConstants.TARGET_TRANSLUCENT)
					.sorted(true);
				break;
			case MaterialConstants.PRESET_SOLID:
				finder.transparency(MaterialConstants.TRANSPARENCY_NONE)
					.cutout(MaterialConstants.CUTOUT_NONE)
					.unmipped(false)
					.target(MaterialConstants.TARGET_MAIN)
					.sorted(false);
				break;
			default:
				assert false : "Unhandled preset";
		}

		finder.preset(MaterialConstants.PRESET_NONE);
	}
}
