/*
 * This file is part of FREX and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
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
 */

package io.vram.frex.impl.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;

import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext;

public class ChunkRenderConditionContext implements RenderRegionContext<Level> {
	public final ObjectArrayList<RenderRegionBakeListener> listeners = new ObjectArrayList<>();
	protected final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos();
	protected final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
	protected Level level;

	public ChunkRenderConditionContext prepare(Level level, int x, int y, int z) {
		listeners.clear();
		origin.set(x, y, z);
		this.level = level;
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
	public Level blockView() {
		return level;
	}

	@Override
	public BlockPos origin() {
		return origin;
	}
}
