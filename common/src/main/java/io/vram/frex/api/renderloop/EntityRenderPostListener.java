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
