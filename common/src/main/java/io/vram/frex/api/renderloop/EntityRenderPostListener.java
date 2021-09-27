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

import io.vram.frex.impl.renderloop.EntityRenderPostListenerImpl;

/**
 * Called after entities are rendered and solid entity layers
 * have been drawn to the main frame buffer target, before
 * block entity rendering begins.
 *
 * <p>Use for global block entity render setup, or
 * to append block-related quads to the entity consumers using the
 * {@VertexConsumerProvider} from the provided context. This
 * will generally give better (if not perfect) results
 * for non-terrain translucency vs. drawing directly later on.
 */
@FunctionalInterface
public interface EntityRenderPostListener {
	void afterEntityRender(WorldRenderContext context);

	static void register(EntityRenderPostListener listener) {
		EntityRenderPostListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static void invoke(WorldRenderContext context) {
		EntityRenderPostListenerImpl.invoke(context);
	}
}
