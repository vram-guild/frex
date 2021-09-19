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

import java.io.InputStreamReader;
import java.util.IdentityHashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.impl.FrexLog;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.StateHolder;

@Internal
public final class MaterialMapDeserializer {
	private MaterialMapDeserializer() { }

	public static MaterialMap loadMaterialMap(String idForLog, JsonObject mapObject, MaterialMap defaultMap, @Nullable RenderMaterial defaultMaterial) {
		if (mapObject == null || mapObject.isJsonNull()) {
			return defaultMap;
		}

		try {
			if (mapObject.has("defaultMaterial")) {
				defaultMaterial = MaterialLoaderImpl.loadMaterial(mapObject.get("defaultMaterial").getAsString(), defaultMaterial);
				defaultMap = new SingleMaterialMap(defaultMaterial);
			}

			if (mapObject.has("spriteMap")) {
				final TextureAtlas blockAtlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
				final TextureAtlasSprite missingSprite = blockAtlas.getSprite(MissingTextureAtlasSprite.getLocation());

				final JsonArray jsonArray = mapObject.getAsJsonArray("spriteMap");
				final int limit = jsonArray.size();
				final IdentityHashMap<TextureAtlasSprite, RenderMaterial> spriteMap = new IdentityHashMap<>();

				for (int i = 0; i < limit; ++i) {
					final JsonObject obj = jsonArray.get(i).getAsJsonObject();

					if (!obj.has("sprite")) {
						FrexLog.warn("Unable to load material map " + idForLog + " because 'sprite' property is missing. Using default material.");
						continue;
					}

					final TextureAtlasSprite sprite = MaterialLoaderImpl.loadSprite(idForLog, obj.get("sprite").getAsString(), blockAtlas, missingSprite);

					if (sprite == null) {
						continue;
					}

					if (!obj.has("material")) {
						FrexLog.warn("Unable to load material map " + idForLog + " because 'material' property is missing. Using default material.");
						continue;
					}

					spriteMap.put(sprite, MaterialLoaderImpl.loadMaterial(obj.get("material").getAsString(), defaultMaterial));
				}

				return spriteMap.isEmpty() ? defaultMap : (defaultMaterial == null ? new MultiMaterialMap(spriteMap) : new DefaultedMultiMaterialMap(defaultMaterial, spriteMap));
			} else {
				return defaultMap;
			}
		} catch (final Exception e) {
			FrexLog.warn("Unable to load material map " + idForLog + " because of exception. Using default material map.", e);
			return defaultMap;
		}
	}

	public static <T extends StateHolder<?, ?>> void deserialize(List<T> states, ResourceLocation idForLog, InputStreamReader reader, IdentityHashMap<T, MaterialMap> map) {
		try {
			final JsonObject json = GsonHelper.parse(reader);
			final String idString = idForLog.toString();

			final MaterialMap globalDefaultMap = MaterialMapLoader.DEFAULT_MAP;
			@Nullable RenderMaterial defaultMaterial = null;
			MaterialMap defaultMap = globalDefaultMap;

			if (json.has("defaultMaterial")) {
				defaultMaterial = MaterialLoaderImpl.loadMaterial(json.get("defaultMaterial").getAsString(), defaultMaterial);
				defaultMap = new SingleMaterialMap(defaultMaterial);
			}

			if (json.has("defaultMap")) {
				defaultMap = loadMaterialMap(idString + "#default", json.getAsJsonObject("defaultMap"), defaultMap, defaultMaterial);
			}

			JsonObject variants = null;

			if (json.has("variants")) {
				variants = json.getAsJsonObject("variants");

				if (variants.isJsonNull()) {
					FrexLog.warn("Unable to load variant material maps for " + idString + " because the 'variants' block is empty. Using default map.");
					variants = null;
				}
			}

			for (final T state : states) {
				MaterialMap result = defaultMap;

				if (variants != null) {
					final String stateId = BlockModelShaper.statePropertiesToString(state.getValues());
					result = loadMaterialMap(idString + "#" + stateId, variants.getAsJsonObject(stateId), defaultMap, defaultMaterial);
				}

				if (result != globalDefaultMap) {
					map.put(state, result);
				}
			}
		} catch (final Exception e) {
			FrexLog.warn("Unable to load material map for " + idForLog.toString() + " due to unhandled exception:", e);
		}
	}
}
