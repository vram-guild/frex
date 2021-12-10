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

package io.vram.frex.api.model.util;

import java.nio.ByteOrder;

/**
 * Static routines of general utility for renderer implementations. Renderers
 * are not required to use these helpers, but they were designed to be usable
 * without the default renderer.
 */
public abstract class ColorUtil {
	private ColorUtil() { }

	public static final int FULL_BRIGHTNESS = 15 << 20 | 15 << 4;
	public static final int WHITE = -1;
	public static final boolean SWAP_RED_BLUE = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;

	/**
	 * Swaps red blue order. Use if needed to match GPU expectations for color component order.
	 */
	public static int swapRedBlue(int color) {
		return color == -1 ? -1 : ((color & 0xFF00FF00) | ((color & 0x00FF0000) >> 16) | ((color & 0xFF) << 16));
	}

	/**
	 * arguments are assumed to be ARGB - does not modify alpha.
	 */
	public static int multiplyRGB(int color, float shade) {
		final int red = (int) (((color >> 16) & 0xFF) * shade);
		final int green = (int) (((color >> 8) & 0xFF) * shade);
		final int blue = (int) ((color & 0xFF) * shade);
		final int alpha = ((color >> 24) & 0xFF);

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * Component-wise multiply. Components need to be in same order in both inputs!
	 */
	public static int multiplyColor(int color1, int color2) {
		if (color1 == -1) {
			return color2;
		} else if (color2 == -1) {
			return color1;
		}

		final int alpha = ((color1 >> 24) & 0xFF) * ((color2 >> 24) & 0xFF) / 0xFF;
		final int red = ((color1 >> 16) & 0xFF) * ((color2 >> 16) & 0xFF) / 0xFF;
		final int green = ((color1 >> 8) & 0xFF) * ((color2 >> 8) & 0xFF) / 0xFF;
		final int blue = (color1 & 0xFF) * (color2 & 0xFF) / 0xFF;

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * Combination of {@link #multiplyColor(int, int)} and {@link #swapRedBlue(int)}.
	 * These tend to be in hot loop for quad encoding so avoid an extra call is
	 * worth a little redundant code.
	 */
	public static int multiplyColorSwapRedBlue(int color1, int color2) {
		if (color1 == -1) {
			return swapRedBlue(color2);
		} else if (color2 == -1) {
			return swapRedBlue(color1);
		}

		final int alpha = ((color1 >> 24) & 0xFF) * ((color2 >> 24) & 0xFF) / 0xFF;
		final int red = ((color1 >> 16) & 0xFF) * ((color2 >> 16) & 0xFF) / 0xFF;
		final int green = ((color1 >> 8) & 0xFF) * ((color2 >> 8) & 0xFF) / 0xFF;
		final int blue = (color1 & 0xFF) * (color2 & 0xFF) / 0xFF;

		return (alpha << 24) | (blue << 16) | (green << 8) | red;
	}

	/**
	 * Component-wise max.
	 */
	public static int maxBrightness(int b0, int b1) {
		// wouldn't work if had negative values
		assert b0 >= 0;
		assert b1 >= 0;

		final int low0 = b0 & 0xFFFF;
		final int low1 = b1 & 0xFFFF;
		final int high0 = b0 & 0xFFFF0000;
		final int high1 = b1 & 0xFFFF0000;
		return (low0 > low1 ? low0 : low1) | (high0 > high1 ? high0 : high1);
	}
}
