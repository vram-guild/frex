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

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

import io.vram.frex.base.renderer.context.render.ItemRenderContext;
import io.vram.frex.base.renderer.util.EncoderUtil;

public class PastelItemRenderContext extends ItemRenderContext {
	private static final Supplier<ThreadLocal<PastelItemRenderContext>> POOL_FACTORY = () -> ThreadLocal.withInitial(() -> {
		final PastelItemRenderContext result = new PastelItemRenderContext();
		return result;
	});

	private static ThreadLocal<PastelItemRenderContext> POOL = POOL_FACTORY.get();

	// WIP: call this
	public static void reload() {
		POOL = POOL_FACTORY.get();
	}

	public static PastelItemRenderContext get() {
		return POOL.get();
	}

	protected VertexConsumer defaultConsumer;

	protected MultiBufferSource vertexConsumers;

	public PastelItemRenderContext() {
		super();
	}

	@Override
	protected void encodeQuad() {
		// WIP: handle non-default render layers - will need to capture immediate
		EncoderUtil.encodeQuad(emitter, inputContext, defaultConsumer);
	}

	@Override
	protected void prepareEncoding(MultiBufferSource vertexConsumers) {
		defaultConsumer = vertexConsumers.getBuffer(inputContext.defaultRenderType());
		this.vertexConsumers = vertexConsumers;
	}

	@Override
	protected void renderCustomModel(BlockEntityWithoutLevelRenderer builtInRenderer, MultiBufferSource vertexConsumers) {
		builtInRenderer.renderByItem(inputContext.itemStack(), inputContext.mode(), inputContext.matrixStack().asPoseStack(), vertexConsumers, inputContext.lightmap(), inputContext.overlay());
	}
}
