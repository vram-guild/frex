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

import java.io.InputStreamReader;
import java.util.IdentityHashMap;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.impl.FrexLog;

@Internal
public class ParticleMaterialMapDeserializer {
	public static void deserialize(ParticleType<?> particleType, ResourceLocation idForLog, InputStreamReader reader, IdentityHashMap<ParticleType<?>, MaterialMap> map) {
		try {
			final JsonObject json = GsonHelper.parse(reader);

			if (json.has("material")) {
				final MaterialMap result = new SingleMaterialMap(MaterialLoaderImpl.loadMaterial(json.get("material").getAsString(), null));
				map.put(particleType, result);
			}
		} catch (final Exception e) {
			FrexLog.warn("Unable to load particle material map for " + idForLog.toString() + " due to unhandled exception:", e);
		}
	}
}
