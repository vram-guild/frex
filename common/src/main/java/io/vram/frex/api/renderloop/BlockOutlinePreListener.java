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

package io.vram.frex.api.renderloop;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;

import io.vram.frex.impl.renderloop.BlockOutlinePreListenerImpl;

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
 * should instead subscribe to {@link BlockOutlineListener}.
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
	 * Returning false prevents {@link BlockOutlineListener} from invoking
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
