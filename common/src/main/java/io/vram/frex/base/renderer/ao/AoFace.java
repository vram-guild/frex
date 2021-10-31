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

package io.vram.frex.base.renderer.ao;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import io.vram.frex.api.math.PackedSectionPos;
import io.vram.frex.base.renderer.mesh.BaseQuadView;

/**
 * Adapted from vanilla. Holds positional data in related functions for each block face.
 */
public class AoFace {
	@FunctionalInterface
	private interface ClampFunc {
		float clamp(float x);
	}

	protected static ClampFunc clampFunc;

	/**
	 * Allows renderers to control this feature as part of their configuration system.
	 * When true, vertices the extend outside the unit cube are treated as inside the
	 * unit cube for purposes of AO lighting. Can prevent dark spots. Enabled by default.
	 */
	public static void clampExteriorVertices(boolean enable) {
		clampFunc = enable ? x -> x < 0f ? 0f : (x > 1f ? 1f : x) : x -> x;
	}

	static {
		clampExteriorVertices(true);
	}

	public static final AoFace AOF_DOWN = new AoFace(WEST, EAST, NORTH, SOUTH,
		(q, i) -> clampFunc.clamp(q.y(i)),
		(q, i) -> clampFunc.clamp(q.z(i)),
		(q, i) -> 1 - clampFunc.clamp(q.x(i)),
		(q, i, w) -> {
			final float u = clampFunc.clamp(q.z(i));
			final float v = 1 - clampFunc.clamp(q.x(i));
			w[0] = v * u;
			w[1] = v * (1 - u);
			w[2] = (1 - v) * (1 - u);
			w[3] = (1 - v) * u;
		});

	public static final AoFace AOF_UP = new AoFace(EAST, WEST, NORTH, SOUTH,
		(q, i) -> 1 - clampFunc.clamp(q.y(i)),
		(q, i) -> clampFunc.clamp(q.z(i)),
		(q, i) -> clampFunc.clamp(q.x(i)),
		(q, i, w) -> {
			final float u = clampFunc.clamp(q.z(i));
			final float v = clampFunc.clamp(q.x(i));
			w[0] = v * u;
			w[1] = v * (1 - u);
			w[2] = (1 - v) * (1 - u);
			w[3] = (1 - v) * u;
		});

	public static final AoFace AOF_NORTH = new AoFace(UP, DOWN, EAST, WEST,
		(q, i) -> clampFunc.clamp(q.z(i)),
		(q, i) -> 1 - clampFunc.clamp(q.x(i)),
		(q, i) -> clampFunc.clamp(q.y(i)),
		(q, i, w) -> {
			final float u = 1 - clampFunc.clamp(q.x(i));
			final float v = clampFunc.clamp(q.y(i));
			w[0] = v * u;
			w[1] = v * (1 - u);
			w[2] = (1 - v) * (1 - u);
			w[3] = (1 - v) * u;
		});
	public static final AoFace AOF_SOUTH = new AoFace(WEST, EAST, DOWN, UP,
		(q, i) -> 1 - clampFunc.clamp(q.z(i)),
		(q, i) -> clampFunc.clamp(q.y(i)),
		(q, i) -> 1 - clampFunc.clamp(q.x(i)),
		(q, i, w) -> {
			final float u = clampFunc.clamp(q.y(i));
			final float v = 1 - clampFunc.clamp(q.x(i));
			w[0] = u * v;
			w[1] = (1 - u) * v;
			w[2] = (1 - u) * (1 - v);
			w[3] = u * (1 - v);
		});

	public static final AoFace AOF_WEST = new AoFace(UP, DOWN, NORTH, SOUTH,
		(q, i) -> clampFunc.clamp(q.x(i)),
		(q, i) -> clampFunc.clamp(q.z(i)),
		(q, i) -> clampFunc.clamp(q.y(i)),
		(q, i, w) -> {
			final float u = clampFunc.clamp(q.z(i));
			final float v = clampFunc.clamp(q.y(i));
			w[0] = v * u;
			w[1] = v * (1 - u);
			w[2] = (1 - v) * (1 - u);
			w[3] = (1 - v) * u;
		});

