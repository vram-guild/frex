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

import java.io.InputStreamReader;
import java.util.IdentityHashMap;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;

import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.impl.FrexLog;

@Internal
public class ItemMaterialMapDeserializer {
	public static void deserialize(Item item, ResourceLocation idForLog, InputStreamReader reader, IdentityHashMap<Item, MaterialMap> itemMap) {
		try {
			final JsonObject json = GsonHelper.parse(reader);
			final String idString = idForLog.toString();

			final MaterialMap globalDefaultMap = MaterialMapLoader.DEFAULT_MAP;
			@Nullable RenderMaterial defaultMaterial = null;
			MaterialMap result = globalDefaultMap;

			if (json.has("defaultMaterial")) {
				defaultMaterial = MaterialLoaderImpl.loadMaterial(json.get("defaultMaterial").getAsString(), defaultMaterial);
				result = new SingleMaterialMap(defaultMaterial);
			}

			if (json.has("defaultMap")) {
				result = MaterialMapDeserializer.loadMaterialMap(idString + "#default", json.getAsJsonObject("defaultMap"), result, defaultMaterial);
			}

			if (result != globalDefaultMap) {
				itemMap.put(item, result);
			}
		} catch (final Exception e) {
			FrexLog.warn("Unable to load block material map for " + idForLog.toString() + " due to unhandled exception:", e);
		}
	}
}
