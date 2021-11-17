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

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialShader;

public class BaseMaterialShader implements MaterialShader {
	public final int index;
	public final ResourceLocation vertexId;
	public final String vertexIdString;
	public final int vertexIndex;
	public final ResourceLocation fragmentId;
	public final String fragmentIdString;
	public final int fragmentIndex;
	public final ResourceLocation depthVertexId;
	public final int depthVertexIndex;
	public final ResourceLocation depthFragmentId;
	public final int depthFragmentIndex;

	public BaseMaterialShader(BaseMaterialShaderManager manager, int index, int vertexIndex, int fragmentIndex, int depthVertexIndex, int depthFragmentIndex) {
		this.index = index;
		this.vertexIndex = vertexIndex;
		this.fragmentIndex = fragmentIndex;
		this.depthVertexIndex = depthVertexIndex;
		this.depthFragmentIndex = depthFragmentIndex;
		vertexId = manager.vertexIndexer.fromIndex(vertexIndex);
		vertexIdString = vertexId.toString();
		fragmentId = manager.fragmentIndexer.fromIndex(fragmentIndex);
		fragmentIdString = fragmentId.toString();
		depthVertexId = manager.vertexIndexer.fromIndex(depthVertexIndex);
		depthFragmentId = manager.fragmentIndexer.fromIndex(depthFragmentIndex);
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public ResourceLocation vertexShaderId() {
		return vertexId;
	}

	@Override
	public String vertexShader() {
		return vertexIdString;
	}

	@Override
	public ResourceLocation fragmentShaderId() {
		return fragmentId;
	}

	@Override
	public String fragmentShader() {
		return fragmentIdString;
	}
}
