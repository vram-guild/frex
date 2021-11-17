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

package io.vram.frex.impl.material.predicate;

import java.util.function.BiPredicate;

import com.google.gson.JsonElement;

import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.impl.material.MaterialDeserializer;

public class MaterialTester<T> extends MaterialPredicate {
	public static final Test<String> TEXTURE_TEST = (renderMaterial, s) -> renderMaterial.texture().idAsString().equals(s);
	public static final Test<String> VERTEX_SOURCE_TEST = (renderMaterial, s) -> renderMaterial.shader().vertexShader().equals(s);
	public static final Test<String> FRAGMENT_SOURCE_TEST = (renderMaterial, s) -> renderMaterial.shader().fragmentShader().equals(s);
	public static final Test<String> LABEL_TEST = (renderMaterial, s) -> renderMaterial.label().equals(s);
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

	public static MaterialTester<Integer> createPreset(JsonElement je) {
		try {
			return new MaterialTester<>(MaterialDeserializer.readPreset(je.getAsString()), PRESET_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createCutout(JsonElement je) {
		try {
			final int cutout = MaterialDeserializer.readCutout(je.getAsString());
			return cutout == -1 ? null : new MaterialTester<>(cutout, CUTOUT_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createDecal(JsonElement je) {
		try {
			final int decal = MaterialDeserializer.readDecal(je.getAsString());
			return decal == -1 ? null : new MaterialTester<>(decal, DECAL_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createDepthTest(JsonElement je) {
		try {
			final int depthTest = MaterialDeserializer.readDepthTest(je.getAsString());
			return depthTest == -1 ? null : new MaterialTester<>(depthTest, DEPTH_TEST_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createTarget(JsonElement je) {
		try {
			final int target = MaterialDeserializer.readTarget(je.getAsString());
			return target == -1 ? null : new MaterialTester<>(target, TARGET_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createTransparency(JsonElement je) {
		try {
			final int transparency = MaterialDeserializer.readTransparency(je.getAsString());
			return transparency == -1 ? null : new MaterialTester<>(transparency, TRANSPARENCY_TEST);
		} catch (final Exception e) {
			return null;
		}
	}

	public static MaterialTester<Integer> createWriteMask(JsonElement je) {
		try {
			final int writeMask = MaterialDeserializer.readWriteMask(je.getAsString());
			return writeMask == -1 ? null : new MaterialTester<>(writeMask, WRITE_MASK_TEST);
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

	@Override
	public int hashCode() {
		return test.hashCode() ^ toTest.hashCode();
	}

	// prevent creating arbitrary BiPredicate as part of equals() logic
	private interface Test<T> extends BiPredicate<RenderMaterial, T> {
	}
}
