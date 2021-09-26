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

package io.vram.frex.mixin;

import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.impl.event.ChunkRenderConditionContext;
import io.vram.frex.impl.event.ChunkRenderConditionContext.RenderRegionListenerProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(RenderChunkRegion.class)
public class MixinRenderChunkRegion implements RenderRegionListenerProvider {
	@Unique
	private @Nullable RenderRegionBakeListener[] listeners;

	private static final ThreadLocal<ChunkRenderConditionContext> TRANSFER_POOL = ThreadLocal.withInitial(ChunkRenderConditionContext::new);

	@Inject(method = "isAllEmpty", at = @At("RETURN"), cancellable = true)
	private static void isChunkEmpty(BlockPos startPos, BlockPos endPos, int i, int j, LevelChunk[][] levelChunks, CallbackInfoReturnable<Boolean> cir) {
		// even if region not empty we still test here and capture listeners here
		final ChunkRenderConditionContext context = TRANSFER_POOL.get().prepare(startPos.getX() + 1, startPos.getY() + 1, startPos.getZ() + 1);
		RenderRegionBakeListener.prepareInvocations(context, context.listeners);

		if (cir.getReturnValueZ() && !context.listeners.isEmpty()) {
			// if empty but has listeners, force it to build
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "<init>*", at = @At("RETURN"))
	private void onInit(Level world, int chunkX, int chunkZ, LevelChunk[][] chunks, BlockPos startPos, BlockPos endPos, CallbackInfo ci) {
		// capture our predicate search results while still on the same thread - will happen right after the hook above
		listeners = TRANSFER_POOL.get().getListeners();
	}

	@Override
	public @Nullable RenderRegionBakeListener[] frex_getRenderRegionListeners() {
		return listeners;
	}
}
