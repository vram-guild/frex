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

package io.vram.frex.pastel.mixin;

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

import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection.RebuildTask;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection.RebuildTask.CompileResults;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
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
import io.vram.frex.pastel.mixinterface.RenderChunkRegionExt;

@Mixin(RebuildTask.class)
public abstract class MixinChunkRebuildTask implements RenderRegionContext<BlockAndTintGetter> {
	//e -> field_20839 -> this$1
	@Shadow(aliases = {"field_20839"}) protected RenderSection this$1;

	// Below are for RenderRegionBakeListener support

	@Unique
	private final PastelBlockStateRenderer blockStateRenderer = new PastelBlockStateRenderer();

	// could shadow this but is set to null by the time we need it
	@Unique
	private RenderChunkRegion contextRegion;

	@Unique
	private final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

	@Inject(method = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask;compile(FFFLnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask$CompileResults;",
				require = 1, locals = LocalCapture.CAPTURE_FAILHARD,
				at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;create()Lnet/minecraft/util/RandomSource;"))
	private void regionStartHook(float arg0, float arg1, float arg2, SectionBufferBuilderPack arg3, CallbackInfoReturnable<CompileResults> cir, CompileResults compileResults, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph visGraph, RenderChunkRegion renderChunkRegion, PoseStack poseStack, @SuppressWarnings("rawtypes") Set set) {
		final PastelTerrainRenderContext context = PastelTerrainRenderContext.POOL.get();
		((RenderChunkRegionExt) renderChunkRegion).frx_setContext(context, this$1.getOrigin());
		context.prepareForRegion(renderChunkRegion, poseStack, blockPos, arg3, set);

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

	@Redirect(method = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask;compile(FFFLnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask$CompileResults;",
			require = 1, at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V"))
	private void blockRenderHook(BlockRenderDispatcher renderManager, BlockState blockState, BlockPos blockPos, BlockAndTintGetter blockView, PoseStack matrix, VertexConsumer bufferBuilder, boolean checkSides, RandomSource random) {
		if (blockState.getRenderShape() == RenderShape.MODEL) {
			final Vec3 vec3d = blockState.getOffset(blockView, blockPos);

			if (vec3d != Vec3.ZERO) {
				MatrixStack.fromVanilla(matrix).translate((float) vec3d.x, (float) vec3d.y, (float) vec3d.z);
			}

			((RenderChunkRegionExt) blockView).frx_getContext().renderBlock(blockState, blockPos, renderManager.getBlockModel(blockState));
		}
	}

	@Redirect(method = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask;compile(FFFLnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask$CompileResults;",
			require = 1, at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"))
	private void fluidRenderHook(BlockRenderDispatcher renderManager, BlockPos blockPos, BlockAndTintGetter blockView, VertexConsumer vertexConsumer, BlockState currentBlockState, FluidState fluidState) {
		((RenderChunkRegionExt) blockView).frx_getContext().renderFluid(currentBlockState, blockPos, FluidModel.get(fluidState.getType()));
	}

	@Inject(at = @At("RETURN"), method = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask;compile(FFFLnet/minecraft/client/renderer/SectionBufferBuilderPack;)Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection$RebuildTask$CompileResults;")
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
