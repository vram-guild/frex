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

package io.vram.frex.base.renderer;

import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.renderer.ConditionManager;
import io.vram.frex.api.renderer.MaterialShaderManager;
import io.vram.frex.api.renderer.MaterialTextureManager;
import io.vram.frex.api.renderer.Renderer;
import io.vram.frex.base.renderer.material.BaseMaterialManager;
import io.vram.frex.base.renderer.material.BaseMaterialManager.MaterialFactory;
import io.vram.frex.base.renderer.material.BaseMaterialView;
import io.vram.frex.base.renderer.material.BaseTextureManager;
import io.vram.frex.base.renderer.mesh.BaseMeshBuilder;

public abstract class BaseRenderer<M extends BaseMaterialView & RenderMaterial> implements Renderer {
	protected final BaseTextureManager textureManager = new BaseTextureManager();
	protected final BaseConditionManager conditionManager = new BaseConditionManager();
	protected final BaseMaterialShaderManager shaderManager = new BaseMaterialShaderManager();
	protected final BaseMaterialManager<M> materialManager;

	public BaseRenderer(MaterialFactory<M> factory) {
		materialManager = createMaterialManager(conditionManager, textureManager, shaderManager, factory);
	}

	protected BaseMaterialManager<M> createMaterialManager(ConditionManager conditions, MaterialTextureManager textures, MaterialShaderManager shaders, MaterialFactory<M> factory) {
		return new BaseMaterialManager<>(conditionManager, textureManager, shaderManager, factory);
	}

	@Override
	public BaseMeshBuilder meshBuilder() {
		return new BaseMeshBuilder();
	}

	@Override
	public BaseTextureManager textures() {
		return textureManager;
	}

	@Override
	public BaseMaterialManager<M> materials() {
		return materialManager;
	}

	@Override
	public BaseConditionManager conditions() {
		return conditionManager;
	}

	@Override
	public BaseMaterialShaderManager shaders() {
		return shaderManager;
	}
}
