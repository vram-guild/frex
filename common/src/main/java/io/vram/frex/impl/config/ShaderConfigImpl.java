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

package io.vram.frex.impl.config;

import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.impl.FrexLog;

@Internal
public class ShaderConfigImpl {
	private static final Object2ObjectOpenHashMap<ResourceLocation, Supplier<String>> MAP = new Object2ObjectOpenHashMap<>();

	public static Supplier<String> getShaderConfigSupplier(ResourceLocation token) {
		Preconditions.checkNotNull(token, "Encountered null shader config token. This is a bug in a mod.");

		return MAP.getOrDefault(token, () -> "// WARNING - INCLUDE TOKEN NOT FOUND: " + token.toString());
	}

	public static void registerShaderConfigSupplier(ResourceLocation token, Supplier<String> supplier) {
		Preconditions.checkNotNull(token, "Encountered null shader config token. This is a bug in a mod.");
		Preconditions.checkNotNull(supplier, "Encountered null shader config supplier. This is a bug in a mod.");

		if (MAP.put(token, supplier) != null) {
			FrexLog.warn("ShaderConfigSupplier for token " + token.toString() + " was registered more than once. The last registration will be used.");
		}
	}
}
