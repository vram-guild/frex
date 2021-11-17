/*
 * Copyright © Original Authors
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
