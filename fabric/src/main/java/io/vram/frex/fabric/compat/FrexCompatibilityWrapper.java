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

package io.vram.frex.fabric.compat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;

import io.vram.frex.api.renderer.Renderer;

public class FrexCompatibilityWrapper implements net.fabricmc.fabric.api.renderer.v1.Renderer {
	public static FrexCompatibilityWrapper of(Renderer wrapped) {
		return new FrexCompatibilityWrapper(wrapped);
	}

	final Renderer wrapped;

	protected FrexCompatibilityWrapper(Renderer wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public MeshBuilder meshBuilder() {
		return FabricMeshBuilder.of(wrapped.meshBuilder());
	}

	@Override
	public MaterialFinder materialFinder() {
		return FabricMaterialFinder.of(wrapped.materials().materialFinder());
	}

	@Override
	public @Nullable RenderMaterial materialById(ResourceLocation id) {
		if (id != null && id.equals(RenderMaterial.MATERIAL_STANDARD)) {
			return FabricMaterial.of(wrapped.materials().defaultMaterial());
		} else {
			return FabricMaterial.of(wrapped.materials().materialFromId(id));
		}
	}

	@Override
	public boolean registerMaterial(ResourceLocation id, net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material) {
		if (material == null) {
			material = FabricMaterial.of(wrapped.materials().defaultMaterial());
		}

		return wrapped.materials().registerMaterial(id, ((FabricMaterial) material).wrapped);
	}
}
