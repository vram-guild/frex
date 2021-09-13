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

package io.vram.frex.impl.material.predicate;

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
import java.util.function.BiPredicate;

import com.google.gson.JsonElement;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.impl.material.MaterialDeserializer;

public class MaterialTester<T> extends MaterialPredicate {
	public static final Test<String> TEXTURE_TEST = (renderMaterial, s) -> renderMaterial.texture().equals(s);
	public static final Test<String> VERTEX_SOURCE_TEST = (renderMaterial, s) -> renderMaterial.vertexShader().equals(s);
	public static final Test<String> FRAGMENT_SOURCE_TEST = (renderMaterial, s) -> renderMaterial.fragmentShader().equals(s);
	public static final Test<String> RENDER_LAYER_NAME_TEST = (renderMaterial, s) -> renderMaterial.renderLayerName().equals(s);
	public static final Test<Boolean> DISABLE_AO_TEST = (renderMaterial, b) -> (renderMaterial.disableAo() == b);
	public static final Test<Boolean> DISABLE_COLOR_INDEX_TEST = (renderMaterial, b) -> (renderMaterial.disableColorIndex() == b);
	public static final Test<Boolean> DISABLE_DIFFUSE_TEST = (renderMaterial, b) -> (renderMaterial.disableDiffuse() == b);
	public static final Test<Boolean> EMISSIVE_TEST = (renderMaterial, b) -> (renderMaterial.emissive() == b);
	public static final Test<Integer> PRESET_TEST = (renderMaterial, b) -> (renderMaterial.preset() == b);
	public static final Test<Boolean> BLUR_TEST = (renderMaterial, b) -> (renderMaterial.blur() == b);
	public static final Test<Boolean> CULL_TEST = (renderMaterial, b) -> (renderMaterial.cull() == b);
	public static final Test<Integer> CUTOUT_TEST = (renderMaterial, i) -> (renderMaterial.cutout() == i);
	public static final Test<Integer> DECAL_TEST = (renderMaterial, i) -> (renderMaterial.decal() == i);
	public static final Test<Integer> DEPTH_TEST_TEST = (renderMaterial, i) -> (renderMaterial.depthTest() == i);
	public static final Test<Boolean> DISCARDS_TEXTURE_TEST = (renderMaterial, b) -> (renderMaterial.discardsTexture() == b);
	public static final Test<Boolean> FLASH_OVERLAY_TEST = (renderMaterial, b) -> (renderMaterial.flashOverlay() == b);
	public static final Test<Boolean> FOG_TEST = (renderMaterial, b) -> (renderMaterial.fog() == b);
	public static final Test<Boolean> HURT_OVERLAY_TEST = (renderMaterial, b) -> (renderMaterial.hurtOverlay() == b);
	public static final Test<Boolean> LINES_TEST = (renderMaterial, b) -> (renderMaterial.lines() == b);
	public static final Test<Boolean> SORTED_TEST = (renderMaterial, b) -> (renderMaterial.sorted() == b);
	public static final Test<Integer> TARGET_TEST = (renderMaterial, i) -> (renderMaterial.target() == i);
	public static final Test<Integer> TRANSPARENCY_TEST = (renderMaterial, i) -> (renderMaterial.transparency() == i);
	public static final Test<Boolean> UNMIPPED_TEST = (renderMaterial, b) -> (renderMaterial.unmipped() == b);
	public static final Test<Integer> WRITE_MASK_TEST = (renderMaterial, i) -> (renderMaterial.writeMask() == i);

