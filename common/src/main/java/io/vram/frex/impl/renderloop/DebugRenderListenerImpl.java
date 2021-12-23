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

package io.vram.frex.impl.renderloop;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import io.vram.frex.api.renderloop.DebugRenderListener;
import io.vram.frex.api.renderloop.WorldRenderContext;

public class DebugRenderListenerImpl {
	private static final ObjectArrayList<DebugRenderListener> LISTENERS = new ObjectArrayList<>();
	private static DebugRenderListener active = ctx -> { };

	public static void register(DebugRenderListener listener) {
		LISTENERS.add(listener);

		if (LISTENERS.size() == 1) {
			active = LISTENERS.get(0);
		} else if (LISTENERS.size() == 2) {
			active = ctx -> {
				final int limit = LISTENERS.size();

				for (int i = 0; i < limit; ++i) {
					LISTENERS.get(i).beforeDebugRender(ctx);
				}
			};
		}
	}

	public static void invoke(WorldRenderContext ctx) {
		active.beforeDebugRender(ctx);
	}
}