	public static final AoFace AOF_EAST = new AoFace(DOWN, UP, NORTH, SOUTH,
		(q, i) -> 1 - clampFunc.clamp(q.x(i)),
		(q, i) -> clampFunc.clamp(q.z(i)),
		(q, i) -> 1 - clampFunc.clamp(q.y(i)),
		(q, i, w) -> {
			final float u = clampFunc.clamp(q.z(i));
			final float v = 1 - clampFunc.clamp(q.y(i));
			w[0] = v * u;
			w[1] = v * (1 - u);
			w[2] = (1 - v) * (1 - u);
			w[3] = (1 - v) * u;
		});

	protected static final AoFace[] values = createValues();

	public final int[] neighbors;
	public final WeightFunction weightFunc;
	public final Vertex2Float depthFunc;
	public final Vertex2Float uFunc;
	public final Vertex2Float vFunc;

	public final int bottomOffset;
	public final int leftOffset;
	public final int topOffset;
	public final int rightOffset;
	public final int bottomLeftOffset;
	public final int bottomRightOffset;
	public final int topLeftOffset;
	public final int topRightOffset;

	AoFace(Direction bottom, Direction top, Direction left, Direction right, Vertex2Float depthFunc, Vertex2Float uFunc, Vertex2Float vFunc, WeightFunction weightFunc) {
		neighbors = new int[4];
		neighbors[0] = bottom.ordinal();
		neighbors[1] = top.ordinal();
		neighbors[2] = left.ordinal();
		neighbors[3] = right.ordinal();

		final Vec3i bottomVec = bottom.getNormal();
		final Vec3i leftVec = left.getNormal();
		final Vec3i topVec = top.getNormal();
		final Vec3i rightVec = right.getNormal();

		bottomOffset = PackedSectionPos.pack(bottomVec);
		leftOffset = PackedSectionPos.pack(leftVec);
		topOffset = PackedSectionPos.pack(topVec);
		rightOffset = PackedSectionPos.pack(rightVec);

		bottomLeftOffset = PackedSectionPos.add(bottomOffset, leftOffset);
		bottomRightOffset = PackedSectionPos.add(bottomOffset, rightOffset);
		topLeftOffset = PackedSectionPos.add(topOffset, leftOffset);
		topRightOffset = PackedSectionPos.add(topOffset, rightOffset);

		this.depthFunc = depthFunc;
		this.weightFunc = weightFunc;
		this.vFunc = vFunc;
		this.uFunc = uFunc;
	}

	protected static AoFace[] createValues() {
		final AoFace[] result = new AoFace[6];
		result[DOWN.get3DDataValue()] = AOF_DOWN;
		result[UP.get3DDataValue()] = AOF_UP;
		result[NORTH.get3DDataValue()] = AOF_NORTH;
		result[SOUTH.get3DDataValue()] = AOF_SOUTH;
		result[WEST.get3DDataValue()] = AOF_WEST;
		result[EAST.get3DDataValue()] = AOF_EAST;
		return result;
	}

	public static AoFace get(int directionOrdinal) {
		return values[directionOrdinal];
	}

	/**
	 * Implementations handle bilinear interpolation of a point on a light face by
	 * computing weights for each corner of the light face. Relies on the fact that
	 * each face is a unit cube. Uses coordinates from axes orthogonal to face as
	 * distance from the edge of the cube, flipping as needed. Multiplying distance
	 * coordinate pairs together gives sub-area that are the corner weights. Weights
	 * sum to 1 because it is a unit cube. Values are stored in the provided array.
	 */
	@FunctionalInterface
	public interface WeightFunction {
		void apply(BaseQuadView q, int vertexIndex, float[] out);
	}

	@FunctionalInterface
	public interface Vertex2Float {
		float apply(BaseQuadView q, int vertexIndex);
	}
}
