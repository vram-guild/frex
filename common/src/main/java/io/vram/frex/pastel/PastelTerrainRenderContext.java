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

import static io.vram.frex.base.renderer.util.EncoderUtil.colorizeQuad;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.api.math.PackedSectionPos;
import io.vram.frex.api.model.BlockModel;
import io.vram.frex.base.renderer.ao.AoCalculator;
import io.vram.frex.base.renderer.context.input.BaseBlockInputContext;
import io.vram.frex.base.renderer.context.render.BlockRenderContext;
import io.vram.frex.base.renderer.util.EncoderUtil;
import io.vram.frex.pastel.util.RenderChunkRegionExt;

public class PastelTerrainRenderContext extends BlockRenderContext<RenderChunkRegion> {
	protected RenderChunkRegionExt regionExt;

	private final AoCalculator aoCalc = new AoCalculator() {
		@Override
		protected int ao(int cacheIndex) {
			return regionExt.frx_cachedAoLevel(cacheIndex);
		}

		@Override
		protected int brightness(int cacheIndex) {
			return regionExt.frx_cachedBrightness(cacheIndex);
		}

		@Override
		protected boolean isOpaque(int cacheIndex) {
			return regionExt.frx_isClosed(cacheIndex);
		}

		@Override
		protected int cacheIndexFromSectionIndex(int packedSectorIndex) {
			return packedSectorIndex;
		}
	};

	@Override
	protected BaseBlockInputContext<RenderChunkRegion> createInputContext() {
		return new BaseBlockInputContext<>() {
			@Override
			protected int fastBrightness(BlockPos pos) {
				return regionExt.frx_cachedBrightness(pos);
			}

			@Override
			public @Nullable Object blockEntityRenderData(BlockPos pos) {
				return regionExt.frx_getBlockEntityRenderData(pos);
			}
		};
	}

	public PastelTerrainRenderContext prepareForRegion(RenderChunkRegion region, PoseStack poseStack, BlockPos origin) {
		inputContext.prepareForWorld(region, true, (MatrixStack) poseStack);
		regionExt = (RenderChunkRegionExt) region;
		regionExt.frx_setContext(this, origin);
		return this;
	}

	public void renderFluid(BlockState blockState, BlockPos blockPos, boolean defaultAo, final BlockModel model) {
		aoCalc.prepare(PackedSectionPos.packWithSectionMask(blockPos));
		prepareForFluid(blockState, blockPos, defaultAo);
		renderInner(model);
	}

	public void renderBlock(BlockState blockState, BlockPos blockPos, boolean defaultAo, final BakedModel model) {
		aoCalc.prepare(PackedSectionPos.packWithSectionMask(blockPos));
		prepareForBlock(model, blockState, blockPos, defaultAo);
		renderInner((BlockModel) model);
	}

	// PERF: don't pass in matrixStack each time, just change model matrix directly
	private void renderInner(final BlockModel model) {
		try {
			model.renderAsBlock(this.inputContext, emitter());
		} catch (final Throwable var9) {
			final CrashReport crashReport_1 = CrashReport.forThrowable(var9, "Tesselating block in world - Canvas Renderer");
			final CrashReportCategory crashReportElement_1 = crashReport_1.addCategory("Block being tesselated");
			CrashReportCategory.populateBlockDetails(crashReportElement_1, inputContext.blockView(), inputContext.pos(), inputContext.blockState());
			throw new ReportedException(crashReport_1);
		}
	}

	@Override
	protected void shadeQuad() {
		// needs to happen before offsets are applied
		if (!emitter.material().disableAo() && Minecraft.useAmbientOcclusion()) {
			aoCalc.compute(emitter);
		} else if (PastelRenderer.semiFlatLighting) {
			aoCalc.computeFlat(emitter);
		} else {
			EncoderUtil.applyFlatLighting(emitter, inputContext.flatBrightness(emitter));
		}

		colorizeQuad(emitter, this.inputContext);
	}

	@Override
	protected void encodeQuad() {
		// WIP: handle non-default render layers - will need to capture immediate
		EncoderUtil.encodeQuad(emitter, inputContext, defaultConsumer);
	}

	public static final ThreadLocal<PastelTerrainRenderContext> POOL = ThreadLocal.withInitial(PastelTerrainRenderContext::new);
}
