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

package io.vram.frex.api.world;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.impl.world.RenderRegionBakeListenerImpl;

@FunctionalInterface
public interface RenderRegionBakeListener {
	void bake(RenderRegionContext context, BlockStateRenderer blockStateRenderer);

	static void register(Predicate<? super RenderRegionContext> predicate, RenderRegionBakeListener listener) {
		RenderRegionBakeListenerImpl.register(predicate, listener);
	}

	/**
	 * For use by renderer implementations.  Implementations are responsible for providing a thread-safe list
	 * instance and if populated, invoking all listeners in the list at the appropriate time. Renderer must
	 * also clear the list instance if needed before calling.
	 */
	static void prepareInvocations(RenderRegionContext context, List<RenderRegionBakeListener> listeners) {
		RenderRegionBakeListenerImpl.prepareInvocations(context, listeners);
	}

	// FEAT: create way to add a block view transform for virtual rendering - at a minimum, allow for connected textures and glass face culling
	public interface RenderRegionContext {
		/**
		 * Not available until chunk baking.  Predicate tests must
		 * be done based on block position only.
		 */
		@Nullable BlockAndTintGetter blockView();

		/**
		 * Min position (inclusive) of the area being built.
		 * The region backing {@link #blockView()} will typically have some padding
		 * extending beyond this.
		 */
		BlockPos origin();

		/** Non-allocating utility for block position operations. Do not retain. */
		MutableBlockPos originOffset(int x, int y, int z);

		/**
		 * Size of the area being built, on x-axis,, including the origin.
		 * The region backing {@link #blockView()} will typically have some padding
		 * extending beyond this.
		 *
		 * <p>In vanilla, regions are consistently 16x16x16.  A renderer mod
		 * could change region size so this should not be assumed.
		 */
		default int xSize() {
			return 16;
		}

		/**
		 * Size of the area being built, on y-axis,, including the origin.
		 * The region backing {@link #blockView()} will typically have some padding
		 * extending beyond this.
		 *
		 * <p>In vanilla, regions are consistently 16x16x16.  A renderer mod
		 * could change region size so this should not be assumed.
		 */
		default int ySize() {
			return 16;
		}

		/**
		 * Size of the area being built, on z-axis, including the origin.
		 * The region backing {@link #blockView()} will typically have some padding
		 * extending beyond this.
		 *
		 * <p>In vanilla, regions are consistently 16x16x16.  A renderer mod
		 * could change region size so this should not be assumed.
		 */
		default int zSize() {
			return 16;
		}
	}

	@FunctionalInterface
	public interface BlockStateRenderer {
		void bake(BlockPos pos, BlockState state);
	}
}
