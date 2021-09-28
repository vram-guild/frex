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

import net.minecraft.client.renderer.LevelRenderer;

import io.vram.frex.impl.renderloop.WorldRenderStartListenerImpl;

/**
 * Called before world rendering executes. Input parameters are available but frustum is not.
 * Use this event instead of injecting to the HEAD of {@link LevelRenderer#renderLevel} to avoid
 * compatibility problems with 3rd-party renderer implementations.
 *
 * <p>Use for setup of state that is needed during the world render call that
 * does not depend on the view frustum.
 */
@FunctionalInterface
public interface WorldRenderStartListener {
	void onStartWorldRender(WorldRenderContext context);

	static void register(WorldRenderStartListener listener) {
		WorldRenderStartListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static void invoke(WorldRenderContext context) {
		WorldRenderStartListenerImpl.invoke(context);
	}
}
