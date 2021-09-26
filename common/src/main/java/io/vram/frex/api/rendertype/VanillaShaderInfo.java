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

package io.vram.frex.api.rendertype;

import io.vram.frex.impl.material.VanillaShaderInfoImpl;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

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
