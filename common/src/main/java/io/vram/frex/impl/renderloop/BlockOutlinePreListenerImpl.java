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
