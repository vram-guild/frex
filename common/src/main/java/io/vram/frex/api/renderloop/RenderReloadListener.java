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

import io.vram.frex.impl.renderloop.RenderReloadListenerImpl;

/**
 * Called when the world renderer reloads, usually as result of changing resource pack
 * or video configuration, or when the player types F3+A in the debug screen.
 * Afterwards all render chunks will be reset and reloaded.
 *
 * <p>Render chunks and other render-related object instances will be made null
 * or invalid after this event so do not use it to capture dependent state.
 * Instead, use it to invalidate state and reinitialize lazily.
 */
@FunctionalInterface
public interface RenderReloadListener {
	void onRenderReload();

	static void register(RenderReloadListener listener) {
		RenderReloadListenerImpl.register(listener);
	}

	/**
	 * For use by renderer implementations.
	 */
	static void invoke() {
		RenderReloadListenerImpl.invoke();
	}
}
