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

import com.google.gson.JsonObject;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.impl.FrexLog;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

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
