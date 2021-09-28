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

package io.vram.frex.impl.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessChunkRendererRegion;

import grondag.frex.api.event.RenderRegionBakeListener.BlockStateRenderer;

public class BlockStateRendererImpl implements BlockStateRenderer {
	private BlockRenderDispatcher blockRenderManager;
	private PoseStack matrixStack;
	private RenderChunkRegion chunkRendererRegion;

	public void prepare(BlockRenderDispatcher blockRenderManager, PoseStack matrixStack, RenderChunkRegion chunkRendererRegion) {
		this.blockRenderManager = blockRenderManager;
		this.matrixStack = matrixStack;
		this.chunkRendererRegion = chunkRendererRegion;
	}

	@Override
	public void bake(BlockPos pos, BlockState state) {
		final BakedModel model = blockRenderManager.getBlockModel(state);

		matrixStack.pushPose();
		matrixStack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
		((AccessChunkRendererRegion) chunkRendererRegion).fabric_getRenderer().tesselateBlock(state, pos, model, matrixStack);
		matrixStack.popPose();
	}
}
