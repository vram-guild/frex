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

package io.vram.frex.api.material;

public interface MaterialConstants {
	/**
	 * No preset applied.
	 */
	int PRESET_NONE = 0;

	/**
	 * Emulate blending behavior of {@code BlockRenderLayer} associated with the block.
	 */
	int PRESET_DEFAULT = 1;

	/**
	 * Fully opaque with depth test, no blending. Used for most normal blocks.
	 */
	int PRESET_SOLID = 2;

	/**
	 * Pixels with alpha &gt; 0.5 are rendered as if {@code SOLID}. Other pixels are not rendered.
	 * Texture mip-map enabled.  Used for leaves.
	 */
	int PRESET_CUTOUT_MIPPED = 3;

	/**
	 * Pixels with alpha &gt; 0.5 are rendered as if {@code SOLID}. Other pixels are not rendered.
	 * Texture mip-map disabled.  Used for iron bars, glass and other cutout sprites with hard edges.
	 */
	int PRESET_CUTOUT = 4;

	/**
	 * Pixels are blended with the background according to alpha color values. Some performance cost,
	 * use in moderation. Texture mip-map enabled.  Used for stained glass.
	 */
	int PRESET_TRANSLUCENT = 5;

	int PRESET_COUNT = 6;

	//Parameters to {@link #cutout(int)}.

	/** Disable cutout testing. */
	int CUTOUT_NONE = 0;

	/** Use 0.5 as cutout threshold. */
	int CUTOUT_HALF = 1;

	/** Use 0.1 as cutout threshold. */
	int CUTOUT_TENTH = 2;

	/** Use zero as cutout threshold. */
	int CUTOUT_ZERO = 3;

	/** Use vertex color alpha as cutout threshold. */
	int CUTOUT_ALPHA = 4;

	int CUTOUT_COUNT = 5;

	/**
	 * Parameters to {@link MaterialFinder#decal(int)}.
	 */
	int DECAL_NONE = 0;

	int DECAL_POLYGON_OFFSET = 1;

	int DECAL_VIEW_OFFSET = 2;

	int DECAL_COUNT = 3;

	/**
	 * Parameters to {@link MaterialFinder#depthTest(int)}.
	 */
	int DEPTH_TEST_DISABLE = 0;

	int DEPTH_TEST_ALWAYS = 1;

	int DEPTH_TEST_EQUAL = 2;

	int DEPTH_TEST_LEQUAL = 3;

	int DEPTH_TEST_COUNT = 4;

	/**
	 * Parameters to {@link #target(int)}.
	 */
	int TARGET_MAIN = 0;

	int TARGET_OUTLINE = 1;

	int TARGET_TRANSLUCENT = 2;

	int TARGET_PARTICLES = 3;

	int TARGET_WEATHER = 4;

	int TARGET_CLOUDS = 5;

	int TARGET_ENTITIES = 6;

	int TARGET_COUNT = 7;

	/**
	 * Parameters to {@link MaterialFinder#transparency(int)}.
	 */
	int TRANSPARENCY_NONE = 0;

	/** Used for some particles and other effects. */
	int TRANSPARENCY_ADDITIVE = 1;

	/** Used for lightning. */
	int TRANSPARENCY_LIGHTNING = 2;

	/** Used for enchantment glint. */
	int TRANSPARENCY_GLINT = 3;

	/** Used for block breaking overlay. */
	int TRANSPARENCY_CRUMBLING = 4;

	/** Used for terrain blocks, decals and most other use cases. */
	int TRANSPARENCY_TRANSLUCENT = 5;

	/** Used for terrain particles. */
	int TRANSPARENCY_DEFAULT = 6;

	int TRANSPARENCY_COUNT = 7;

	/**
	 * Parameters to {@link MaterialFinder#writeMask(int)}.
	 */
	int WRITE_MASK_COLOR = 0;

	int WRITE_MASK_DEPTH = 1;

	int WRITE_MASK_COLOR_DEPTH = 2;

	int WRITE_MASK_COUNT = 3;

	int MAX_TEXTURE_STATES = 0x4000;
	int MAX_SHADERS = 0x1000;
	int MAX_CONDITIONS = 0x100;
	int MAX_MATERIAL_COUNT = 0x8000;
}
