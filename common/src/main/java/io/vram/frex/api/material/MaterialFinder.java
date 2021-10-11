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

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import io.vram.frex.api.texture.MaterialTexture;
import io.vram.frex.impl.material.MaterialFinderPool;

/**
 * Finds standard {@link RenderMaterial} instances used to communicate
 * quad rendering characteristics to a {@link RenderContext}.
 *
 * <p>Must be obtained via {@link Renderer#materialFinder()}.
 */
public interface MaterialFinder {
	MaterialFinder clear();

	/**
	 * Defines how sprite pixels will be blended with the scene.
	 *
	 * <p>The application of blend mode is context-dependent, especially
	 * for {@link MaterialConstants#TRANSLUCENT}.  Various material properties
	 * will be set differently when the model is rendered as a block, vs.
	 * a moving entity block, vs. item in GUI and item floating in the world,
	 * as examples.
	 *
	 * <p>To avoid this ambiguity, pass {@link MaterialConstants#PRESET_NONE} as
	 * the parameter and the material will always be applied without adjustment.
	 *
	 * @param preset
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder preset(int preset);

	MaterialFinder disableColorIndex(boolean disable);

	MaterialFinder disableDiffuse(boolean disable);

	MaterialFinder disableAo(boolean disable);

	MaterialFinder emissive(boolean isEmissive);

	RenderMaterial find();

	MaterialFinder copyFrom(RenderMaterial material);

	/**
	 * Enables blur (bilinear texture sampling) on magnification,
	 * and makes minification blur LOD-linear. Used for enchantment glint.
	 *
	 * <p>Note this is isn't generally useful for atlas sprites and different from mipmap.
	 * Mipmap is done with linear sampling using nearest LOD because otherwise artifacts appear when
	 * sprites are sampled across LODS levels, especially for randomized rotated
	 * sprites like grass.
	 *
	 * @param blur [@code true} to enable bilinear texture sampling on magnification
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder blur(boolean blur);

	MaterialFinder condition(MaterialCondition condition);

	/**
	 * Enables or disables backface culling on GPU. Generally
	 * desired for most usage because it results in fewer polygons drawn.
	 *
	 * @param cull [@code true} to enable GPU face culling
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder cull(boolean cull);

	/**
	 * Directly enables cutout rendering without using {@link #preset()}.
	 * May be overridden if BlendMode is non-null.
	 *
	 * @param cutout mode to enable, one of the CUTOUT_ constants.
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder cutout(int cutout);

	/**
	 * @param decal one of {@link MaterialConstants#DECAL_NONE}, {@link MaterialConstants#DECAL_POLYGON_OFFSET}
	 * or {@link MaterialConstants#DECAL_VIEW_OFFSET}
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder decal(int decal);

	/**
	 * @param depthTest one of {@link MaterialConstants#DEPTH_TEST_DISABLE}, {@link MaterialConstants#DEPTH_TEST_ALWAYS},
	 * {@link MaterialConstants#DEPTH_TEST_EQUAL} or {@link MaterialConstants#DEPTH_TEST_LEQUAL}
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder depthTest(int depthTest);

	/**
	 * Performance hint for the renderer -
	 * set true when the material has a shader and the shader
	 * uses procedural texturing and does not use the bound texture.
	 *
	 * <p>Primary use case is to disable upload of animated textures
	 * that aren't going to be used.
	 *
	 * @param discardsTexture {@code true} to enable performance hint
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder discardsTexture(boolean discardsTexture);

	/**
	 * Enable or disable the translucent flash overlay used to indicate primed TNT.
	 *
	 * @param flashOverlay {@code true} to enable
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder flashOverlay(boolean flashOverlay);

	MaterialFinder foilOverlay(boolean foilOverlay);

	/**
	 * Enable or disables atmospheric fog for this material.
	 */
	MaterialFinder fog(boolean enable);

