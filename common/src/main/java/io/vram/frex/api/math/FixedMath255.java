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

package io.vram.frex.api.math;

/**
 * 8-bit fixed precision math routines.
 * Assumes normalized values.
 */
public class FixedMath255 {
	public static final int UNIT_VALUE = 0xFF;
	public static final int HALF_VALUE = 0x7F;
	public static final int UNIT_SHIFT = 8;
	public static final float FLOAT_CONVERSION_FACTOR = 1f / 255f;

	public static int clamp(float x) {
		if (x < 0f) x = 0;
		if (x > 1f) x = 1;
		return Math.round(x * UNIT_VALUE);
	}

	public static int mul(int x, int y) {
		return (x * y + UNIT_VALUE) >> UNIT_SHIFT;
	}

	protected static final int RECIPROCAL_DIVIDE_127_MAGIC = 33026;
	protected static final int RECIPROCAL_DIVIDE_127_SHIFT = 22;

	/**
	 * Fast re-scale of normal values from signed 127 to unsigned 0-255.
	 * See https://www.agner.org/optimize/optimizing_assembly.pdf Sec 16.8 "Division"
	 */
	public static int from127(int base127) {
		return ((Math.abs(base127) * 255 + 1) * RECIPROCAL_DIVIDE_127_MAGIC) >> RECIPROCAL_DIVIDE_127_SHIFT;
	}
}
