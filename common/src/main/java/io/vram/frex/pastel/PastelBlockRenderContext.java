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

import io.vram.frex.base.renderer.context.render.SimpleBlockRenderContext;
import io.vram.frex.base.renderer.util.EncoderUtil;

/**
 * Default context for non-terrain block rendering when multi-buffer not available.
 */
public class PastelBlockRenderContext extends SimpleBlockRenderContext {
	private static final Supplier<ThreadLocal<PastelBlockRenderContext>> POOL_FACTORY = () -> ThreadLocal.withInitial(() -> {
		final PastelBlockRenderContext result = new PastelBlockRenderContext();
		return result;
	});

	private static ThreadLocal<PastelBlockRenderContext> POOL = POOL_FACTORY.get();

	public static void reload() {
		POOL = POOL_FACTORY.get();
	}

	public static PastelBlockRenderContext get() {
		return POOL.get();
	}

	public PastelBlockRenderContext() {
		super();
	}

	@Override
	protected void encodeQuad() {
		EncoderUtil.encodeQuad(emitter, inputContext, defaultConsumer);
	}
}
