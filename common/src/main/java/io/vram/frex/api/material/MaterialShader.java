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

package io.vram.frex.api.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.renderer.Renderer;

public interface MaterialShader {
	ResourceLocation DEFAULT_VERTEX_SOURCE = new ResourceLocation("frex:shaders/material/default.vert");
	ResourceLocation DEFAULT_FRAGMENT_SOURCE = new ResourceLocation("frex:shaders/material/default.frag");

	int index();

	ResourceLocation vertexShaderId();

	default String vertexShader() {
		return vertexShaderId().toString();
	}

	ResourceLocation fragmentShaderId();

	default String fragmentShader() {
		return fragmentShaderId().toString();
	}

	static MaterialShader getOrCreate(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId) {
		return Renderer.get().shaders().getOrCreate(vertexSourceId, fragmentSourceId);
	}

	static MaterialShader getOrCreate(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId, @Nullable ResourceLocation depthVertexSourceId, @Nullable ResourceLocation depthFragmentSourceId) {
		return Renderer.get().shaders().getOrCreate(vertexSourceId, fragmentSourceId, depthVertexSourceId, depthFragmentSourceId);
	}

	static MaterialShader fromIndex(int shaderIndex) {
		return Renderer.get().shaders().shaderFromIndex(shaderIndex);
	}

	static MaterialShader defaultShader() {
		return Renderer.get().shaders().defaultShader();
	}
}
