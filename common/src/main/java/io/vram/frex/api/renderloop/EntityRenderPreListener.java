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
