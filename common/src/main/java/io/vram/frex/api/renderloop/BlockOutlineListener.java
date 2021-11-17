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

package io.vram.frex.api.renderloop;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.impl.renderloop.BlockOutlineListenerImpl;

/**
 * Called after block outline render checks are made and before the
 * default block outline render runs.  Will NOT be called if the default outline
 * render was cancelled in {@link #BEFORE_BLOCK_OUTLINE}.
 *
 * <p>Use this to replace the default block outline rendering for specific blocks that
 * need special outline rendering or to add information that doesn't replace the block outline.
 * Subscribers cannot affect each other or detect if another subscriber is also
 * handling a specific block.  If two subscribers render for the same block, both
 * renders will appear.
 *
 * <p>Returning false from any event subscriber will cancel the default block
 * outline render.  This has no effect on other subscribers to this event -
 * all subscribers will always be called.  Canceling is appropriate when the
 * subscriber replacing the default block outline render for a specific block.
 *
 * <p>This event is not appropriate for mods that replace the default block
 * outline render for <em>all</em> blocks because all event subscribers will
 * always render - only the default outline render can be cancelled.  That should
 * be accomplished by mixin to the block outline render routine itself, typically
 * by targeting {@link LevelRenderer#renderShape}.
 */
@FunctionalInterface
public interface BlockOutlineListener {
	boolean onBlockOutline(WorldRenderContext worldRenderContext, BlockOutlineContext blockOutlineContext);

	static void register(BlockOutlineListener listener) {
		BlockOutlineListenerImpl.register(listener);
	}

	/**
	 * Must be called by renderer implementations if they
	 * disable the hooks implemented by FREX.
	 */
	static boolean invoke(WorldRenderContext worldRenderContext, BlockOutlineContext blockOutlineContext) {
		return BlockOutlineListenerImpl.invoke(worldRenderContext, blockOutlineContext);
	}

	/**
	 * Used to convey the parameters normally sent to
	 * {@code WorldRenderer.drawBlockOutline}.
	 */
	public interface BlockOutlineContext {
		Entity entity();

		double cameraX();

		double cameraY();

		double cameraZ();

		BlockPos blockPos();

		BlockState blockState();
	}
}
