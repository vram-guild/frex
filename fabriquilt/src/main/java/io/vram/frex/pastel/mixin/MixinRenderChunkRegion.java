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

import java.util.BitSet;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import io.vram.frex.api.math.PackedSectionPos;
import io.vram.frex.api.world.BlockEntityRenderData;
import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.impl.world.ChunkRenderConditionContext;
import io.vram.frex.pastel.PastelTerrainRenderContext;
import io.vram.frex.pastel.mixinterface.RenderChunkRegionExt;

// PERF: find a way to disable redundant Fabric MixinChunkRendeRegion mixin for fabric RenderAttachedBlockview
@Mixin(RenderChunkRegion.class)
public abstract class MixinRenderChunkRegion implements RenderChunkRegionExt {
	@Shadow
	protected abstract int index(BlockPos pos);

	@Shadow
	protected abstract int index(int x, int y, int z);

	@Shadow protected Level level;

	private PastelTerrainRenderContext context;
	private int originX, originY, originZ;
	private final Int2IntOpenHashMap brightnessCache = new Int2IntOpenHashMap(4096, Hash.FAST_LOAD_FACTOR);
	private final Int2IntOpenHashMap aoLevelCache = new Int2IntOpenHashMap(4096, Hash.FAST_LOAD_FACTOR);
	private final MutableBlockPos searchPos = new MutableBlockPos();
	private final BitSet closedCheckBits = new BitSet();
	private final BitSet closedResultBits = new BitSet();
	private Int2ObjectOpenHashMap<Object> renderDataObjects;

	private static final AtomicInteger FRX_ERROR_COUNTER = new AtomicInteger();
	private static final Logger FRX_LOGGER = LogManager.getLogger();

	// For RenderRegionBakeListener
	@Unique
	private @Nullable RenderRegionBakeListener[] listeners;

	private static final ThreadLocal<ChunkRenderConditionContext> TRANSFER_POOL = ThreadLocal.withInitial(ChunkRenderConditionContext::new);

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onNew(Level level, int cxOff, int czOff, LevelChunk[][] levelChunks, BlockPos posFrom, BlockPos posTo, CallbackInfo ci) {
		brightnessCache.defaultReturnValue(Integer.MAX_VALUE);
		aoLevelCache.defaultReturnValue(Integer.MAX_VALUE);

		Int2ObjectOpenHashMap<Object> dataObjects = null;

		for (final LevelChunk[] chunkOuter : levelChunks) {
			for (final LevelChunk chunk : chunkOuter) {
				// Hash maps in chunks should generally not be modified outside of client thread
				// but does happen in practice, due to mods or inconsistent vanilla behaviors, causing
				// CMEs when we iterate the map.  (Vanilla does not iterate these maps when it builds
				// the chunk cache and does not suffer from this problem.)
				//
				// We handle this simply by retrying until it works.  Ugly but effective.
				for (;;) {
					try {
						dataObjects = frx_mapChunk(chunk, posFrom, posTo, dataObjects);
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

		renderDataObjects = dataObjects;
	}

	@Unique
	private Int2ObjectOpenHashMap<Object> frx_mapChunk(LevelChunk chunk, BlockPos posFrom, BlockPos posTo, Int2ObjectOpenHashMap<Object> map) {
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
						map = new Int2ObjectOpenHashMap<>();
					}

					map.put(index(entPos), o);
				}
			}
		}

		return map;
	}

	@Unique
	@Override
	public @Nullable Object frx_getBlockEntityRenderData(BlockPos pos) {
		return renderDataObjects == null ? null : renderDataObjects.get(index(pos));
	}

	@Unique
	private int frx_blockPosToSectionPos(BlockPos pos) {
		return PackedSectionPos.pack(pos.getX() - originX, pos.getY() - originY, pos.getZ() - originZ);
	}

