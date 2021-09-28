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

package io.vram.frex.api.rendertype;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

import io.vram.frex.impl.material.VanillaShaderInfoImpl;

@NonExtendable
public interface VanillaShaderInfo {
	boolean fog();

	int cutout();

	boolean diffuse();

	boolean foil();

	VanillaShaderInfo MISSING = VanillaShaderInfoImpl.MISSING;

	/**
	 * Retrieves descriptive information about vanilla shaders. Mostly
	 * of use by renderer implementations and by FREX itself to translate
	 * RenderState instances into renderable materials
	 *
	 * <p>To use this method, your mod will need an access widener to reference
	 * the {@link ShaderStateShard} class. That class is not exposed here to prevent
	 * compilation or validation problems in dependent mods without visibility to it.
	 *
	 * @param shaderStateShard MUST be an instance of {@link ShaderStateShard}.
	 * @return VanillaShaderInfo instance describing the shader, or
	 *         {@link #MISSING} if not found.
	 */
	static VanillaShaderInfo get(Object shaderStateShard) {
		return VanillaShaderInfoImpl.get(shaderStateShard);
	}

	/**
	 * Serves the same purpose as {@link #get(Object)} but accepts the
	 * internal name of the shader instance.  Use this version when you do not have
	 * visibility to ShaderStateShard and/or have some way to obtain the shader
	 * instance name without it.
	 *
	 * @param shaderName  The name of the vanilla shader, corresponding to {@link ShaderInstance#name}.
	 * @return VanillaShaderInfo instance describing the shader, or
	 *         {@link #MISSING} if not found.
	 */
	static VanillaShaderInfo get(String shaderName) {
		return VanillaShaderInfoImpl.get(shaderName);
	}
}
