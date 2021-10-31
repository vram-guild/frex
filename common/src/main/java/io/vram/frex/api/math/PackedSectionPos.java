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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

	static int unpackSectionX(int packedSectionPos) {
		return (packedSectionPos & 31) - 2;
	}

	static int unpackSectionY(int packedSectionPos) {
		return ((packedSectionPos >> 5) & 31) - 2;
	}

	static int unpackSectionZ(int packedSectionPos) {
		return ((packedSectionPos >> 10) & 31) - 2;
	}

	static String unpackToString(int packedSectionPos) {
		return String.format("(%d, %d, %d)", unpackSectionX(packedSectionPos), unpackSectionY(packedSectionPos), unpackSectionZ(packedSectionPos));
	}

	static int pack(Vec3i vec) {
		return pack(vec.getX(), vec.getY(), vec.getZ());
	}

	/**
	 * Masks the input coordinates so that they are relative to
	 * section origin, assuming sections are aligned to 16-block
	 * boundaries.
	 *
	 * @param pos Position within a chunk section.
	 * @return packed sector coordinates relative to implied section origin.
	 */
	static int packWithSectionMask(BlockPos pos) {
		return pack(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
	}

	static int add(int packedPosA, int packedPosB) {
		return packedPosA + packedPosB - PACKED_SECTION_ADDEND;
	}

	static int offset(int packedSectionPos, Direction face) {
		return add(packedSectionPos, pack(face.getNormal()));
	}
}
