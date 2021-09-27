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

import io.vram.frex.impl.renderloop.FrustumSetupListenerImpl;

/**
 * Called after view Frustum is computed and all render chunks to be rendered are
 * identified and rebuilt but before chunks are uploaded to GPU.
 *
 * <p>Use for setup of state that depends on view frustum.
 */
@FunctionalInterface
public interface FrustumSetupListener {
	void afterFrustumSetup(WorldRenderContext context);

	static void register(FrustumSetupListener listener) {
		FrustumSetupListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static void invoke(WorldRenderContext context) {
		FrustumSetupListenerImpl.invoke(context);
	}
}
