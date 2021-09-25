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

package io.vram.frex.impl.material;

import java.util.function.Predicate;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.renderer.RenderType;

@Internal
public class RenderTypeExclusionImpl {
	private static final ReferenceOpenHashSet<RenderType> EXCLUSIONS = new ReferenceOpenHashSet<>(64, Hash.VERY_FAST_LOAD_FACTOR);
	private static final ObjectArrayList<Predicate<RenderType>> FILTERS = new ObjectArrayList<>();
	private static Predicate<RenderType> activeFilter = r -> false;

	public static boolean isExcluded(RenderType renderType) {
		if (EXCLUSIONS.contains(renderType)) {
			return true;
		}

		if (activeFilter.test(renderType)) {
			EXCLUSIONS.add(renderType);
			return true;
		}

		return false;
	}

	public static void exclude(RenderType renderType) {
		EXCLUSIONS.add(renderType);
	}

	public static void addFilter(Predicate<RenderType> filter) {
		FILTERS.add(filter);

		if (FILTERS.size() == 1) {
			activeFilter = filter;
		} else {
			activeFilter = t -> {
				final int limit = FILTERS.size();

				for (int i = 0; i < limit; ++i) {
					if (FILTERS.get(i).test(t)) {
						return true;
					}
				}

				return false;
			};
		}
	}
}