	@Unique
	private MutableBlockPos frx_sectionPosToSearchPos(int packedSectionPos) {
		return searchPos.set(
			PackedSectionPos.unpackSectionX(packedSectionPos) + originX,
			PackedSectionPos.unpackSectionY(packedSectionPos) + originY,
			PackedSectionPos.unpackSectionZ(packedSectionPos) + originZ
		);
	}

	@Unique
	@Override
	public PastelTerrainRenderContext frx_getContext() {
		return context;
	}

	@Unique
	@Override
	public void frx_setContext(PastelTerrainRenderContext context, BlockPos origin) {
		this.context = context;
		originX = origin.getX();
		originY = origin.getY();
		originZ = origin.getZ();
	}

	@Unique
	@Override
	public int frx_cachedAoLevel(int packedSectionPos) {
		int result = aoLevelCache.get(packedSectionPos);

		if (result == Integer.MAX_VALUE) {
			final var pos = frx_sectionPosToSearchPos(packedSectionPos);
			final var blockView = (RenderChunkRegion) (Object) this;
			final BlockState state = blockView.getBlockState(pos);

			if (state.getLightEmission() == 0) {
				result = Math.round(255f * state.getShadeBrightness(blockView, pos));
			} else {
				result = 255;
			}

			aoLevelCache.put(packedSectionPos, result);
		}

		return result;
	}

	@Unique
	@Override
	public int frx_cachedBrightness(int packedSectionPos) {
		int result = brightnessCache.get(packedSectionPos);

		if (result == Integer.MAX_VALUE) {
			final var pos = frx_sectionPosToSearchPos(packedSectionPos);
			final var blockView = (RenderChunkRegion) (Object) this;
			result = LevelRenderer.getLightColor(blockView, blockView.getBlockState(pos), pos);
			brightnessCache.put(packedSectionPos, result);
		}

		return result;
	}

	@Unique
	@Override
	public int frx_cachedBrightness(BlockPos pos) {
		return frx_cachedBrightness(frx_blockPosToSectionPos(pos));
	}

	@Unique
	@Override
	public boolean frx_isClosed(int packedSectionPos) {
		if (closedCheckBits.get(packedSectionPos)) {
			return closedResultBits.get(packedSectionPos);
		}

		final var pos = frx_sectionPosToSearchPos(packedSectionPos);
		final var blockView = (RenderChunkRegion) (Object) this;
		final var blockState = blockView.getBlockState(pos);
		final boolean result = blockState.isSolidRender(blockView, pos);
		closedCheckBits.set(packedSectionPos);

		if (result) {
			closedResultBits.set(packedSectionPos);
		}

		return result;
	}

	@Inject(method = "createIfNotEmpty", at = @At("HEAD"))
	private static void onCreateIfNotEmpty(Level level, BlockPos startPos, BlockPos endPos, int i, CallbackInfoReturnable<RenderChunkRegion> cir) {
		final ChunkRenderConditionContext context = TRANSFER_POOL.get().prepare(level, startPos.getX() + 1, startPos.getY() + 1, startPos.getZ() + 1);
		RenderRegionBakeListener.prepareInvocations(context, context.listeners);
	}

	@Inject(method = "isAllEmpty", at = @At("RETURN"), cancellable = true)
	private static void isChunkEmpty(BlockPos startPos, BlockPos endPos, int i, int j, LevelChunk[][] levelChunks, CallbackInfoReturnable<Boolean> cir) {
		// if empty but has listeners, force it to build
		if (cir.getReturnValueZ() && !TRANSFER_POOL.get().listeners.isEmpty()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "<init>*", at = @At("RETURN"))
	private void onInit(Level world, int chunkX, int chunkZ, LevelChunk[][] chunks, BlockPos startPos, BlockPos endPos, CallbackInfo ci) {
		// capture our predicate search results while still on the same thread - will happen right after the hook above
		listeners = TRANSFER_POOL.get().getListeners();
	}

	@Override
	public @Nullable RenderRegionBakeListener[] frx_getRenderRegionListeners() {
		return listeners;
	}

	@Override
	public Biome frx_getBiome(BlockPos pos) {
		return level.getBiome(pos);
	}
}
