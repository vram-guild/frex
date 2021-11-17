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

package io.vram.frex.impl.world;

import java.util.List;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.level.Level;

import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext;

public class RenderRegionBakeListenerImpl {
	@FunctionalInterface
	private interface BakeHandler {
		void handle(RenderRegionContext<Level> context, List<RenderRegionBakeListener> list);
	}

	private static class BakeHandlerImpl implements BakeHandler {
		private final Predicate<? super RenderRegionContext<Level>> predicate;
		private final RenderRegionBakeListener listener;

		private BakeHandlerImpl(Predicate<? super RenderRegionContext<Level>> predicate, RenderRegionBakeListener listener) {
			this.predicate = predicate;
			this.listener = listener;
		}

		@Override
		public void handle(RenderRegionContext<Level> context, List<RenderRegionBakeListener> list) {
			if (predicate.test(context)) {
				list.add(listener);
			}
		}
	}

	private static final ObjectArrayList<BakeHandler> LISTENERS = new ObjectArrayList<>();
	private static BakeHandler active = (context, list) -> { };

	public static void register(Predicate<? super RenderRegionContext<Level>> predicate, RenderRegionBakeListener handler) {
		LISTENERS.add(new BakeHandlerImpl(predicate, handler));

		if (LISTENERS.size() == 1) {
			active = LISTENERS.get(0);
		} else if (LISTENERS.size() == 2) {
			active = (context, list) -> {
				final int limit = LISTENERS.size();

				for (int i = 0; i < limit; ++i) {
					LISTENERS.get(i).handle(context, list);
				}
			};
		}
	}

	public static void prepareInvocations(RenderRegionContext<Level> context, List<RenderRegionBakeListener> list) {
		active.handle(context, list);
	}
}
