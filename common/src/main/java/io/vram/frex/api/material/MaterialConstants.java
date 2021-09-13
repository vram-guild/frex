/*
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
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

	/**
	 * Parameters to {@link MaterialFinder#decal(int)}.
	 */
	int DECAL_NONE = 0;

	int DECAL_POLYGON_OFFSET = 1;

	int DECAL_VIEW_OFFSET = 2;

	/**
	 * Parameters to {@link MaterialFinder#depthTest(int)}.
	 */
	int DEPTH_TEST_DISABLE = 0;

	int DEPTH_TEST_ALWAYS = 1;

	int DEPTH_TEST_EQUAL = 2;

	int DEPTH_TEST_LEQUAL = 3;

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

	/**
	 * Parameters to {@link MaterialFinder#writeMask(int)}.
	 */
	int WRITE_MASK_COLOR = 0;

	int WRITE_MASK_DEPTH = 1;

	int WRITE_MASK_COLOR_DEPTH = 2;
}
