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

package io.vram.frex.api.config;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.impl.config.ShaderConfigImpl;

/**
 * Use this to inject constant declarations that are managed by the mod into shaders .
 *
 * <p>The declarations are referenced by including a provided token in the shader(s) that need them.
 * They are provided by the mod as a string and consumed when needed by the renderer.
 *
 * <p>For example if you have a config option named {@code BOOP} with three choices: {@code BOOP_NEVER},
 * {@code BOOP_SOMETIMES} and {@code BOOP_ALWAYS} then you'd generate something like this:
 *
 * <p><pre>
 * #define BOOP_NEVER 0
 * #define BOOP_SOMETIMES 1
 * #define BOOP_ALWAYS 2
 *
 * // currently selected config
 * #define BOOP BOOP_ALWAYS
 * </pre>
 *
 * <p>Shader authors can reference this like so:
 *
 * <pre>
 * #include mymod:boop_config
 *
 * #if BOOP == BOOP_ALWAYS
 * // code that always boops goes here
 * #endif
 * </pre>
 *
 * <p>This is only suitable for configuration that can be represented as boolean or numeric values
 * via preprocessor declarations and that remain static until changed by the player.
 *
 * <p>The renderer implementation is responsible for detecting multiple/nested {code #include}
 * statements for the same token and for ignoring all duplicate occurrences.
 */
public interface ShaderConfig {
	/**
	 * Registers a supplier of configuration declarations that will replace the given
	 * token whenever it is encountered in shader source.
	 *
	 * <p>Will warn if the same token is registered twice and the last registration will be used.
	 */
	static void registerShaderConfigSupplier(ResourceLocation token, Supplier<String> supplier) {
		ShaderConfigImpl.registerShaderConfigSupplier(token, supplier);
	}

	/**
	 * Used by the renderer implementation - retrieves supplier of configuration declarations
	 * to replace the given token when it is encountered in shader source.
	 *
	 * <p>If the token is not registered, will return a default supplier that outputs
	 * a GLSL-friendly comment explaining it was not found.
	 */
	static Supplier<String> getShaderConfigSupplier(ResourceLocation token) {
		return ShaderConfigImpl.getShaderConfigSupplier(token);
	}

	/**
	 * Mods should invoke this after changing configuration that affects supplier output
	 * to force the renderer to recompile shaders.
	 */
	@SuppressWarnings("resource")
	static void invalidateShaderConfig() {
		Minecraft.getInstance().levelRenderer.allChanged();
	}
}
