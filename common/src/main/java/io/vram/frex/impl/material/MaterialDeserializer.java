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

package io.vram.frex.impl.material;

import static io.vram.frex.api.material.MaterialConstants.CUTOUT_ALPHA;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_HALF;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_NONE;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_TENTH;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_ZERO;
import static io.vram.frex.api.material.MaterialConstants.DECAL_NONE;
import static io.vram.frex.api.material.MaterialConstants.DECAL_POLYGON_OFFSET;
import static io.vram.frex.api.material.MaterialConstants.DECAL_VIEW_OFFSET;
import static io.vram.frex.api.material.MaterialConstants.DEPTH_TEST_ALWAYS;
import static io.vram.frex.api.material.MaterialConstants.DEPTH_TEST_DISABLE;
import static io.vram.frex.api.material.MaterialConstants.DEPTH_TEST_EQUAL;
import static io.vram.frex.api.material.MaterialConstants.DEPTH_TEST_LEQUAL;
import static io.vram.frex.api.material.MaterialConstants.TARGET_CLOUDS;
import static io.vram.frex.api.material.MaterialConstants.TARGET_ENTITIES;
import static io.vram.frex.api.material.MaterialConstants.TARGET_MAIN;
import static io.vram.frex.api.material.MaterialConstants.TARGET_OUTLINE;
import static io.vram.frex.api.material.MaterialConstants.TARGET_PARTICLES;
import static io.vram.frex.api.material.MaterialConstants.TARGET_TRANSLUCENT;
import static io.vram.frex.api.material.MaterialConstants.TARGET_WEATHER;
import static io.vram.frex.api.material.MaterialConstants.TRANSPARENCY_ADDITIVE;
import static io.vram.frex.api.material.MaterialConstants.TRANSPARENCY_CRUMBLING;
import static io.vram.frex.api.material.MaterialConstants.TRANSPARENCY_DEFAULT;
import static io.vram.frex.api.material.MaterialConstants.TRANSPARENCY_GLINT;
import static io.vram.frex.api.material.MaterialConstants.TRANSPARENCY_LIGHTNING;
import static io.vram.frex.api.material.MaterialConstants.TRANSPARENCY_NONE;
import static io.vram.frex.api.material.MaterialConstants.TRANSPARENCY_TRANSLUCENT;
import static io.vram.frex.api.material.MaterialConstants.WRITE_MASK_COLOR;
import static io.vram.frex.api.material.MaterialConstants.WRITE_MASK_COLOR_DEPTH;
import static io.vram.frex.api.material.MaterialConstants.WRITE_MASK_DEPTH;

import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;

@Internal
public class MaterialDeserializer {
	private MaterialDeserializer() { }

	private static final MaterialFinder FINDER = MaterialFinder.newInstance();

	public static RenderMaterial deserialize(JsonObject json) {
		final MaterialFinder finder = FINDER.clear();

		// "layers" tag still support for old multi-layer format but
		// only first layer is used. Layer is not required.
		if (json.has("layers")) {
			final JsonArray layers = GsonHelper.convertToJsonArray(json.get("layers"), "layers");

			if (!layers.isJsonNull() && layers.size() >= 1) {
				readMaterial(layers.get(0).getAsJsonObject(), finder);

				return finder.find();
			}
		}

		readMaterial(json, finder);
		return finder.find();
	}

