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
