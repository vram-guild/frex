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

package io.vram.frex.pastel.mixin;

import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.api.model.fluid.FluidModel;
import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext;
import io.vram.frex.pastel.PastelBlockStateRenderer;
import io.vram.frex.pastel.PastelTerrainRenderContext;
import io.vram.frex.pastel.util.RenderChunkRegionExt;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public abstract class MixinChunkRebuildTask implements RenderRegionContext<BlockAndTintGetter> {
	//e -> field_20839 -> this$1
	@Shadow protected RenderChunk this$1;

	/** Holds block state for use in fluid render. */
	private BlockState currentBlockState;

	// Below are for RenderRegionBakeListener support

	@Unique
	private final PastelBlockStateRenderer blockStateRenderer = new PastelBlockStateRenderer();

	// could shadow this but is set to null by the time we need it
	@Unique
	private RenderChunkRegion contextRegion;

	@Unique
	private final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

	@Inject(method = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Ljava/util/Set;",
				require = 1, locals = LocalCapture.CAPTURE_FAILEXCEPTION,
				at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;enableCaching()V"))
	private void regionStartHook(float arg0, float arg1, float arg2, CompiledChunk arg3, ChunkBufferBuilderPack arg4, CallbackInfoReturnable<Set<?>> cir, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph visGraph, Set<?> set, RenderChunkRegion renderChunkRegion, PoseStack poseStack) {
		if (renderChunkRegion != null) {
			final PastelTerrainRenderContext context = PastelTerrainRenderContext.POOL.get();
			((RenderChunkRegionExt) renderChunkRegion).frx_setContext(context, this$1.getOrigin());
			context.prepareForRegion(renderChunkRegion, arg3, poseStack, blockPos, arg4);

			final RenderRegionBakeListener[] listeners = ((RenderChunkRegionExt) renderChunkRegion).frx_getRenderRegionListeners();

			if (listeners != null) {
				contextRegion = renderChunkRegion;
				blockStateRenderer.prepare(poseStack, renderChunkRegion);
				final int limit = listeners.length;

				for (int n = 0; n < limit; ++n) {
					final var listener = listeners[n];
					context.overrideBlockView(listener.blockViewOverride(renderChunkRegion));
					listener.bake(this, blockStateRenderer);
				}

				context.overrideBlockView(renderChunkRegion);
				contextRegion = null;
			}
		}
	}

	/** Capture block state for use in fluid render. */
	@Redirect(method = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Ljava/util/Set;",
			require = 1, at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState onGetBlockState(RenderChunkRegion blockView, BlockPos pos) {
		final var result = blockView.getBlockState(pos);
		currentBlockState = result;
		return result;
	}

	@Redirect(method = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Ljava/util/Set;",
			require = 1, at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/Random;)Z"))
	private boolean blockRenderHook(BlockRenderDispatcher renderManager, BlockState blockState, BlockPos blockPos, BlockAndTintGetter blockView, PoseStack matrix, VertexConsumer bufferBuilder, boolean checkSides, Random random) {
		if (blockState.getRenderShape() == RenderShape.MODEL) {
			final Vec3 vec3d = blockState.getOffset(blockView, blockPos);

			if (vec3d != Vec3.ZERO) {
				MatrixStack.cast(matrix).translate((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
			}

			((RenderChunkRegionExt) blockView).frx_getContext().renderBlock(blockState, blockPos, renderManager.getBlockModel(blockState));
			// we handle all buffer initialization/tracking in render context
			return false;
		} else {
			return false;
		}
	}

	@Redirect(method = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Ljava/util/Set;",
			require = 1, at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/material/FluidState;)Z"))
	private boolean fluidRenderHook(BlockRenderDispatcher renderManager, BlockPos blockPos, BlockAndTintGetter blockView, VertexConsumer vertexConsumer, FluidState fluidState) {
		((RenderChunkRegionExt) blockView).frx_getContext().renderFluid(currentBlockState, blockPos, false, FluidModel.get(fluidState.getType()));
		// we handle all initialization/tracking in render context
		return false;
	}

	@Inject(at = @At("RETURN"), method = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;compile(FFFLnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;Lnet/minecraft/client/renderer/ChunkBufferBuilderPack;)Ljava/util/Set;")
	private void hookRebuildChunkReturn(CallbackInfoReturnable<Set<BlockEntity>> ci) {
		PastelTerrainRenderContext.POOL.get().inputContext.release();
	}

	@Override
	public BlockAndTintGetter blockView() {
		return contextRegion;
	}

	@Override
	public BlockPos origin() {
		return this$1.getOrigin();
	}

	@Override
	public MutableBlockPos originOffset(int x, int y, int z) {
		final var origin = origin();
		return searchPos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
	}
}
