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

package io.vram.frex.impl.material.map;

import static io.vram.frex.impl.material.predicate.EntityBiPredicate.ENTITY_ALWAYS_TRUE;
import static io.vram.frex.impl.material.predicate.MaterialPredicate.MATERIAL_ALWAYS_TRUE;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.BiPredicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import io.vram.frex.api.config.FrexConfig;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.material.MaterialTransform;
import io.vram.frex.impl.FrexLog;
import io.vram.frex.impl.material.MaterialTransformLoader;
import io.vram.frex.impl.material.predicate.EntityBiPredicate;
import io.vram.frex.impl.material.predicate.EntityMaterialBoth;
import io.vram.frex.impl.material.predicate.EntityMaterialOnly;
import io.vram.frex.impl.material.predicate.EntityOnly;
import io.vram.frex.impl.material.predicate.MaterialPredicate;
import io.vram.frex.impl.material.predicate.MaterialPredicateDeserializer;

@Internal
public class EntityMaterialMapDeserializer {
	public static void deserialize(EntityType<?> entityType, ResourceLocation idForLog, List<Resource> orderedResourceList, IdentityHashMap<EntityType<?>, MaterialMap<Entity>> map) {
		try {
			final JsonObject[] reversedJsonList = new JsonObject[orderedResourceList.size()];
			final String[] packIds = new String[orderedResourceList.size()];
			final String idString = idForLog.toString();

			final MaterialMap<Entity> defaultMap = MaterialMap.identity();
			MaterialTransform defaultTransform = MaterialTransform.IDENTITY;
			MaterialMap<Entity> result = defaultMap;
			int j = 0;

			for (int i = orderedResourceList.size(); i-- > 0; ) {
				final InputStreamReader reader = new InputStreamReader(orderedResourceList.get(i).getInputStream(), StandardCharsets.UTF_8);
				final JsonObject json = GsonHelper.parse(reader);

				reversedJsonList[j] = json;
				packIds[j] = orderedResourceList.get(i).getSourceName();

				// Only read top-most resource for defaultMaterial
				if (json.has("defaultMaterial") && defaultTransform == MaterialTransform.IDENTITY) {
					defaultTransform = MaterialTransformLoader.loadTransform(formatLog(packIds[j], idString), json.get("defaultMaterial").getAsString(), defaultTransform);
					result = new SingleMaterialMap<>(ENTITY_ALWAYS_TRUE, defaultTransform);
				}

				j++;
			}

			result = loadEntityMaterialMap(idString, packIds, reversedJsonList, result, defaultTransform);

			if (result != defaultMap) {
				map.put(entityType, result);
			}
		} catch (final Exception e) {
			FrexLog.warn("Unable to load entity material map for " + idForLog.toString() + " due to unhandled exception:", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static MaterialMap<Entity> loadEntityMaterialMap(String idForLog, String[] packIdsForLog, JsonObject[] reversedJsonList, MaterialMap<Entity> defaultMap, MaterialTransform defaultTransform) {
		try {
			final ObjectArrayList<EntityBiPredicate> predicates = new ObjectArrayList<>();
			final ObjectArrayList<MaterialTransform> transforms = new ObjectArrayList<>();

			for (int jsonId = 0; jsonId < reversedJsonList.length; jsonId++) {
				final String packId = packIdsForLog[jsonId];
				final JsonObject json = reversedJsonList[jsonId];
				final JsonArray jsonArray = json.getAsJsonArray("map");

				if (jsonArray == null || jsonArray.isJsonNull() || jsonArray.size() == 0) {
					continue;
				}

				final int limit = jsonArray.size();

				for (int i = 0; i < limit; ++i) {
					final JsonObject obj = jsonArray.get(i).getAsJsonObject();

					if (!obj.has("predicate")) {
						FrexLog.warn("Unable to load entity material map " + formatLog(packId, idForLog) + " because 'predicate' tag is missing. Using default material.");
						continue;
					}

					if (!obj.has("material")) {
						FrexLog.warn("Unable to load entity material map " + formatLog(packId, idForLog) + " because 'material' tag is missing. Using default material.");
						continue;
					}

					final EntityBiPredicate predicate = loadPredicate(obj.get("predicate").getAsJsonObject());

					if (predicate == ENTITY_ALWAYS_TRUE) {
						FrexLog.warn("Empty material map predicate in " + formatLog(packId, idForLog) + " was skipped. This may indicate an invalid JSON.");
					} else {
						if (!predicates.contains(predicate)) {
							predicates.add(predicate);
							transforms.add(MaterialTransformLoader.loadTransform(formatLog(packId, idForLog), obj.get("material").getAsString(), defaultTransform));
						} else {
							if (FrexConfig.logMaterialPredicateDuplicates) {
								FrexLog.info("Found duplicate predicate in " + formatLog(packId, idForLog));
							}
						}
					}
				}
			}

			if (predicates.isEmpty()) {
				return defaultMap;
			} else {
				predicates.add(ENTITY_ALWAYS_TRUE);
				transforms.add(defaultTransform);
				final int n = predicates.size();
				return new MultiMaterialMap<>(predicates.toArray(new BiPredicate[n]), transforms.toArray(new MaterialTransform[n]));
			}
		} catch (final Exception e) {
			FrexLog.warn("Unable to load material map " + idForLog + " because of exception. Using default material map.", e);
			return defaultMap;
		}
	}

	private static EntityBiPredicate loadPredicate(JsonObject obj) {
		final EntityOnly entityPredicate = EntityOnly.fromJson(obj.get("entityPredicate"));
		final MaterialPredicate materialPredicate = MaterialPredicateDeserializer.deserialize(obj.get("materialPredicate").getAsJsonObject());

		if (entityPredicate == EntityOnly.ANY) {
			if (materialPredicate == MATERIAL_ALWAYS_TRUE) {
				return ENTITY_ALWAYS_TRUE;
			} else {
				return new EntityMaterialOnly(materialPredicate);
			}
		} else {
			if (materialPredicate == MATERIAL_ALWAYS_TRUE) {
				return entityPredicate;
			} else {
				return new EntityMaterialBoth(entityPredicate, materialPredicate);
			}
		}
	}

	private static String formatLog(String packId, String id) {
		return String.format("%s;%s", packId, id);
	}
}
