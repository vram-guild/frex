/*
 * Copyright © Original Authors
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

package io.vram.frex.api.material;

import org.jetbrains.annotations.ApiStatus.Experimental;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.impl.material.MaterialLoaderImpl;

/**
 * For use by model loading libraries - handles deserialization of material JSON
 * files, creating and registering them in the renderer.  Note that resource reload
 * events do not cause materials to be reloaded, but shader source will be refreshed
 * if the renderer supports that feature.
 *
 * <p>Loaded materials will be Fabric API materials if no FREX compliant renderer is present
 * and the materials will (obviously) not have shaders in that case.
 *
 * <p>Renderer Authors: This interface is implemented by FREX - you do not need to implement it.
 */
@Experimental
public interface MaterialLoader {
	/**
	 * Material files should be in {@code assets/<mod-id>/materials} and have a {@code .json} suffix.
	 *
	 * @param id domain and path of material json. See notes above.
	 * @return  Loaded material if successful, null if file not found or specified features are unsupported.
	 */
	static RenderMaterial getOrLoadMaterial(ResourceLocation id) {
		return MaterialLoaderImpl.loadMaterial(id);
	}

	/**
	 * @deprecated Use the better-named {@link #getOrLoadMaterial(ResourceLocation)}
	 */
	@Deprecated
	static RenderMaterial loadMaterial(ResourceLocation id) {
		return MaterialLoaderImpl.loadMaterial(id);
	}
}
