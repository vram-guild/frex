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

package io.vram.frex.pastel;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.base.renderer.context.render.EntityBlockRenderContext;
import io.vram.frex.base.renderer.util.EncoderUtil;

/**
 * Context for non-terrain block rendering when multi-buffer available.
 * Includes logic for rendering item frame itself, which acts like a block model but
 * has no block state.
 */
public class PastelEntityBlockRenderContext extends EntityBlockRenderContext {
	private static final Supplier<ThreadLocal<PastelEntityBlockRenderContext>> POOL_FACTORY = () -> ThreadLocal.withInitial(() -> {
		final PastelEntityBlockRenderContext result = new PastelEntityBlockRenderContext();
		return result;
	});

	private static ThreadLocal<PastelEntityBlockRenderContext> POOL = POOL_FACTORY.get();

	public static void reload() {
		POOL = POOL_FACTORY.get();
	}

	public static PastelEntityBlockRenderContext get() {
		return POOL.get();
	}

	protected MultiBufferSource vertexConsumers;

	public PastelEntityBlockRenderContext() {
		super();
	}

	@Override
	protected void prepareEncoding(MultiBufferSource vertexConsumers) {
		this.vertexConsumers = vertexConsumers;
	}

	@Override
	protected void shadeQuad() {
		emitter.applyFlatLighting(inputContext.flatBrightness(emitter));
		emitter.colorize(inputContext);
		applySimpleDiffuseShade();
	}

	@Override
	protected void encodeQuad() {
		RenderType renderType;
		final var mat = emitter.material();

		// NB: by the time we get here material should be fully specified - no default preset
		assert mat.preset() != MaterialConstants.PRESET_DEFAULT;

		if (mat.transparency() != MaterialConstants.TRANSPARENCY_NONE) {
			renderType = Minecraft.useShaderTransparency() ? Sheets.translucentItemSheet() : Sheets.translucentCullBlockSheet();
		} else {
			renderType = mat.cutout() == MaterialConstants.CUTOUT_NONE ? Sheets.solidBlockSheet() : Sheets.cutoutBlockSheet();
		}

		EncoderUtil.encodeQuad(emitter, inputContext, vertexConsumers.getBuffer(renderType));
	}
}
