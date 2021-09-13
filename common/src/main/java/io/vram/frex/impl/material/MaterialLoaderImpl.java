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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import io.vram.frex.api.config.FrexFeature;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.renderer.Renderer;
import io.vram.frex.impl.FrexLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Internal
public final class MaterialLoaderImpl {
	private MaterialLoaderImpl() { }

	private static final ObjectOpenHashSet<Identifier> CAUGHT = new ObjectOpenHashSet<>();
	private static final Object2ObjectOpenHashMap<Identifier, RenderMaterial> LOAD_CACHE = new Object2ObjectOpenHashMap<>();

	private static final ObjectArrayList<Identifier> STANDARD_MATERIAL_IDS = new ObjectArrayList<>();

	static {
		STANDARD_MATERIAL_IDS.add(new Identifier ("fabric", "standard"));
		STANDARD_MATERIAL_IDS.add(RenderMaterial.MATERIAL_STANDARD);
	}

	/** Clear load cache and errors. Call at start of loading each pass. */
	public static void reset() {
		CAUGHT.clear();
		LOAD_CACHE.clear();
	}

	static RenderMaterial loadMaterial(String materialString, RenderMaterial defaultValue) {
		final Identifier id = new Identifier(materialString);

		RenderMaterial result = loadMaterialCached(id);

		if (result == null) {
			result = Renderer.get().materialById(id);
		}

		return result == null ? defaultValue : result;
	}

	public static RenderMaterial loadMaterial(Identifier id) {
		final RenderMaterial result = loadMaterialCached(id);
		// Check for materials registered via code
		return result == null ? Renderer.get().materialById(id) : result;
	}

	private static synchronized RenderMaterial loadMaterialCached(Identifier idIn) {
		final Renderer r = Renderer.get();

		RenderMaterial result = LOAD_CACHE.get(idIn);

		if (result == null) {
			result = loadMaterialInner(idIn);
			LOAD_CACHE.put(idIn, result);

			if (FrexFeature.isAvailable(FrexFeature.UPDATE_MATERIAL_REGISTRATION)) {
				r.registerOrUpdateMaterial(idIn, result);
			} else {
				r.registerMaterial(idIn, result);
			}
		}

		return result;
	}

	private static RenderMaterial loadMaterialInner(Identifier idIn) {
		// Don't try to load the standard material
		if (STANDARD_MATERIAL_IDS.contains(idIn)) {
			return null;
		}

		final Identifier id = new Identifier(idIn.getNamespace(), "materials/" + idIn.getPath() + ".json");

		RenderMaterial result = null;
		final ResourceManager rm = MinecraftClient.getInstance().getResourceManager();

		try (Resource res = rm.getResource(id)) {
			result = MaterialDeserializer.deserialize(readJsonObject(res));
		} catch (final Exception e) {
			// TODO:  make error suppression configurable
			if (CAUGHT.add(idIn)) {
				FrexLog.info("Unable to load render material " + idIn.toString() + " due to exception " + e.toString());
			}
		}

		return result;
	}

	static Sprite loadSprite(String idForLog, String spriteId, SpriteAtlasTexture atlas, Sprite missingSprite) {
		final Sprite sprite = atlas.getSprite(new Identifier(spriteId));

		if (sprite == null || sprite == missingSprite) {
			FrexLog.warn("Unable to find sprite " + spriteId + " for material map " + idForLog + ". Using default material.");
			return null;
		}

		return sprite;
	}

	public static JsonObject readJsonObject(Resource res) {
		InputStream stream = null;
		InputStreamReader reader = null;
		JsonObject result = null;

		try {
			stream = res.getInputStream();
			reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
			result = JsonHelper.deserialize(reader);
		} catch (final Exception e) {
			throw new RuntimeException("Unexpected error during material deserialization", e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}

				if (stream != null) {
					stream.close();
				}
			} catch (final Exception e) {
				FrexLog.warn("Unexpected error during material deserialization", e);
			}
		}

		return result;
	}
}
