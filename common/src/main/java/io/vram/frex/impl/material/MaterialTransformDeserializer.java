/*
 * This file is part of FREX and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
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
 */

package io.vram.frex.impl.material;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.MaterialTransform;

@Internal
public class MaterialTransformDeserializer {
	private MaterialTransformDeserializer() { }

	public static MaterialTransform deserialize(JsonObject json) {
		if (!GsonHelper.getAsBoolean(json, "transform", false)) {
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
			final ResourceLocation vs = hasVertex ? new ResourceLocation(GsonHelper.getAsString(json, "vertexSource")) : null;
			final ResourceLocation fs = hasFragment ? new ResourceLocation(GsonHelper.getAsString(json, "fragmentSource")) : null;
			transforms.add(finder -> finder.shader(vs, fs));
		}

		// We test for tags even though getBoolean also does so because we don't necessarily know the correct default value
		if (json.has("disableAo")) {
			final boolean val = GsonHelper.getAsBoolean(json, "disableAo", true);
			transforms.add(finder -> finder.disableAo(val));
		}

		if (json.has("disableColorIndex")) {
			final boolean val = GsonHelper.getAsBoolean(json, "disableColorIndex", true);
			transforms.add(finder -> finder.disableColorIndex(val));
		}

		if (json.has("disableDiffuse")) {
			final boolean val = GsonHelper.getAsBoolean(json, "disableDiffuse", true);
			transforms.add(finder -> finder.disableDiffuse(val));
		}

		if (json.has("emissive")) {
			final boolean val = GsonHelper.getAsBoolean(json, "emissive", true);
			transforms.add(finder -> finder.emissive(val));
		}

		if (json.has("blendMode")) {
			final int val = MaterialDeserializer.readPreset(GsonHelper.getAsString(json, "blendMode"));
			transforms.add(finder -> finder.preset(val));
		}

		if (json.has("preset")) {
			final int val = MaterialDeserializer.readPreset(GsonHelper.getAsString(json, "preset"));
			transforms.add(finder -> finder.preset(val));
		}

		if (json.has("blur")) {
			final boolean val = GsonHelper.getAsBoolean(json, "blur", true);
			transforms.add(finder -> finder.blur(val));
		}

		if (json.has("cull")) {
			final boolean val = GsonHelper.getAsBoolean(json, "cull", true);
			transforms.add(finder -> finder.cull(val));
		}

		if (json.has("cutout")) {
			final int cutout = MaterialDeserializer.readCutout(json.get("cutout").getAsString());

			if (cutout != -1) {
				transforms.add(finder -> finder.cutout(cutout));
			}
		}

		if (json.has("decal")) {
			final int decal = MaterialDeserializer.readDecal(json.get("decal").getAsString());

			if (decal != -1) {
				transforms.add(finder -> finder.decal(decal));
			}
		}

		if (json.has("depthTest")) {
			final int depthTest = MaterialDeserializer.readDepthTest(json.get("depthTest").getAsString());

			if (depthTest != -1) {
				transforms.add(finder -> finder.depthTest(depthTest));
			}
		}

		if (json.has("discardsTexture")) {
			final boolean val = GsonHelper.getAsBoolean(json, "discardsTexture", true);
			transforms.add(finder -> finder.discardsTexture(val));
		}

		if (json.has("flashOverlay")) {
			final boolean val = GsonHelper.getAsBoolean(json, "flashOverlay", true);
			transforms.add(finder -> finder.flashOverlay(val));
		}

		if (json.has("fog")) {
			final boolean val = GsonHelper.getAsBoolean(json, "fog", false);
			transforms.add(finder -> finder.fog(val));
		}

		if (json.has("hurtOverlay")) {
			final boolean val = GsonHelper.getAsBoolean(json, "hurtOverlay", true);
			transforms.add(finder -> finder.hurtOverlay(val));
		}

		if (json.has("lines")) {
			final boolean val = GsonHelper.getAsBoolean(json, "lines", true);
			transforms.add(finder -> finder.lines(val));
		}

		if (json.has("sorted")) {
			final boolean val = GsonHelper.getAsBoolean(json, "sorted", true);
			transforms.add(finder -> finder.sorted(val));
		}

		if (json.has("target")) {
			final int target = MaterialDeserializer.readTarget(json.get("target").getAsString());

			if (target != -1) {
				transforms.add(finder -> finder.target(target));
			}
		}

		if (json.has("texture")) {
			final ResourceLocation texture = new ResourceLocation(GsonHelper.getAsString(json, "texture"));
			transforms.add(finder -> finder.texture(texture));
		}

		if (json.has("transparency")) {
			final int transparency = MaterialDeserializer.readTransparency(json.get("transparency").getAsString());

			if (transparency != -1) {
				transforms.add(finder -> finder.transparency(transparency));
			}
		}

		if (json.has("unmipped")) {
			final boolean val = GsonHelper.getAsBoolean(json, "unmipped", true);
			transforms.add(finder -> finder.unmipped(val));
		}

		if (json.has("writeMask")) {
			final int writeMask = MaterialDeserializer.readWriteMask(json.get("writeMask").getAsString());

			if (writeMask != -1) {
				transforms.add(finder -> finder.writeMask(writeMask));
			}
		}

		if (json.has("castShadows")) {
			final boolean val = GsonHelper.getAsBoolean(json, "castShadows", true);
			transforms.add(finder -> finder.castShadows(val));
		}
	}
}