	/**
	 * Enable or disable the translucent red overlay used to indicate damage.
	 *
	 * @param hurtOverlay {@code true} to enable
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder hurtOverlay(boolean hurtOverlay);

	/**
	 * Set the line width to the full-width default used by Minecraft.
	 * Enabled for most vanilla render layers except some debug renders.
	 *
	 * @param lines {@code true} to enable full-width line rendering
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder lines(boolean lines);

	MaterialFinder shader(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId);

	MaterialFinder shader(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId, @Nullable ResourceLocation depthVertexSourceId, @Nullable ResourceLocation depthFragmentSourceId);

	/**
	 * For transparent materials, enables sorting of quads by
	 * camera distance before buffering and drawing. Vanilla sorts
	 * all transparent render layers even when it isn't strictly needed.
	 *
	 * <p>Content authors should set this to false for translucent decal materials
	 * when it is known the quad will be backed by a solid quad to avoid the
	 * performance overhead of sorting.
	 *
	 * @param sorted {@code true} to enable sorting of quads by camera distance
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder sorted(boolean sorted);

	/**
	 * Selects framebuffer target for material. Several of
	 * these are equivalent to MAIN unless Fabulous graphics are enabled.
	 * @param target identifies framebuffer target for material. One of
	 * {@link MaterialConstants#TARGET_MAIN}, {@link MaterialConstants#TARGET_OUTLINE}, {@link MaterialConstants#TARGET_TRANSLUCENT},
	 * {@link MaterialConstants#TARGET_PARTICLES}, {@link MaterialConstants#TARGET_WEATHER}. {@link MaterialConstants#TARGET_CLOUDS}
	 * or {@link MaterialConstants#TARGET_ENTITIES}
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder target(int target);

	/**
	 * Select base color texture, or disable texturing.
	 * @param id namespaced id of texture or texture atlas for base color. {@code null} to disable texturing.
	 * @return finder instance for ease of chaining calls
	 */
	default MaterialFinder texture(@Nullable ResourceLocation id) {
		return textureIndex(id == null ? MaterialTexture.none().index() : MaterialTexture.fromId(id).index());
	}

	default MaterialFinder texture(MaterialTexture texture) {
		return textureIndex(texture.index());
	}

	MaterialFinder textureIndex(int textureIndex);

	/**
	 * Enables or disables texture blending and sets blending mode.
	 *
	 * <p>If {@link #preset()} is used to set a non-empty preset,
	 * then this setting will be adjusted to an appropriate context-dependent
	 * value at runtime and is essentially ignored.
	 *
	 * @param transparency one of {@link MaterialConstants#TRANSPARENCY_NONE}, {@link MaterialConstants#TRANSPARENCY_ADDITIVE},
	 * {@link MaterialConstants#TRANSPARENCY_LIGHTNING}, {@link MaterialConstants#TRANSPARENCY_GLINT},
	 * {@link MaterialConstants#TRANSPARENCY_CRUMBLING}, {@link MaterialConstants#TRANSPARENCY_TRANSLUCENT},
	 * or {@link MaterialConstants#TRANSPARENCY_DEFAULT}
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder transparency(int transparency);

	/**
	 * Directly enables mipped rendering without using {@link #preset()}.
	 * May be overridden if preset is used.
	 *
	 * @param unmipped {@code true} to enable
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder unmipped(boolean unmipped);

	/**
	 * Determines which standard framebuffer attachments are written.
	 * Implementations may have additional framebuffer attachments not
	 * controlled by this setting.
	 *
	 * @param writeMask one of {@link MaterialConstants#WRITE_MASK_COLOR}, {@link MaterialConstants#WRITE_MASK_DEPTH},
	 * or {@link MaterialConstants#WRITE_MASK_COLOR_DEPTH}
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder writeMask(int writeMask);

	/**
	 * Set true when material should cast shadows. Pipelines that lack
	 * shadowmaps or some other mechanism for realistic shadows will
	 * ignore this.  Defaults to true.
	 *
	 * <p>Set false for materials that render as solid for practical
	 * reasons but should not cast shadows.  Beacon beams are a vanilla example.
	 *
	 * @param castShadows Set true if material should cast shadows.
	 * @return finder instance for ease of chaining calls
	 */
	MaterialFinder castShadows(boolean castShadows);

	static MaterialFinder threadLocal() {
		return MaterialFinderPool.threadLocal();
	}

	MaterialFinder label(String name);
}
