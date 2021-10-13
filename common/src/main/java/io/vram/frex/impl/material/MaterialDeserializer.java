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
			final String cutout = obj.get("cutout").getAsString().toLowerCase(Locale.ROOT);

			if (cutout.equals("cutout_half")) {
				finder.decal(CUTOUT_HALF);
			} else if (cutout.equals("cutout_tenth")) {
				finder.decal(CUTOUT_TENTH);
			} else if (cutout.equals("cutout_zero")) {
				finder.decal(CUTOUT_ZERO);
			} else if (cutout.equals("cutout_alpha")) {
				finder.cutout(CUTOUT_ALPHA);
			} else {
				finder.cutout(CUTOUT_NONE);
			}
		}

		if (obj.has("decal")) {
			final String decal = obj.get("decal").getAsString().toLowerCase(Locale.ROOT);

			if (decal.equals("polygon_offset")) {
				finder.decal(DECAL_POLYGON_OFFSET);
			} else if (decal.equals("view_offset")) {
				finder.decal(DECAL_VIEW_OFFSET);
			} else if (decal.equals("none")) {
				finder.decal(DECAL_NONE);
			}
		}

		if (obj.has("depthTest")) {
			final String depthTest = obj.get("depthTest").getAsString().toLowerCase(Locale.ROOT);

			if (depthTest.equals("always")) {
				finder.depthTest(DEPTH_TEST_ALWAYS);
			} else if (depthTest.equals("equal")) {
				finder.depthTest(DEPTH_TEST_EQUAL);
			} else if (depthTest.equals("lequal")) {
				finder.depthTest(DEPTH_TEST_LEQUAL);
			} else if (depthTest.equals("disable")) {
				finder.depthTest(DEPTH_TEST_DISABLE);
			}
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
			final String target = obj.get("target").getAsString().toLowerCase(Locale.ROOT);

			if (target.equals("main")) {
				finder.target(TARGET_MAIN);
			} else if (target.equals("outline")) {
				finder.target(TARGET_OUTLINE);
			} else if (target.equals("translucent")) {
				finder.target(TARGET_TRANSLUCENT);
			} else if (target.equals("particles")) {
				finder.target(TARGET_PARTICLES);
			} else if (target.equals("weather")) {
				finder.target(TARGET_WEATHER);
			} else if (target.equals("clouds")) {
				finder.target(TARGET_CLOUDS);
			} else if (target.equals("entities")) {
				finder.target(TARGET_ENTITIES);
			}
		}

		if (obj.has("texture")) {
			finder.texture(new ResourceLocation(GsonHelper.getAsString(obj, "texture")));
		}

		if (obj.has("transparency")) {
			final String transparency = obj.get("transparency").getAsString().toLowerCase(Locale.ROOT);

			if (transparency.equals("none")) {
				finder.transparency(TRANSPARENCY_NONE);
			} else if (transparency.equals("additive")) {
				finder.transparency(TRANSPARENCY_ADDITIVE);
			} else if (transparency.equals("lightning")) {
				finder.transparency(TRANSPARENCY_LIGHTNING);
			} else if (transparency.equals("glint")) {
				finder.transparency(TRANSPARENCY_GLINT);
			} else if (transparency.equals("crumbling")) {
				finder.transparency(TRANSPARENCY_CRUMBLING);
			} else if (transparency.equals("translucent")) {
				finder.transparency(TRANSPARENCY_TRANSLUCENT);
			} else if (transparency.equals("default")) {
				finder.transparency(TRANSPARENCY_DEFAULT);
			}
		}

		if (obj.has("unmipped")) {
			finder.unmipped(GsonHelper.getAsBoolean(obj, "unmipped", false));
		}

		if (obj.has("writeMask")) {
			final String writeMask = obj.get("writeMask").getAsString().toLowerCase(Locale.ROOT);

			if (writeMask.equals("color")) {
				finder.writeMask(WRITE_MASK_COLOR);
			} else if (writeMask.equals("depth")) {
				finder.writeMask(WRITE_MASK_DEPTH);
			} else if (writeMask.equals("color_depth")) {
				finder.writeMask(WRITE_MASK_COLOR_DEPTH);
			}
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
}
