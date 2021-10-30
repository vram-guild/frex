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

package io.vram.frex.compat.fabric;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;

import io.vram.frex.api.material.MaterialCondition;
import io.vram.frex.api.renderer.Renderer;

import grondag.frex.api.material.MaterialFinder;
import grondag.frex.api.material.RenderMaterial;

@SuppressWarnings("deprecation")
public class FrexCompatibilityWrapper implements grondag.frex.api.Renderer {
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
		return wrapped.materials().registerMaterial(id, ((FabricMaterial) material).wrapped);
	}

	@Override
	public int maxSpriteDepth() {
		return 1;
	}

	@Override
	public MaterialCondition createCondition(BooleanSupplier supplier, boolean affectBlocks, boolean affectItems) {
		return wrapped.conditions().createCondition(supplier, affectBlocks, affectItems);
	}

	@Override
	public MaterialCondition conditionById(ResourceLocation id) {
		return wrapped.conditions().conditionFromId(id);
	}

	@Override
	public boolean registerCondition(ResourceLocation id, MaterialCondition pipeline) {
		return wrapped.conditions().registerCondition(id, pipeline);
	}
}
