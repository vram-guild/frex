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

package io.vram.frex.compat.fabric;

import java.util.function.BooleanSupplier;

import io.vram.frex.api.material.MaterialCondition;
import io.vram.frex.api.renderer.Renderer;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;

import grondag.frex.api.material.MaterialFinder;
import grondag.frex.api.material.RenderMaterial;

public class FabricRenderer implements grondag.frex.api.Renderer {
	public static FabricRenderer of(Renderer wrapped) {
		return new FabricRenderer(wrapped);
	}

	final Renderer wrapped;

	protected FabricRenderer(Renderer wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public MeshBuilder meshBuilder() {
		return FabricMeshBuilder.of(wrapped.meshBuilder());
	}

	@Override
	public MaterialFinder materialFinder() {
		return FabricMaterialFinder.of(wrapped.materialFinder());
	}

	@Override
	public @Nullable RenderMaterial materialById(Identifier id) {
		return FabricMaterial.of(wrapped.materialById(id));
	}

	@Override
	public boolean registerMaterial(Identifier id, net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material) {
		return wrapped.registerMaterial(id, ((FabricMaterial) material).wrapped);
	}

	@Override
	public int maxSpriteDepth() {
		return 1;
	}

	@Override
	public MaterialCondition createCondition(BooleanSupplier supplier, boolean affectBlocks, boolean affectItems) {
		return wrapped.createCondition(supplier, affectBlocks, affectItems);
	}

	@Override
	public MaterialCondition conditionById(Identifier id) {
		return wrapped.conditionById(id);
	}

	@Override
	public boolean registerCondition(Identifier id, MaterialCondition pipeline) {
		return wrapped.registerCondition(id, pipeline);
	}
}