	private static void readMaterial(JsonObject obj, io.vram.frex.api.material.MaterialFinder finder) {
		final String vertexSource = GsonHelper.getAsString(obj, "vertexSource", null);
		final String fragmentSource = GsonHelper.getAsString(obj, "fragmentSource", null);
		final String depthVertexSource = GsonHelper.getAsString(obj, "depthVertexSource", null);
		final String depthFragmentSource = GsonHelper.getAsString(obj, "depthFragmentSource", null);

		final ResourceLocation vertexSourceId = vertexSource != null && ResourceLocation.isValidResourceLocation(vertexSource) ? new ResourceLocation(vertexSource) : null;
		final ResourceLocation fragmentSourceId = fragmentSource != null && ResourceLocation.isValidResourceLocation(fragmentSource) ? new ResourceLocation(fragmentSource) : null;
		final ResourceLocation depthVertexSourceId = depthVertexSource != null && ResourceLocation.isValidResourceLocation(depthVertexSource) ? new ResourceLocation(depthVertexSource) : null;
		final ResourceLocation depthFragmentSourceId = depthFragmentSource != null && ResourceLocation.isValidResourceLocation(depthFragmentSource) ? new ResourceLocation(depthFragmentSource) : null;

		if (fragmentSourceId != null || vertexSourceId != null || depthFragmentSourceId != null || depthVertexSourceId != null) {
			finder.shader(vertexSourceId, fragmentSourceId, depthVertexSourceId, depthFragmentSourceId);
		}

		// We test for tags even though getBoolean also does so because we don't necessarily know the correct default value
		if (obj.has("disableAo")) {
			finder.disableAo(GsonHelper.getAsBoolean(obj, "disableAo", false));
		}

		if (obj.has("disableColorIndex")) {
			finder.disableColorIndex(GsonHelper.getAsBoolean(obj, "disableColorIndex", false));
		}

		if (obj.has("disableDiffuse")) {
			finder.disableDiffuse(GsonHelper.getAsBoolean(obj, "disableDiffuse", false));
		}

		if (obj.has("emissive")) {
			finder.emissive(GsonHelper.getAsBoolean(obj, "emissive", false));
		}

		if (obj.has("blendMode")) {
			finder.preset(readPreset(GsonHelper.getAsString(obj, "blendMode")));
		}

		if (obj.has("preset")) {
			finder.preset(readPreset(GsonHelper.getAsString(obj, "preset")));
		}

		if (obj.has("blur")) {
			finder.blur(GsonHelper.getAsBoolean(obj, "blur", false));
		}

		if (obj.has("cull")) {
			finder.cull(GsonHelper.getAsBoolean(obj, "cull", false));
		}

		if (obj.has("cutout")) {
			final int cutout = readCutout(GsonHelper.getAsString(obj, "cutout"));
			finder.cutout(cutout == -1 ? CUTOUT_NONE : cutout);
		}

		if (obj.has("decal")) {
			final int decal = readDecal(GsonHelper.getAsString(obj, "decal"));
			finder.decal(decal == -1 ? DECAL_NONE : decal);
		}

		if (obj.has("depthTest")) {
			final int depthTest = readDepthTest(GsonHelper.getAsString(obj, "depthTest"));
			finder.depthTest(depthTest == -1 ? DEPTH_TEST_DISABLE : depthTest);
		}

		if (obj.has("discardsTexture")) {
			finder.discardsTexture(GsonHelper.getAsBoolean(obj, "discardsTexture", false));
		}

		if (obj.has("flashOverlay")) {
			finder.flashOverlay(GsonHelper.getAsBoolean(obj, "flashOverlay", false));
		}

		if (obj.has("fog")) {
			finder.fog(GsonHelper.getAsBoolean(obj, "fog", true));
		}

		if (obj.has("hurtOverlay")) {
			finder.hurtOverlay(GsonHelper.getAsBoolean(obj, "hurtOverlay", false));
		}

		if (obj.has("lines")) {
			finder.lines(GsonHelper.getAsBoolean(obj, "lines", false));
		}

		if (obj.has("sorted")) {
			finder.sorted(GsonHelper.getAsBoolean(obj, "sorted", false));
		}

		if (obj.has("target")) {
			final int target = readTarget(GsonHelper.getAsString(obj, "target"));
			finder.target(target == -1 ? TARGET_MAIN : target);
		}

		if (obj.has("texture")) {
			finder.texture(new ResourceLocation(GsonHelper.getAsString(obj, "texture")));
		}

		if (obj.has("transparency")) {
			final int transparency = readTransparency(GsonHelper.getAsString(obj, "transparency"));
			finder.transparency(transparency == -1 ? TRANSPARENCY_NONE : transparency);
		}

		if (obj.has("unmipped")) {
			finder.unmipped(GsonHelper.getAsBoolean(obj, "unmipped", false));
		}

		if (obj.has("writeMask")) {
			final int writeMask = readWriteMask(GsonHelper.getAsString(obj, "writeMask"));
			finder.writeMask(writeMask == -1 ? WRITE_MASK_COLOR_DEPTH : writeMask);
		}

		if (obj.has("castShadows")) {
			finder.castShadows(GsonHelper.getAsBoolean(obj, "castShadows", true));
		}
	}

