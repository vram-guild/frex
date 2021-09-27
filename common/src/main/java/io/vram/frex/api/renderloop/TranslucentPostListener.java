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

import io.vram.frex.impl.renderloop.TranslucentPostListenerImpl;

/**
 * Called after entity, terrain, and particle translucent layers have been
 * drawn to the framebuffer but before translucency combine has happened
 * in fabulous mode.
 *
 * <p>Use for drawing overlays or other effects on top of those targets
 * (or the main target when fabulous isn't active) before clouds and weather
 * are drawn.  However, note that {@code WorldRenderPostEntityCallback} will
 * offer better results in most use cases.
 *
 * <p>Vertex consumers are not available in this event because all buffered quads
 * are drawn before this event is called.  Any rendering here must be drawn
 * directly to the frame buffer.  The render state matrix will not include
 * camera transformation, so {@link #LAST} may be preferable if that is wanted.
 */
@FunctionalInterface
public interface TranslucentPostListener {
	void afterTranslcuentRender(WorldRenderContext context);

	static void register(TranslucentPostListener listener) {
		TranslucentPostListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static void invoke(WorldRenderContext context) {
		TranslucentPostListenerImpl.invoke(context);
	}
}
