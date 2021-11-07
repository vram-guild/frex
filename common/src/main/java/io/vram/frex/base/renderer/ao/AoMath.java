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

/**
 * 8-bit fixed precision math routines.
 * Assumes normalized values.
 */
public class AoMath {
	public static final int UNIT_VALUE = 0xFF;
	public static final int HALF_VALUE = 0x7F;
	public static final int UNIT_SHIFT = 8;

	public static int clamp(float x) {
		if (x < 0f) x = 0;
		if (x > 1f) x = 1;
		return Math.round(x * UNIT_VALUE);
	}

	public static int mul(int x, int y) {
		return (x * y + UNIT_VALUE) >> UNIT_SHIFT;
	}

	// WIP: remove
	public static boolean compare(float[] w, int fw) {
		final float d0 = Math.abs(w[0] - (fw & 0xFF) / 255f);
		final float d1 = Math.abs(w[1] - ((fw >> 8) & 0xFF) / 255f);
		final float d2 = Math.abs(w[2] - ((fw >> 16) & 0xFF) / 255f);
		final float d3 = Math.abs(w[3] - ((fw >> 24) & 0xFF) / 255f);

		final float dMax = Math.max(Math.max(d0, d1), Math.max(d2, d3));

		if (dMax >= 0.01f) {
			System.out.println(d0 + "  " + d1 + "  " + d2 + "  " + d3);
			return false;
		} else {
			return true;
		}
	}
}
