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

public interface RenderMaterial extends MaterialView {
	ResourceLocation STANDARD_MATERIAL_KEY = new ResourceLocation("frex", "standard");
	ResourceLocation MISSING_MATERIAL_KEY = new ResourceLocation("frex", "missing");

	int index();

	default boolean isDefault() {
		return this == defaultMaterial();
	}

	default boolean isMissing() {
		return this == missingMaterial();
	}

	default boolean registerWithId(ResourceLocation id) {
		return Renderer.get().materials().registerMaterial(id, this);
	}

	default boolean registerOrUpdateWithId(ResourceLocation id) {
		return Renderer.get().materials().registerOrUpdateMaterial(id, this);
	}

	default RenderMaterial withOverlay(int u, int v) {
		return MaterialFinder.threadLocal().copyFrom(this).overlay(u, v).find();
	}

	default RenderMaterial withOverlay(int uv) {
		return MaterialFinder.threadLocal().copyFrom(this).overlay(uv).find();
	}

	static @Nullable RenderMaterial fromId(ResourceLocation id) {
		return Renderer.get().materials().materialFromId(id);
	}

	static RenderMaterial fromIndex(int index) {
		return Renderer.get().materials().materialFromIndex(index);
	}

	static RenderMaterial defaultMaterial() {
		return Renderer.get().materials().defaultMaterial();
	}

	static RenderMaterial missingMaterial() {
		return Renderer.get().materials().missingMaterial();
	}
}
