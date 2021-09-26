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

package io.vram.frex.api.light;

import io.vram.frex.api.config.FrexFeature;
import io.vram.frex.impl.light.HeldItemLightListenerImpl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface HeldItemLightListener {
	/**
	 * Use this event to add or modify held item lighting due to effects, worn items
	 * or other conditions that aren't completely dependent on held items. The renderer will
	 * call this event once per frame after the held light is retrieved for the current player
	 * or camera entity.
	 *
	 * <p>Held lights for non-camera entities are currently not supported.
	 *
	 * @param holdingEntity The entity that is holding (or not holding) a light provider.
	 *                      Currently this is always the camera entity.
	 *
	 * @param heldStack     The item stack that was used to determine the default result.
	 *                      May be empty. If no light was found, will be from the secondary hand.
	 *
	 * @param defaultResult The light result determined by the renderer. Will be the same in all
	 *                      listener invocations. Use this to know if another listener has
	 *                      already changed the current result.
	 *
	 * @param currentResult The light result that should be returned if the listener does not
	 *                      modify it. May already have been changed by a prior listener.
	 *                      Compare with default result to detect this.
	 *
	 * @return              The light that should be used. Return {@link ItemLight#NONE} to disable
	 *                      held light. Can be modified by subsequent listener invocations.
	 */
	ItemLight onGetHeldItemLight(LivingEntity holdingEntity, ItemStack heldStack, ItemLight defaultResult, ItemLight currentResult);

	static void register(HeldItemLightListener listener) {
		HeldItemLightListenerImpl.register(listener);
	}

	/**
	 * For use by renderer implementations.
	 * Renderers that implement item lights should declare {@link FrexFeature#HELD_ITEM_LIGHTS}.
	 */
	static ItemLight apply(LivingEntity holdingEntity, ItemStack heldStack, ItemLight defaultResult) {
		return HeldItemLightListenerImpl.apply(holdingEntity, heldStack, defaultResult);
	}
}
