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

package io.vram.frex.impl.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext;

public class ChunkRenderConditionContext implements RenderRegionContext {
	public final ObjectArrayList<RenderRegionBakeListener> listeners = new ObjectArrayList<>();
	private final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos();
	private final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

	public ChunkRenderConditionContext prepare(int x, int y, int z) {
		listeners.clear();
		origin.set(x, y, z);
		return this;
	}

	@Override
	public MutableBlockPos originOffset(int x, int y, int z) {
		final var origin = this.origin;
		return searchPos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
	}

	public @Nullable RenderRegionBakeListener[] getListeners() {
		if (listeners.isEmpty()) {
			return null;
		} else {
			return listeners.toArray(new RenderRegionBakeListener[listeners.size()]);
		}
	}

	@Override
	public BlockAndTintGetter blockView() {
		return null;
	}

	@Override
	public BlockPos origin() {
		return origin;
	}
}
