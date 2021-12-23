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
