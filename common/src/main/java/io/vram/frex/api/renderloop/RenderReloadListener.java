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
