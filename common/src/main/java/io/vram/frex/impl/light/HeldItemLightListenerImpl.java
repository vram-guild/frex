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

package io.vram.frex.impl.light;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import io.vram.frex.api.light.HeldItemLightListener;
import io.vram.frex.api.light.ItemLight;

public abstract class HeldItemLightListenerImpl {
	private HeldItemLightListenerImpl() { }

	private static final ObjectArrayList<HeldItemLightListener> LIST = new ObjectArrayList<>();
	private static HeldItemLightListener active = (holdingEntity, heldStack, defaultResult, currentResult) -> currentResult;

	public static void register(HeldItemLightListener listener) {
		LIST.add(listener);

		if (LIST.size() == 1) {
			active = listener;
		} else if (LIST.size() == 2) {
			active = (holdingEntity, heldStack, defaultResult, currentResult) -> {
				final int limit = LIST.size();

				for (int i = 0; i < limit; ++i) {
					currentResult = LIST.get(i).onGetHeldItemLight(holdingEntity, heldStack, defaultResult, currentResult);
				}

				return currentResult;
			};
		}
	}

	public static ItemLight apply(LivingEntity holdingEntity, ItemStack heldStack, ItemLight defaultResult) {
		return active.onGetHeldItemLight(holdingEntity, heldStack, defaultResult, defaultResult);
	}
}
