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

package io.vram.frex.api.model.util;

import java.util.Arrays;

import org.jetbrains.annotations.Contract;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public abstract class FaceUtil {
	private FaceUtil() { }

	public static final int NORTH_INDEX = toFaceIndex(Direction.NORTH);
	public static final int SOUTH_INDEX = toFaceIndex(Direction.SOUTH);
	public static final int EAST_INDEX = toFaceIndex(Direction.EAST);
	public static final int WEST_INDEX = toFaceIndex(Direction.WEST);
	public static final int UP_INDEX = toFaceIndex(Direction.UP);
	public static final int DOWN_INDEX = toFaceIndex(Direction.DOWN);
	/** Result from {@link #toFaceIndex(Direction)} for null values. */
	public static final int UNASSIGNED_INDEX = 6;

	public static final int FACE_INDEX_COUNT = 7;

	public static final int NORTH_FLAG = 1 << NORTH_INDEX;
	public static final int SOUTH_FLAG = 1 << SOUTH_INDEX;
	public static final int EAST_FLAG = 1 << EAST_INDEX;
	public static final int WEST_FLAG = 1 << WEST_INDEX;
	public static final int UP_FLAG = 1 << UP_INDEX;
	public static final int DOWN_FLAG = 1 << DOWN_INDEX;
	public static final int UNASSIGNED_FLAG = 1 << UNASSIGNED_INDEX;

	public static final int ALL_REAL_FACE_FLAGS = NORTH_FLAG | SOUTH_FLAG | EAST_FLAG | WEST_FLAG | UP_FLAG | DOWN_FLAG;

	/** @see #faceFromIndex(int) */
	private static final Direction[] FACES = Arrays.copyOf(Direction.values(), FACE_INDEX_COUNT);

	private static final OffsetFunc[] FACE_OFFSETS = new OffsetFunc[6];
	private static final int[] FACE_INDEX_OPPOSITES = new int[6];

	static {
		FACE_OFFSETS[Direction.UP.ordinal()] = (t, s) -> {
			t.set(s.getX(), s.getY() + 1, s.getZ());
			return t;
		};

		FACE_OFFSETS[Direction.DOWN.ordinal()] = (t, s) -> {
			t.set(s.getX(), s.getY() - 1, s.getZ());
			return t;
		};

		FACE_OFFSETS[Direction.NORTH.ordinal()] = (t, s) -> {
			t.set(s.getX(), s.getY(), s.getZ() - 1);
			return t;
		};

		FACE_OFFSETS[Direction.SOUTH.ordinal()] = (t, s) -> {
			t.set(s.getX(), s.getY(), s.getZ() + 1);
			return t;
		};

		FACE_OFFSETS[Direction.EAST.ordinal()] = (t, s) -> {
			t.set(s.getX() + 1, s.getY(), s.getZ());
			return t;
		};

		FACE_OFFSETS[Direction.WEST.ordinal()] = (t, s) -> {
			t.set(s.getX() - 1, s.getY(), s.getZ());
			return t;
		};

		FACE_INDEX_OPPOSITES[FaceUtil.toFaceIndex(Direction.DOWN)] = FaceUtil.toFaceIndex(Direction.UP);
		FACE_INDEX_OPPOSITES[FaceUtil.toFaceIndex(Direction.UP)] = FaceUtil.toFaceIndex(Direction.DOWN);
		FACE_INDEX_OPPOSITES[FaceUtil.toFaceIndex(Direction.EAST)] = FaceUtil.toFaceIndex(Direction.WEST);
		FACE_INDEX_OPPOSITES[FaceUtil.toFaceIndex(Direction.WEST)] = FaceUtil.toFaceIndex(Direction.EAST);
		FACE_INDEX_OPPOSITES[FaceUtil.toFaceIndex(Direction.NORTH)] = FaceUtil.toFaceIndex(Direction.SOUTH);
		FACE_INDEX_OPPOSITES[FaceUtil.toFaceIndex(Direction.SOUTH)] = FaceUtil.toFaceIndex(Direction.NORTH);
	}

	/**
	 * Convenient way to encode faces that may be null.
	 * Null is returned as {@link #UNASSIGNED_INDEX}.
	 * Use {@link #faceFromIndex(int)} to retrieve encoded face.
	 */
	public static int toFaceIndex(Direction face) {
		return face == null ? UNASSIGNED_INDEX : face.get3DDataValue();
	}

	/**
	 * Use to decode a result from {@link #toFaceIndex(Direction)}.
	 * Return value will be null if encoded value was null.
	 * Can also be used for no-allocation iteration of {@link Direction#values()},
	 * optionally including the null face. (Use &lt; or  &lt;= {@link #UNASSIGNED_INDEX}
	 * to exclude or include the null value, respectively.)
	 */
	@Contract("null -> null")
	public static Direction faceFromIndex(int faceIndex) {
		return FACES[faceIndex];
	}

	public static BlockPos.MutableBlockPos fastFaceOffset(BlockPos.MutableBlockPos target, BlockPos start, int faceOrdinal) {
		return FACE_OFFSETS[faceOrdinal].offset(target, start);
	}

	@FunctionalInterface
	private interface OffsetFunc {
		BlockPos.MutableBlockPos offset(BlockPos.MutableBlockPos target, BlockPos start);
	}

	public static int oppositeFaceIndex(int faceIndex) {
		return FACE_INDEX_OPPOSITES[faceIndex];
	}
}
