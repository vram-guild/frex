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

package io.vram.frex.api.math;

import net.minecraft.core.Vec3i;

/**
 * Utilities for packing, reading and manipulating
 * integer, three-component vectors with values in the range
 * -2 to +17.  This is the entire range of block positions
 * normally used for chunk section rendering, relative to the
 * section origin.
 */
public interface PackedSectionPos {
	/**
	 * Add to make coordinates zero-based instead of starting at -2.
	 */
	int PACKED_SECTION_ADDEND = 2 | (2 << 5) | (2 << 10);

	static int pack(int x, int y, int z) {
		return (x + 2) | ((y + 2) << 5) | ((z + 2) << 10);
	}

	static int pack(Vec3i vec) {
		return pack(vec.getX(), vec.getY(), vec.getZ());
	}

	static int add(int packedPosA, int packedPosB) {
		return packedPosA + packedPosB - PACKED_SECTION_ADDEND;
	}
}
