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

import io.vram.frex.api.renderloop.BlockOutlineListener;
import io.vram.frex.api.renderloop.BlockOutlineListener.BlockOutlineContext;
import io.vram.frex.api.renderloop.WorldRenderContext;

public class BlockOutlineListenerImpl {
	private static final ObjectArrayList<BlockOutlineListener> LISTENERS = new ObjectArrayList<>();
	private static BlockOutlineListener active = (wCtx, bCtx) -> true;

	public static void register(BlockOutlineListener listener) {
		LISTENERS.add(listener);

		if (LISTENERS.size() == 1) {
			active = LISTENERS.get(0);
		} else if (LISTENERS.size() == 2) {
			active = (wCtx, bCtx) -> {
				final int limit = LISTENERS.size();
				boolean result = true;

				for (int i = 0; i < limit; ++i) {
					if (!LISTENERS.get(i).onBlockOutline(wCtx, bCtx)) {
						result = false;
					}
				}

				return result;
			};
		}
	}

	public static boolean invoke(WorldRenderContext worldRenderContext, BlockOutlineContext blockOutlineContext) {
		return active.onBlockOutline(worldRenderContext, blockOutlineContext);
	}
}
