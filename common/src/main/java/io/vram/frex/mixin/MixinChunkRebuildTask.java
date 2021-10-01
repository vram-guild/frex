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

package io.vram.frex.mixin;

import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext;
import io.vram.frex.impl.event.BlockStateRendererImpl;
import io.vram.frex.impl.event.ChunkRenderConditionContext.RenderRegionListenerProvider;

@Mixin(targets = "net/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class MixinChunkRebuildTask implements RenderRegionContext {
	@Shadow protected RenderChunk this$1;

	@Unique
	private final BlockStateRendererImpl blockStateRenderer = new BlockStateRendererImpl();

	// could shadow this but is set to null by the time we need it
	@Unique
	private RenderChunkRegion contextRegion;

	@Inject(method = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Ljava/util/Set;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onCompile(float cameraX, float cameraY, float cameraZ, ChunkRenderDispatcher.CompiledChunk data, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph chunkOcclusionDataBuilder, Set<BlockEntity> set, RenderChunkRegion chunkRendererRegion, PoseStack matrixStack, Random random, BlockRenderDispatcher blockRenderManager) {
		if (chunkRendererRegion != null) {
			final RenderRegionBakeListener[] listeners = ((RenderRegionListenerProvider) chunkRendererRegion).frex_getRenderRegionListeners();

			if (listeners != null) {
				contextRegion = chunkRendererRegion;
				blockStateRenderer.prepare(blockRenderManager, matrixStack, chunkRendererRegion);
				final int limit = listeners.length;

				for (int n = 0; n < limit; ++n) {
					listeners[n].bake(this, blockStateRenderer);
				}

				contextRegion = null;
			}
		}
	}

	@Override
	public BlockAndTintGetter blockView() {
		return contextRegion;
	}

	@Override
	public BlockPos origin() {
		return this$1.getOrigin();
	}
}
