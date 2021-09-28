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

package io.vram.frex.api.renderloop;

import io.vram.frex.impl.renderloop.WorldRenderPostListenerImpl;

/**
 * Called after all world rendering is complete and changes to GL state are unwound.
 *
 * <p>Use to draw overlays that handle GL state management independently or to tear
 * down transient state in event handlers or as a hook that precedes hand/held item
 * and GUI rendering.
 */
@FunctionalInterface
public interface WorldRenderPostListener {
	void afterWorldRender(WorldRenderContext context);

	static void register(WorldRenderPostListener listener) {
		WorldRenderPostListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static void invoke(WorldRenderContext context) {
		WorldRenderPostListenerImpl.invoke(context);
	}
}
