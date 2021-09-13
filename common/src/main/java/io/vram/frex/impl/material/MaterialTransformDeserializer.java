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

import com.google.gson.JsonObject;
import io.vram.frex.api.material.MaterialFinder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Internal
public class MaterialTransformDeserializer {
	private MaterialTransformDeserializer() { }

	public static MaterialTransform deserialize(JsonObject json) {
		if (!JsonHelper.getBoolean(json, "transform", false)) {
			return MaterialTransform.constant(MaterialDeserializer.deserialize(json));
		}

		final ObjectArrayList<MaterialTransform> transforms = new ObjectArrayList<>();

		readTransforms(json, transforms);

		if (transforms.isEmpty()) {
			return MaterialTransform.IDENTITY;
		} else if (transforms.size() == 1) {
			return transforms.get(0);
		} else {
			return new ArrayTransform(transforms.toArray(new MaterialTransform[transforms.size()]));
		}
	}

	private static class ArrayTransform implements MaterialTransform {
		private final MaterialTransform[] transforms;

		private ArrayTransform(MaterialTransform[] transforms) {
			this.transforms = transforms;
		}

		@Override
		public void apply(MaterialFinder finder) {
			final int limit = transforms.length;

			for (int i = 0; i < limit; ++i) {
				transforms[i].apply(finder);
			}
		}
	}

