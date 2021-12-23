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

package io.vram.frex.api.renderloop;

import io.vram.frex.impl.renderloop.EntityRenderPreListenerImpl;

/**
 * Called after the Solid, Cutout and Cutout Mipped terrain layers have been output to the framebuffer.
 *
 * <p>Use to render non-translucent terrain to the framebuffer.
 *
 * <p>Note that 3rd-party renderers may combine these passes or otherwise alter the
 * rendering pipeline for sake of performance or features. This can break direct writes to the
 * framebuffer.  Use this event for cases that cannot be satisfied by FabricBakedModel,
 * BlockEntityRenderer or other existing abstraction. If at all possible, use an existing terrain
 * RenderLayer instead of outputting to the framebuffer directly with GL calls.
 *
 * <p>The consumer is responsible for setup and tear down of GL state appropriate for the intended output.
 *
 * <p>Because solid and cutout quads are depth-tested, order of output does not matter except to improve
 * culling performance, which should not be significant after primary terrain rendering. This means
 * mods that currently hook calls to individual render layers can simply execute them all at once when
 * the event is called.
 *
 * <p>This event fires before entities and block entities are rendered and may be useful to prepare them.
 */
@FunctionalInterface
public interface EntityRenderPreListener {
	void beforeEntityRender(WorldRenderContext context);

	static void register(EntityRenderPreListener listener) {
		EntityRenderPreListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static void invoke(WorldRenderContext context) {
		EntityRenderPreListenerImpl.invoke(context);
	}
}
