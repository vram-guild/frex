/*
 * Copyright © Original Authors
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

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import io.vram.frex.api.world.BlockEntityRenderData;
import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.impl.world.ChunkRenderConditionContext;
import io.vram.frex.pastel.mixinterface.RenderChunkRegionExt;

// PERF: find a way to disable redundant Fabric MixinChunkRendeRegion mixin for fabric RenderAttachedBlockview
@Mixin(RenderRegionCache.class)
public abstract class MixinRenderRegionCache {
	private static final AtomicInteger FRX_ERROR_COUNTER = new AtomicInteger();
	private static final Logger FRX_LOGGER = LogManager.getLogger();

	// For RenderRegionBakeListener
	@Unique
	private @Nullable RenderRegionBakeListener[] listeners;

	private static final ThreadLocal<ChunkRenderConditionContext> TRANSFER_POOL = ThreadLocal.withInitial(ChunkRenderConditionContext::new);

	@Inject(at = @At("RETURN"), method = "createRegion", locals = LocalCapture.CAPTURE_FAILHARD)
	public void onCreateRegion(Level level, BlockPos posFrom, BlockPos posTo, int radius, CallbackInfoReturnable<RenderChunkRegion> ci, int i, int j, int k, int l, RenderRegionCache.ChunkInfo[][] chunkInfos) {
		Long2ObjectOpenHashMap<Object> dataObjects = null;

		for (final RenderRegionCache.ChunkInfo[] chunkOuter : chunkInfos) {
			for (final RenderRegionCache.ChunkInfo chunk : chunkOuter) {
				// Hash maps in chunks should generally not be modified outside of client thread
				// but does happen in practice, due to mods or inconsistent vanilla behaviors, causing
				// CMEs when we iterate the map.  (Vanilla does not iterate these maps when it builds
				// the chunk cache and does not suffer from this problem.)
				//
				// We handle this simply by retrying until it works.  Ugly but effective.
				for (;;) {
					try {
						dataObjects = mapChunk(chunk.chunk(), posFrom, posTo, dataObjects);
						break;
					} catch (final ConcurrentModificationException e) {
						final int count = FRX_ERROR_COUNTER.incrementAndGet();

						if (count <= 5) {
							FRX_LOGGER.warn("[Render Data Attachment] Encountered CME during render region build. A mod is accessing or changing chunk data outside the main thread. Retrying.", e);

							if (count == 5) {
								FRX_LOGGER.info("[Render Data Attachment] Subsequent exceptions will be suppressed.");
							}
						}
					}
				}
			}
		}

		final var region = ci.getReturnValue();

		if (dataObjects != null && region != null) {
			((RenderChunkRegionExt) region).frx_setBlockEntityRenderData(dataObjects);
		}
	}

	private static Long2ObjectOpenHashMap<Object> mapChunk(LevelChunk chunk, BlockPos posFrom, BlockPos posTo, Long2ObjectOpenHashMap<Object> map) {
		final int xMin = posFrom.getX();
		final int xMax = posTo.getX();
		final int zMin = posFrom.getZ();
		final int zMax = posTo.getZ();
		final int yMin = posFrom.getY();
		final int yMax = posTo.getY();

		for (final Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
			final BlockPos entPos = entry.getKey();

			if (entPos.getX() >= xMin && entPos.getX() <= xMax
					&& entPos.getY() >= yMin && entPos.getY() <= yMax
					&& entPos.getZ() >= zMin && entPos.getZ() <= zMax) {
				final Object o = BlockEntityRenderData.get(entry.getValue());

				if (o != null) {
					if (map == null) {
						map = new Long2ObjectOpenHashMap<>();
					}

					map.put(entPos.asLong(), o);
				}
			}
		}

		return map;
	}

	@Inject(method = "createRegion", at = @At("HEAD"))
	private void beforeCreateRegion(Level level, BlockPos startPos, BlockPos endPos, int i, CallbackInfoReturnable<RenderChunkRegion> cir) {
		final ChunkRenderConditionContext context = TRANSFER_POOL.get().prepare(level, startPos.getX() + 1, startPos.getY() + 1, startPos.getZ() + 1);
		RenderRegionBakeListener.prepareInvocations(context, context.listeners);
	}

	@Inject(method = "isAllEmpty", at = @At("RETURN"), cancellable = true)
	private static void onIsAllEmpty(BlockPos startPos, BlockPos endPos, int i, int j, RenderRegionCache.ChunkInfo[][] chunkInfos, CallbackInfoReturnable<Boolean> cir) {
		// if empty but has listeners, force it to build
		if (cir.getReturnValueZ() && !TRANSFER_POOL.get().listeners.isEmpty()) {
			cir.setReturnValue(false);
		}
	}
}
