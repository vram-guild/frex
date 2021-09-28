/*
 * Copyright © Contributing Authors
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
 *
 * Additional copyright and licensing notices may apply for content that was
 * included from other projects. For more information, see ATTRIBUTION.md.
 */

package io.vram.frex.impl.renderloop;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.phys.HitResult;

import io.vram.frex.api.renderloop.BlockOutlinePreListener;
import io.vram.frex.api.renderloop.WorldRenderContext;

public class BlockOutlinePreListenerImpl {
	private static final ObjectArrayList<BlockOutlinePreListener> LISTENERS = new ObjectArrayList<>();
	private static BlockOutlinePreListener active = (ctx, hit) -> true;

	public static void register(BlockOutlinePreListener listener) {
		LISTENERS.add(listener);

		if (LISTENERS.size() == 1) {
			active = LISTENERS.get(0);
		} else if (LISTENERS.size() == 2) {
			active = (ctx, hit) -> {
				final int limit = LISTENERS.size();
				boolean result = true;

				for (int i = 0; i < limit; ++i) {
					if (!LISTENERS.get(i).beforeBlockOutline(ctx, hit)) {
						result = false;
					}
				}

				return result;
			};
		}
	}

	public static boolean invoke(WorldRenderContext ctx, HitResult hit) {
		return active.beforeBlockOutline(ctx, hit);
	}
}
