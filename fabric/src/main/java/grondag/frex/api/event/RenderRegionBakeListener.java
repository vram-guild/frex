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

package grondag.frex.api.event;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import io.vram.frex.impl.world.RenderRegionBakeListenerImpl;

@Environment(EnvType.CLIENT)
@Deprecated
@FunctionalInterface
public interface RenderRegionBakeListener extends io.vram.frex.api.world.RenderRegionBakeListener {
	static void register(Predicate<? super io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext<Level>> predicate, io.vram.frex.api.world.RenderRegionBakeListener listener) {
		RenderRegionBakeListenerImpl.register(predicate, listener);
	}

	/**
	 * For use by renderer implementations.  Implementations are responsible for providing a thread-safe list
	 * instance and if populated, invoking all listeners in the list at the appropriate time. Renderer must
	 * also clear the list instance if needed before calling.
	 */
	static void prepareInvocations(io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext<Level> context, List<io.vram.frex.api.world.RenderRegionBakeListener> listeners) {
		RenderRegionBakeListenerImpl.prepareInvocations(context, listeners);
	}

	@Environment(EnvType.CLIENT)
	public interface RenderRegionContext extends io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext<BlockAndTintGetter> { }

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BlockStateRenderer extends io.vram.frex.api.world.RenderRegionBakeListener.BlockStateRenderer { }
}
