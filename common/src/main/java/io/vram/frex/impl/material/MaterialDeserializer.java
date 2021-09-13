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
import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.renderer.Renderer;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Internal
public class MaterialDeserializer {
	private MaterialDeserializer() { }

	private static final Renderer RENDERER = Renderer.get();
	private static final MaterialFinder FINDER = RENDERER.materialFinder();

	public static RenderMaterial deserialize(JsonObject json) {
		final MaterialFinder finder = FINDER.clear();

		// "layers" tag still support for old multi-layer format but
		// only first layer is used. Layer is not required.
		if (json.has("layers")) {
			final JsonArray layers = JsonHelper.asArray(json.get("layers"), "layers");

			if (!layers.isJsonNull() && layers.size() >= 1) {
				readMaterial(layers.get(0).getAsJsonObject(), finder);

				return finder.find();
			}
		}

		readMaterial(json, finder);
		return finder.find();
	}

	private static void readMaterial(JsonObject obj, io.vram.frex.api.material.MaterialFinder finder) {
		final String vertexSource = JsonHelper.getString(obj, "vertexSource", null);
		final String fragmentSource = JsonHelper.getString(obj, "fragmentSource", null);
		final String depthVertexSource = JsonHelper.getString(obj, "depthVertexSource", null);
		final String depthFragmentSource = JsonHelper.getString(obj, "depthFragmentSource", null);

		final Identifier vertexSourceId = vertexSource != null && Identifier.isValid(vertexSource) ? new Identifier(vertexSource) : null;
		final Identifier fragmentSourceId = fragmentSource != null && Identifier.isValid(fragmentSource) ? new Identifier(fragmentSource) : null;
		final Identifier depthVertexSourceId = depthVertexSource != null && Identifier.isValid(depthVertexSource) ? new Identifier(depthVertexSource) : null;
		final Identifier depthFragmentSourceId = depthFragmentSource != null && Identifier.isValid(depthFragmentSource) ? new Identifier(depthFragmentSource) : null;

		if (fragmentSourceId != null || vertexSourceId != null || depthFragmentSourceId != null || depthVertexSourceId != null) {
			finder.shader(vertexSourceId, fragmentSourceId, depthVertexSourceId, depthFragmentSourceId);
		}

		// We test for tags even though getBoolean also does so because we don't necessarily know the correct default value
		if (obj.has("disableAo")) {
			finder.disableAo(JsonHelper.getBoolean(obj, "disableAo", false));
		}

		if (obj.has("disableColorIndex")) {
			finder.disableColorIndex(JsonHelper.getBoolean(obj, "disableColorIndex", false));
		}

		if (obj.has("disableDiffuse")) {
			finder.disableDiffuse(JsonHelper.getBoolean(obj, "disableDiffuse", false));
		}

		if (obj.has("emissive")) {
			finder.emissive(JsonHelper.getBoolean(obj, "emissive", false));
		}

		if (obj.has("blendMode")) {
			finder.preset(readPreset(JsonHelper.getString(obj, "blendMode")));
		}

		if (obj.has("preset")) {
			finder.preset(readPreset(JsonHelper.getString(obj, "preset")));
		}

		if (obj.has("blur")) {
			finder.blur(JsonHelper.getBoolean(obj, "blur", false));
		}

		if (obj.has("cull")) {
			finder.cull(JsonHelper.getBoolean(obj, "cull", false));
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
			finder.discardsTexture(JsonHelper.getBoolean(obj, "discardsTexture", false));
		}

		if (obj.has("flashOverlay")) {
			finder.flashOverlay(JsonHelper.getBoolean(obj, "flashOverlay", false));
		}

		if (obj.has("fog")) {
			finder.fog(JsonHelper.getBoolean(obj, "fog", true));
		}

		if (obj.has("hurtOverlay")) {
			finder.hurtOverlay(JsonHelper.getBoolean(obj, "hurtOverlay", false));
		}

		if (obj.has("lines")) {
			finder.lines(JsonHelper.getBoolean(obj, "lines", false));
		}

		if (obj.has("sorted")) {
			finder.sorted(JsonHelper.getBoolean(obj, "sorted", false));
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
			finder.texture(new Identifier(JsonHelper.getString(obj, "texture")));
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
			finder.unmipped(JsonHelper.getBoolean(obj, "unmipped", false));
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
			finder.castShadows(JsonHelper.getBoolean(obj, "castShadows", true));
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
