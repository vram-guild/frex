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

package io.vram.frex.pastel;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.model.BlockModel;
import io.vram.frex.api.world.RenderRegionBakeListener.BlockStateRenderer;
import io.vram.frex.pastel.util.RenderChunkRegionExt;

public class PastelBlockStateRenderer implements BlockStateRenderer {
	private PoseStack matrixStack;
	private RenderChunkRegion chunkRendererRegion;

	public void prepare(PoseStack matrixStack, RenderChunkRegion chunkRendererRegion) {
		this.matrixStack = matrixStack;
		this.chunkRendererRegion = chunkRendererRegion;
	}

	@Override
	public void bake(BlockPos pos, BlockState state) {
		final BakedModel model = BlockModel.get(state);

		matrixStack.pushPose();
		matrixStack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
		((RenderChunkRegionExt) chunkRendererRegion).frx_getContext().renderBlock(state, pos, model);
		matrixStack.popPose();
	}
}