	private static void readTransforms(JsonObject json, ObjectArrayList<MaterialTransform> transforms) {
		final boolean hasFragment = json.has("fragmentSource");
		final boolean hasVertex = json.has("vertexSource");

		if (hasFragment || hasVertex) {
			final Identifier vs = hasVertex ? new Identifier(JsonHelper.getString(json, "vertexSource")) : null;
			final Identifier fs = hasFragment ? new Identifier(JsonHelper.getString(json, "fragmentSource")) : null;
			transforms.add(finder -> finder.shader(vs, fs));
		}

		// We test for tags even though getBoolean also does so because we don't necessarily know the correct default value
		if (json.has("disableAo")) {
			final boolean val = JsonHelper.getBoolean(json, "disableAo", true);
			transforms.add(finder -> finder.disableAo(val));
		}

		if (json.has("disableColorIndex")) {
			final boolean val = JsonHelper.getBoolean(json, "disableColorIndex", true);
			transforms.add(finder -> finder.disableColorIndex(val));
		}

		if (json.has("disableDiffuse")) {
			final boolean val = JsonHelper.getBoolean(json, "disableDiffuse", true);
			transforms.add(finder -> finder.disableDiffuse(val));
		}

		if (json.has("emissive")) {
			final boolean val = JsonHelper.getBoolean(json, "emissive", true);
			transforms.add(finder -> finder.emissive(val));
		}

		if (json.has("blendMode")) {
			final int val = MaterialDeserializer.readPreset(JsonHelper.getString(json, "blendMode"));
			transforms.add(finder -> finder.preset(val));
		}

		if (json.has("preset")) {
			final int val = MaterialDeserializer.readPreset(JsonHelper.getString(json, "preset"));
			transforms.add(finder -> finder.preset(val));
		}

		if (json.has("blur")) {
			final boolean val = JsonHelper.getBoolean(json, "blur", true);
			transforms.add(finder -> finder.blur(val));
		}

		if (json.has("cull")) {
			final boolean val = JsonHelper.getBoolean(json, "cull", true);
			transforms.add(finder -> finder.cull(val));
		}

		if (json.has("cutout")) {
			final String cutout = json.get("cutout").getAsString().toLowerCase(Locale.ROOT);

			if (cutout.equals("cutout_half")) {
				transforms.add(finder -> finder.decal(CUTOUT_HALF));
			} else if (cutout.equals("cutout_tenth")) {
				transforms.add(finder -> finder.decal(CUTOUT_TENTH));
			} else if (cutout.equals("cutout_zero")) {
				transforms.add(finder -> finder.decal(CUTOUT_ZERO));
			} else if (cutout.equals("cutout_alpha")) {
				transforms.add(finder -> finder.cutout(CUTOUT_ALPHA));
			} else {
				transforms.add(finder -> finder.cutout(CUTOUT_NONE));
			}
		}

		if (json.has("decal")) {
			final String decal = json.get("decal").getAsString().toLowerCase(Locale.ROOT);

			if (decal.equals("polygon_offset")) {
				transforms.add(finder -> finder.decal(DECAL_POLYGON_OFFSET));
			} else if (decal.equals("view_offset")) {
				transforms.add(finder -> finder.decal(DECAL_VIEW_OFFSET));
			} else if (decal.equals("none")) {
				transforms.add(finder -> finder.decal(DECAL_NONE));
			}
		}

		if (json.has("depthTest")) {
			final String depthTest = json.get("depthTest").getAsString().toLowerCase(Locale.ROOT);

			if (depthTest.equals("always")) {
				transforms.add(finder -> finder.depthTest(DEPTH_TEST_ALWAYS));
			} else if (depthTest.equals("equal")) {
				transforms.add(finder -> finder.depthTest(DEPTH_TEST_EQUAL));
			} else if (depthTest.equals("lequal")) {
				transforms.add(finder -> finder.depthTest(DEPTH_TEST_LEQUAL));
			} else if (depthTest.equals("disable")) {
				transforms.add(finder -> finder.depthTest(DEPTH_TEST_DISABLE));
			}
		}

		if (json.has("discardsTexture")) {
			final boolean val = JsonHelper.getBoolean(json, "discardsTexture", true);
			transforms.add(finder -> finder.discardsTexture(val));
		}

		if (json.has("flashOverlay")) {
			final boolean val = JsonHelper.getBoolean(json, "flashOverlay", true);
			transforms.add(finder -> finder.flashOverlay(val));
		}

		if (json.has("fog")) {
			final boolean val = JsonHelper.getBoolean(json, "fog", false);
			transforms.add(finder -> finder.fog(val));
		}

		if (json.has("hurtOverlay")) {
			final boolean val = JsonHelper.getBoolean(json, "hurtOverlay", true);
			transforms.add(finder -> finder.hurtOverlay(val));
		}

		if (json.has("lines")) {
			final boolean val = JsonHelper.getBoolean(json, "lines", true);
			transforms.add(finder -> finder.lines(val));
		}

		if (json.has("sorted")) {
			final boolean val = JsonHelper.getBoolean(json, "sorted", true);
			transforms.add(finder -> finder.sorted(val));
		}

		if (json.has("target")) {
			final String target = json.get("target").getAsString().toLowerCase(Locale.ROOT);

			if (target.equals("main")) {
				transforms.add(finder -> finder.target(TARGET_MAIN));
			} else if (target.equals("outline")) {
				transforms.add(finder -> finder.target(TARGET_OUTLINE));
			} else if (target.equals("translucent")) {
				transforms.add(finder -> finder.target(TARGET_TRANSLUCENT));
			} else if (target.equals("particles")) {
				transforms.add(finder -> finder.target(TARGET_PARTICLES));
			} else if (target.equals("weather")) {
				transforms.add(finder -> finder.target(TARGET_WEATHER));
			} else if (target.equals("clouds")) {
				transforms.add(finder -> finder.target(TARGET_CLOUDS));
			} else if (target.equals("entities")) {
				transforms.add(finder -> finder.target(TARGET_ENTITIES));
			}
		}

		if (json.has("texture")) {
			final Identifier texture = new Identifier(JsonHelper.getString(json, "texture"));
			transforms.add(finder -> finder.texture(texture));
		}

		if (json.has("transparency")) {
			final String transparency = json.get("transparency").getAsString().toLowerCase(Locale.ROOT);

			if (transparency.equals("none")) {
				transforms.add(finder -> finder.transparency(TRANSPARENCY_NONE));
			} else if (transparency.equals("additive")) {
				transforms.add(finder -> finder.transparency(TRANSPARENCY_ADDITIVE));
			} else if (transparency.equals("lightning")) {
				transforms.add(finder -> finder.transparency(TRANSPARENCY_LIGHTNING));
			} else if (transparency.equals("glint")) {
				transforms.add(finder -> finder.transparency(TRANSPARENCY_GLINT));
			} else if (transparency.equals("crumbling")) {
				transforms.add(finder -> finder.transparency(TRANSPARENCY_CRUMBLING));
			} else if (transparency.equals("translucent")) {
				transforms.add(finder -> finder.transparency(TRANSPARENCY_TRANSLUCENT));
			} else if (transparency.equals("default")) {
				transforms.add(finder -> finder.transparency(TRANSPARENCY_DEFAULT));
			}
		}

		if (json.has("unmipped")) {
			final boolean val = JsonHelper.getBoolean(json, "unmipped", true);
			transforms.add(finder -> finder.unmipped(val));
		}

		if (json.has("writeMask")) {
			final String writeMask = json.get("writeMask").getAsString().toLowerCase(Locale.ROOT);

			if (writeMask.equals("color")) {
				transforms.add(finder -> finder.writeMask(WRITE_MASK_COLOR));
			} else if (writeMask.equals("depth")) {
				transforms.add(finder -> finder.writeMask(WRITE_MASK_DEPTH));
			} else if (writeMask.equals("color_depth")) {
				transforms.add(finder -> finder.writeMask(WRITE_MASK_COLOR_DEPTH));
			}
		}

		if (json.has("castShadows")) {
			final boolean val = JsonHelper.getBoolean(json, "castShadows", true);
			transforms.add(finder -> finder.castShadows(val));
		}
	}
}
