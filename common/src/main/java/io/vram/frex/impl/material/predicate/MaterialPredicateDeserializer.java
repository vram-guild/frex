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

package io.vram.frex.impl.material.predicate;

import static io.vram.frex.impl.material.predicate.MaterialPredicate.MATERIAL_ALWAYS_TRUE;

import com.google.gson.JsonObject;

public class MaterialPredicateDeserializer {
	public static MaterialPredicate deserialize(JsonObject json) {
		if (json == null || json.isJsonNull()) {
			return MATERIAL_ALWAYS_TRUE;
		}

		final ArrayPredicate arrayPredicate = new ArrayPredicate(json);

		if (arrayPredicate.size() == 0) {
			return MATERIAL_ALWAYS_TRUE;
		} else if (arrayPredicate.size() == 1) {
			return arrayPredicate.get(0);
		} else {
			return arrayPredicate;
		}
	}
}
