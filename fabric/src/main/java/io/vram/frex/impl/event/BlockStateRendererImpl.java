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
