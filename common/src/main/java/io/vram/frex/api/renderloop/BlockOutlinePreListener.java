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

import io.vram.frex.impl.renderloop.BlockOutlinePreListenerImpl;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

/**
 * Called before default block outline rendering and before checks are
 * done to determine if it should happen. Can optionally cancel the default
 * rendering but all event handlers will always be called.
 *
 * <p>Use this to decorate or replace the default block outline rendering
 * for specific modded blocks or when the need for a block outline render
 * would not be detected.  Normally, outline rendering will not happen for
 * entities, fluids, or other game objects that do not register a block-type hit.
 *
 * <p>Returning false from any event subscriber will cancel the default block
 * outline render and suppress the {@code BLOCK_RENDER} event.  This has no
 * effect on other subscribers to this event - all subscribers will always be called.
 * Canceling here is appropriate when there is still a valid block hit (with a fluid,
 * for example) and you don't want the block outline render to appear.
 *
 * <p>This event should NOT be used for general-purpose replacement of
 * the default block outline rendering because it will interfere with mod-specific
 * renders.  Mods that replace the default block outline for specific blocks
 * should instead subscribe to {@link #BLOCK_OUTLINE}.
 */
@FunctionalInterface
public interface BlockOutlinePreListener {
	/**
	 * Event signature for {@link BlockOutlinePreListener}.
	 *
	 * @param context  Access to state and parameters available during world rendering.
	 * @param hitResult The game object currently under the crosshair target.
	 * Normally equivalent to {@link Minecraft#hitResult}. Provided for convenience.
	 * @return true if vanilla block outline rendering should happen.
	 * Returning false prevents {@link WorldRenderEvents#BLOCK_OUTLINE} from invoking
	 * and also skips the vanilla block outline render, but has no effect on other subscribers to this event.
	 */
	boolean beforeBlockOutline(WorldRenderContext context, @Nullable HitResult hitResult);

	static void register(BlockOutlinePreListener listener) {
		BlockOutlinePreListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static boolean invoke(WorldRenderContext context, HitResult hit) {
		return BlockOutlinePreListenerImpl.invoke(context, hit);
	}
}
