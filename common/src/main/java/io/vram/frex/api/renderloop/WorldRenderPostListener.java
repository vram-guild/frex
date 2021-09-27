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
