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