	public static MaterialTester<String> createString(JsonElement je, Test<String> test) {
		try {
			return new MaterialTester<>(je.getAsString(), test);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Boolean> createBoolean(JsonElement je, Test<Boolean> test) {
		try {
			return new MaterialTester<>(je.getAsBoolean(), test);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createInt(JsonElement je, Test<Integer> test) {
		try {
			return new MaterialTester<>(je.getAsInt(), test);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createBlendMode(JsonElement je) {
		try {
			return new MaterialTester<>(MaterialDeserializer.readPreset(je.getAsString()), PRESET_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	// TODO: create public string to int mapper in MaterialDeserializer similar to BlendMode
	public static MaterialTester<Integer> createCutout(JsonElement je) {
		try {
			final String cutout = je.getAsString().toLowerCase(Locale.ROOT);
			int val;

			if (cutout.equals("cutout_half")) {
				val = CUTOUT_HALF;
			} else if (cutout.equals("cutout_tenth")) {
				val = CUTOUT_TENTH;
			} else if (cutout.equals("cutout_zero")) {
				val = CUTOUT_ZERO;
			} else if (cutout.equals("cutout_alpha")) {
				val = CUTOUT_ALPHA;
			} else {
				val = CUTOUT_NONE;
			}

			return new MaterialTester<>(val, CUTOUT_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	// TODO: create public string to int mapper in MaterialDeserializer similar to BlendMode
	public static MaterialTester<Integer> createDecal(JsonElement je) {
		try {
			final String decal = je.getAsString().toLowerCase(Locale.ROOT);
			int val;

			if (decal.equals("polygon_offset")) {
				val = DECAL_POLYGON_OFFSET;
			} else if (decal.equals("view_offset")) {
				val = DECAL_VIEW_OFFSET;
			} else if (decal.equals("none")) {
				val = DECAL_NONE;
			} else {
				return null;
			}

			return new MaterialTester<>(val, DECAL_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	// TODO: create public string to int mapper in MaterialDeserializer similar to BlendMode
	public static MaterialTester<Integer> createDepthTest(JsonElement je) {
		try {
			final String depthTest = je.getAsString().toLowerCase(Locale.ROOT);
			int val;

			if (depthTest.equals("always")) {
				val = DEPTH_TEST_ALWAYS;
			} else if (depthTest.equals("equal")) {
				val = DEPTH_TEST_EQUAL;
			} else if (depthTest.equals("lequal")) {
				val = DEPTH_TEST_LEQUAL;
			} else if (depthTest.equals("disable")) {
				val = DEPTH_TEST_DISABLE;
			} else {
				return null;
			}

			return new MaterialTester<>(val, DEPTH_TEST_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	// TODO: create public string to int mapper in MaterialDeserializer similar to BlendMode
	public static MaterialTester<Integer> createTarget(JsonElement je) {
		try {
			final String target = je.getAsString().toLowerCase(Locale.ROOT);
			int val;

			if (target.equals("main")) {
				val = TARGET_MAIN;
			} else if (target.equals("outline")) {
				val = TARGET_OUTLINE;
			} else if (target.equals("translucent")) {
				val = TARGET_TRANSLUCENT;
			} else if (target.equals("particles")) {
				val = TARGET_PARTICLES;
			} else if (target.equals("weather")) {
				val = TARGET_WEATHER;
			} else if (target.equals("clouds")) {
				val = TARGET_CLOUDS;
			} else if (target.equals("entities")) {
				val = TARGET_ENTITIES;
			} else {
				return null;
			}

			return new MaterialTester<>(val, TARGET_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	// TODO: create public string to int mapper in MaterialDeserializer similar to BlendMode
	public static MaterialTester<Integer> createTransparency(JsonElement je) {
		try {
			final String transparency = je.getAsString().toLowerCase(Locale.ROOT);
			int val;

			if (transparency.equals("none")) {
				val = TRANSPARENCY_NONE;
			} else if (transparency.equals("additive")) {
				val = TRANSPARENCY_ADDITIVE;
			} else if (transparency.equals("lightning")) {
				val = TRANSPARENCY_LIGHTNING;
			} else if (transparency.equals("glint")) {
				val = TRANSPARENCY_GLINT;
			} else if (transparency.equals("crumbling")) {
				val = TRANSPARENCY_CRUMBLING;
			} else if (transparency.equals("translucent")) {
				val = TRANSPARENCY_TRANSLUCENT;
			} else if (transparency.equals("default")) {
				val = TRANSPARENCY_DEFAULT;
			} else {
				return null;
			}

			return new MaterialTester<>(val, TRANSPARENCY_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	// TODO: create public string to int mapper in MaterialDeserializer similar to BlendMode
	public static MaterialTester<Integer> createWriteMask(JsonElement je) {
		try {
			final String writeMask = je.getAsString().toLowerCase(Locale.ROOT);
			int val;

			if (writeMask.equals("color")) {
				val = WRITE_MASK_COLOR;
			} else if (writeMask.equals("depth")) {
				val = WRITE_MASK_DEPTH;
			} else if (writeMask.equals("color_depth")) {
				val = WRITE_MASK_COLOR_DEPTH;
			} else {
				return null;
			}

			return new MaterialTester<>(val, WRITE_MASK_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	public final T toTest;
	public final Test<T> test;

	public MaterialTester(T toTest, Test<T> test) {
		this.toTest = toTest;
		this.test = test;
	}

	@Override
	public boolean test(RenderMaterial renderMaterial) {
		return test.test(renderMaterial, toTest);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MaterialTester) {
			@SuppressWarnings("unchecked")
			final MaterialTester<T> casted = (MaterialTester<T>) obj;
			return this.toTest.equals(casted.toTest) && test == casted.test;
		} else {
			return false;
		}
	}

	// prevent creating arbitrary BiPredicate as part of equals() logic
	private interface Test<T> extends BiPredicate<RenderMaterial, T> {
	}
}
