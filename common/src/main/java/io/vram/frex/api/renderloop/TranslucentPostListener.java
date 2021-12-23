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