	public static int readPreset(String val) {
		val = val.toLowerCase(Locale.ROOT);

		switch (val) {
			case "solid":
				return MaterialConstants.PRESET_SOLID;
			case "cutout":
				return MaterialConstants.PRESET_CUTOUT;
			case "cutout_mipped":
				return MaterialConstants.PRESET_CUTOUT_MIPPED;
			case "translucent":
				return MaterialConstants.PRESET_TRANSLUCENT;
			case "default":
				return MaterialConstants.PRESET_DEFAULT;
			default:
				return MaterialConstants.PRESET_NONE;
		}
	}

	public static int readCutout(String cutout) {
		cutout = cutout.toLowerCase(Locale.ROOT);

		switch (cutout) {
			case "cutout_half":
				return CUTOUT_HALF;
			case "cutout_tenth":
				return CUTOUT_TENTH;
			case "cutout_zero":
				return CUTOUT_ZERO;
			case "cutout_alpha":
				return CUTOUT_ALPHA;
			case "cutout_none":
				return CUTOUT_NONE;
			default:
				return -1;
		}
	}

	public static int readDecal(String decal) {
		decal = decal.toLowerCase(Locale.ROOT);

		switch (decal) {
			case "polygon_offset":
				return DECAL_POLYGON_OFFSET;
			case "view_offset":
				return DECAL_VIEW_OFFSET;
			case "none":
				return DECAL_NONE;
			default:
				return -1;
		}
	}

	public static int readDepthTest(String depthTest) {
		depthTest = depthTest.toLowerCase(Locale.ROOT);

		switch (depthTest) {
			case "always":
				return DEPTH_TEST_ALWAYS;
			case "equal":
				return DEPTH_TEST_EQUAL;
			case "lequal":
				return DEPTH_TEST_LEQUAL;
			case "disable":
				return DEPTH_TEST_DISABLE;
			default:
				return -1;
		}
	}

	public static int readTarget(String target) {
		target = target.toLowerCase(Locale.ROOT);

		switch (target) {
			case "main":
				return TARGET_MAIN;
			case "outline":
				return TARGET_OUTLINE;
			case "translucent":
				return TARGET_TRANSLUCENT;
			case "particles":
				return TARGET_PARTICLES;
			case "weather":
				return TARGET_WEATHER;
			case "clouds":
				return TARGET_CLOUDS;
			case "entities":
				return TARGET_ENTITIES;
			default:
				return -1;
		}
	}

	public static int readTransparency(String transparency) {
		transparency = transparency.toLowerCase(Locale.ROOT);

		switch (transparency) {
			case "none":
				return TRANSPARENCY_NONE;
			case "additive":
				return TRANSPARENCY_ADDITIVE;
			case "lightning":
				return TRANSPARENCY_LIGHTNING;
			case "glint":
				return TRANSPARENCY_GLINT;
			case "crumbling":
				return TRANSPARENCY_CRUMBLING;
			case "translucent":
				return TRANSPARENCY_TRANSLUCENT;
			case "default":
				return TRANSPARENCY_DEFAULT;
			default:
				return -1;
		}
	}

	public static int readWriteMask(String writeMask) {
		writeMask = writeMask.toLowerCase(Locale.ROOT);

		switch (writeMask) {
			case "color":
				return WRITE_MASK_COLOR;
			case "depth":
				return WRITE_MASK_DEPTH;
			case "color_depth":
				return WRITE_MASK_COLOR_DEPTH;
			default:
				return -1;
		}
	}
}
