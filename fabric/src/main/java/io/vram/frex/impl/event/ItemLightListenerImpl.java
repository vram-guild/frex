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

package io.vram.frex.impl.event;

import java.util.List;
import java.util.function.Predicate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import grondag.frex.api.event.RenderRegionBakeListener;
import grondag.frex.api.event.RenderRegionBakeListener.RenderRegionContext;

@Environment(EnvType.CLIENT)
public class ItemLightListenerImpl {
	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	private interface BakeHandler {
		void handle(RenderRegionContext context, List<RenderRegionBakeListener> list);
	}

	private static class BakeHandlerImpl implements BakeHandler {
		private final Predicate<RenderRegionContext> predicate;
		private final RenderRegionBakeListener listener;

		private BakeHandlerImpl(Predicate<RenderRegionContext> predicate, RenderRegionBakeListener listener) {
			this.predicate = predicate;
			this.listener = listener;
		}

		@Override
		public void handle(RenderRegionContext context, List<RenderRegionBakeListener> list) {
			if (predicate.test(context)) {
				list.add(listener);
			}
		}
	}

	private static final Event<BakeHandler> EVENT = EventFactory.createArrayBacked(BakeHandler.class, listeners -> (context, list) -> {
		for (final BakeHandler handler : listeners) {
			handler.handle(context, list);
		}
	});

	public static void register(Predicate<RenderRegionContext> predicate, RenderRegionBakeListener handler) {
		EVENT.register(new BakeHandlerImpl(predicate, handler));
	}

	public static void prepareInvocations(RenderRegionContext context, List<RenderRegionBakeListener> list) {
		EVENT.invoker().handle(context, list);
	}
}
