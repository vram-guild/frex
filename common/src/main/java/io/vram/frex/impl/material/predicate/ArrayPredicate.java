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

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import io.vram.frex.api.material.RenderMaterial;

public class ArrayPredicate extends MaterialPredicate {
	private final MaterialPredicate[] compact;
	// strictly for equation
	private final MaterialPredicate[] sparse;

	private static void addOrNull(ObjectArrayList<MaterialPredicate> sparseList, ObjectArrayList<MaterialPredicate> compactList, JsonObject json, String property, Function<JsonElement, MaterialPredicate> factory) {
		if (json.has(property)) {
			final MaterialPredicate created = factory.apply(json.get(property));

			if (created != null) {
				compactList.add(created);
			}

			sparseList.add(created);
		} else {
			sparseList.add(null);
		}
	}

	public ArrayPredicate(JsonObject json) {
		final ObjectArrayList<MaterialPredicate> sparsePredicates = new ObjectArrayList<>();
		final ObjectArrayList<MaterialPredicate> compactPredicates = new ObjectArrayList<>();

		addOrNull(sparsePredicates, compactPredicates, json, "texture", e -> MaterialTester.createString(e, MaterialTester.TEXTURE_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "vertexSource", e -> MaterialTester.createString(e, MaterialTester.VERTEX_SOURCE_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "fragmentSource", e -> MaterialTester.createString(e, MaterialTester.FRAGMENT_SOURCE_TEST));
		// deprecated - use label instead
		addOrNull(sparsePredicates, compactPredicates, json, "renderLayerName", e -> MaterialTester.createString(e, MaterialTester.LABEL_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "label", e -> MaterialTester.createString(e, MaterialTester.LABEL_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "disableAo", e -> MaterialTester.createBoolean(e, MaterialTester.DISABLE_AO_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "disableColorIndex", e -> MaterialTester.createBoolean(e, MaterialTester.DISABLE_COLOR_INDEX_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "disableDiffuse", e -> MaterialTester.createBoolean(e, MaterialTester.DISABLE_DIFFUSE_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "emissive", e -> MaterialTester.createBoolean(e, MaterialTester.EMISSIVE_TEST));
		// deprecated - use preset instead
		addOrNull(sparsePredicates, compactPredicates, json, "blendMode", MaterialTester::createPreset);
		addOrNull(sparsePredicates, compactPredicates, json, "preset", MaterialTester::createPreset);
		addOrNull(sparsePredicates, compactPredicates, json, "blur", e -> MaterialTester.createBoolean(e, MaterialTester.BLUR_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "cull", e -> MaterialTester.createBoolean(e, MaterialTester.CULL_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "cutout", MaterialTester::createCutout);
		addOrNull(sparsePredicates, compactPredicates, json, "decal", MaterialTester::createDecal);
		addOrNull(sparsePredicates, compactPredicates, json, "depthTest", MaterialTester::createDepthTest);
		addOrNull(sparsePredicates, compactPredicates, json, "discardsTexture", e -> MaterialTester.createBoolean(e, MaterialTester.DISCARDS_TEXTURE_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "flashOverlay", e -> MaterialTester.createBoolean(e, MaterialTester.FLASH_OVERLAY_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "fog", e -> MaterialTester.createBoolean(e, MaterialTester.FOG_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "hurtOverlay", e -> MaterialTester.createBoolean(e, MaterialTester.HURT_OVERLAY_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "lines", e -> MaterialTester.createBoolean(e, MaterialTester.LINES_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "sorted", e -> MaterialTester.createBoolean(e, MaterialTester.SORTED_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "target", MaterialTester::createTarget);
		addOrNull(sparsePredicates, compactPredicates, json, "transparency", MaterialTester::createTransparency);
		addOrNull(sparsePredicates, compactPredicates, json, "unmipped", e -> MaterialTester.createBoolean(e, MaterialTester.UNMIPPED_TEST));
		addOrNull(sparsePredicates, compactPredicates, json, "writeMask", MaterialTester::createWriteMask);

		sparse = sparsePredicates.toArray(new MaterialPredicate[0]);
		compact = compactPredicates.toArray(new MaterialPredicate[0]);
	}

	public int size() {
		return compact.length;
	}

	public MaterialPredicate get(int i) {
		return compact[i];
	}

	@Override
	public boolean test(RenderMaterial material) {
		final int limit = compact.length;

		for (int i = 0; i < limit; ++i) {
			if (!compact[i].test(material)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ArrayPredicate)) {
			return false;
		}

		final int limit = sparse.length;

		for (int i = 0; i < limit; ++i) {
			final MaterialPredicate own = sparse[i];
			final MaterialPredicate theirs = ((ArrayPredicate) obj).sparse[i];

			if (own != null && theirs != null) {
				if (!own.equals(theirs)) {
					return false;
				}
			} else if (own != theirs) {
				return false;
			}
		}

		return true;
	}
}
