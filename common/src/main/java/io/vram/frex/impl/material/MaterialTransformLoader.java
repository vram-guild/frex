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

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.frex.api.config.FrexConfig;
import io.vram.frex.api.material.MaterialTransform;
import io.vram.frex.impl.FrexLog;

@Internal
public final class MaterialTransformLoader {
	private MaterialTransformLoader() { }

	private static final ObjectOpenHashSet<ResourceLocation> CAUGHT = new ObjectOpenHashSet<>();

	public static MaterialTransform loadTransform(String idForLog, String materialString, MaterialTransform defaultValue) {
		try {
			final MaterialTransform result = loadTransformInner(new ResourceLocation(materialString));
			return result == null ? defaultValue : result;
		} catch (final Exception e) {
			FrexLog.warn("Unable to load material transform " + materialString + " for material map " + idForLog + " because of exception. Using default transform.", e);
			return defaultValue;
		}
	}

	private static MaterialTransform loadTransformInner(ResourceLocation idIn) {
		final ResourceLocation id = new ResourceLocation(idIn.getNamespace(), "materials/" + idIn.getPath() + ".json");

		MaterialTransform result = null;
		final ResourceManager rm = Minecraft.getInstance().getResourceManager();

		try (final Resource res = rm.getResource(id)) {
			result = MaterialTransformDeserializer.deserialize(MaterialLoaderImpl.readJsonObject(res));
		} catch (final Exception e) {
			if (!FrexConfig.suppressMaterialLoadingSpam || CAUGHT.add(idIn)) {
				FrexLog.info("Unable to load material transform " + idIn.toString() + " due to exception " + e.toString());
			}
		}

		return result;
	}
}
