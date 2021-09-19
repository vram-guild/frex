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

package grondag.frex.mixin;

import java.util.Random;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.frex.impl.event.BlockStateRendererImpl;
import grondag.frex.impl.event.ChunkRenderConditionContext.RenderRegionListenerProvider;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask")
public class MixinChunkBuilder implements RenderRegionContext {
	@Shadow protected RenderChunk field_20839;

	@Unique
	private final BlockStateRendererImpl blockStateRenderer = new BlockStateRendererImpl();

	// could shadow this but is set to null by the time we need it
	@Unique
	private RenderChunkRegion contextRegion;

	@Inject(method = "Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk$RebuildTask;render(FFFLnet/minecraft/client/render/chunk/ChunkBuilder$ChunkData;Lnet/minecraft/client/render/chunk/BlockBufferBuilderStorage;)Ljava/util/Set;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;iterate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onRender(float cameraX, float cameraY, float cameraZ, ChunkRenderDispatcher.CompiledChunk data, ChunkBufferBuilderPack buffers, CallbackInfoReturnable<Set<BlockEntity>> cir, int i, BlockPos blockPos, BlockPos blockPos2, VisGraph chunkOcclusionDataBuilder, Set<BlockEntity> set, RenderChunkRegion chunkRendererRegion, PoseStack matrixStack, Random random, BlockRenderDispatcher blockRenderManager) {
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
		return field_20839.getOrigin();
	}
}
