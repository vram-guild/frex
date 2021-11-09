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

package io.vram.frex.impl.material;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import io.vram.frex.api.config.FrexConfig;
import io.vram.frex.api.config.FrexFeature;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.impl.FrexLog;

@Internal
public final class MaterialLoaderImpl {
	private MaterialLoaderImpl() { }

	private static final ObjectOpenHashSet<ResourceLocation> CAUGHT = new ObjectOpenHashSet<>();
	private static final Object2ObjectOpenHashMap<ResourceLocation, RenderMaterial> LOAD_CACHE = new Object2ObjectOpenHashMap<>();

	private static final ObjectArrayList<ResourceLocation> STANDARD_MATERIAL_IDS = new ObjectArrayList<>();

	static {
		STANDARD_MATERIAL_IDS.add(new ResourceLocation ("fabric", "standard"));
		STANDARD_MATERIAL_IDS.add(RenderMaterial.STANDARD_MATERIAL_KEY);
	}

	/** Clear load cache and errors. Call at start of loading each pass. */
	public static void reset() {
		CAUGHT.clear();
		LOAD_CACHE.clear();
	}

	static RenderMaterial loadMaterial(String materialString, RenderMaterial defaultValue) {
		final ResourceLocation id = new ResourceLocation(materialString);

		RenderMaterial result = loadMaterialCached(id);

		if (result == null) {
			result = RenderMaterial.fromId(id);
		}

		return result == null ? defaultValue : result;
	}

	public static RenderMaterial loadMaterial(ResourceLocation id) {
		final RenderMaterial result = loadMaterialCached(id);
		// Check for materials registered via code
		return result == null ? RenderMaterial.fromId(id) : result;
	}

	private static synchronized RenderMaterial loadMaterialCached(ResourceLocation idIn) {
		RenderMaterial result = LOAD_CACHE.get(idIn);

		if (result == null) {
			result = loadMaterialInner(idIn);

			LOAD_CACHE.put(idIn, result);

			if (FrexFeature.isAvailable(FrexFeature.UPDATE_MATERIAL_REGISTRATION)) {
				result.registerOrUpdateWithId(idIn);
			} else {
				result.registerWithId(idIn);
			}
		}

		return result;
	}

	private static RenderMaterial loadMaterialInner(ResourceLocation idIn) {
		// Don't try to load the standard material
		if (STANDARD_MATERIAL_IDS.contains(idIn)) {
			return RenderMaterial.defaultMaterial();
		}

		final ResourceLocation id = new ResourceLocation(idIn.getNamespace(), "materials/" + idIn.getPath() + ".json");

		RenderMaterial result = null;
		final ResourceManager rm = Minecraft.getInstance().getResourceManager();

		try (Resource res = rm.getResource(id)) {
			result = MaterialDeserializer.deserialize(readJsonObject(res));
		} catch (final Exception e) {
			if (!FrexConfig.suppressMaterialLoadingSpam || CAUGHT.add(idIn)) {
				FrexLog.info("Unable to load render material " + idIn.toString() + " due to exception " + e.toString() + ". Using default material.");
			}

			return RenderMaterial.defaultMaterial();
		}

		return result;
	}

	static TextureAtlasSprite loadSprite(String idForLog, String spriteId, TextureAtlas atlas, TextureAtlasSprite missingSprite) {
		final TextureAtlasSprite sprite = atlas.getSprite(new ResourceLocation(spriteId));

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
			result = GsonHelper.parse(reader);
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
