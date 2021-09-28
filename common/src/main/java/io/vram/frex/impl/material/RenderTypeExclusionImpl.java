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
