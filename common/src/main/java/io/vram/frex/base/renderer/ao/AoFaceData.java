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

package io.vram.frex.base.renderer.ao;

/**
 * Holds per-corner results for a single block face. Handles caching and
 * provides various utility methods to simplify code elsewhere.
 */
public class AoFaceData {
	public static final int OPAQUE = -1;
	public final AoFaceCalc calc = new AoFaceCalc();
	// packed values gathered during compute
	public int bottom;
	public int top;
	public int left;
	public int right;
	public int bottomLeft;
	public int bottomRight;
	public int topLeft;
	public int topRight;
	public int center;
	// these values are fully computed at gather time
	public int aoBottom;
	public int aoTop;
	public int aoLeft;
	public int aoRight;
	public int aoBottomLeft;
	public int aoBottomRight;
	public int aoTopLeft;
	public int aoTopRight;
	public int aoCenter;
}
