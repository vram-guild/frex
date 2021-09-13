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

package io.vram.frex.impl.world;

import java.util.List;
import java.util.function.Predicate;

import io.vram.frex.api.world.RenderRegionBakeListener;
import io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class RenderRegionBakeListenerImpl {
	@FunctionalInterface
	private interface BakeHandler {
		void handle(RenderRegionContext context, List<RenderRegionBakeListener> list);
	}

	private static class BakeHandlerImpl implements BakeHandler {
		private final Predicate<? super RenderRegionContext> predicate;
		private final RenderRegionBakeListener listener;

		private BakeHandlerImpl(Predicate<? super RenderRegionContext> predicate, RenderRegionBakeListener listener) {
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

	private static final ObjectArrayList<BakeHandler> LISTENERS = new ObjectArrayList<>();
	private static BakeHandler active = (context, list) -> { };

	public static void register(Predicate<? super RenderRegionContext> predicate, RenderRegionBakeListener handler) {
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

	public static void prepareInvocations(RenderRegionContext context, List<RenderRegionBakeListener> list) {
		active.handle(context, list);
	}
}
