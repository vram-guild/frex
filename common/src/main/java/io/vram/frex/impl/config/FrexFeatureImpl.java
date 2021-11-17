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

package io.vram.frex.impl.config;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class FrexFeatureImpl {
	private static final long[] FLAGS = new long[128];

	public static boolean isAvailable(int featureId) {
		return (FLAGS[featureId >> 6] & (1L << (featureId & 63))) != 0;
	}

	public static void registerFeatures(int... features) {
		for (final int featureId : features) {
			FLAGS[featureId >> 6] |= (1L << (featureId & 63));
		}
	}
}
