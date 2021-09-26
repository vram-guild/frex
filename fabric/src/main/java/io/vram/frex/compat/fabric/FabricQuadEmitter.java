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

import io.vram.frex.api.mesh.QuadEditor;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import grondag.frex.api.mesh.QuadEmitter;

@SuppressWarnings("deprecation")
public class FabricQuadEmitter extends FabricQuadView<QuadEditor> implements grondag.frex.api.mesh.MutableQuadView, grondag.frex.api.mesh.QuadEmitter {
	public static FabricQuadEmitter of(QuadEditor wrapped) {
		return new FabricQuadEmitter(wrapped);
	}

	protected FabricQuadEmitter(QuadEditor wrapped) {
		super(wrapped);
	}

	@Override
	public FabricQuadEmitter wrap(QuadEditor wrapped) {
		this.wrapped = wrapped;
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter material(net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material) {
		wrapped.material(((FabricMaterial) material).wrapped);
		return this;
	}

	@Override
	public @Nullable grondag.frex.api.mesh.QuadEmitter cullFace(@Nullable Direction face) {
		wrapped.cullFace(face);
		return this;
	}

	@Override
	public @Nullable grondag.frex.api.mesh.QuadEmitter nominalFace(Direction face) {
		wrapped.nominalFace(face);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter colorIndex(int colorIndex) {
		wrapped.colorIndex(colorIndex);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter fromVanilla(int[] quadData, int startIndex, boolean isItem) {
		wrapped.fromVanilla(quadData, startIndex);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter fromVanilla(BakedQuad quad, net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material, Direction cullFace) {
		wrapped.fromVanilla(quad, ((FabricMaterial) material).wrapped, cullFace);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter tag(int tag) {
		wrapped.tag(tag);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter pos(int vertexIndex, float x, float y, float z) {
		wrapped.pos(vertexIndex, x, y, z);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter normal(int vertexIndex, float x, float y, float z) {
		wrapped.normal(vertexIndex, x, y, z);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter lightmap(int vertexIndex, int lightmap) {
		wrapped.lightmap(vertexIndex, lightmap);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter spriteColor(int vertexIndex, int spriteIndex, int color) {
		wrapped.vertexColor(vertexIndex, color);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter sprite(int vertexIndex, int spriteIndex, float u, float v) {
		wrapped.uv(vertexIndex, u, v);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter spriteBake(int spriteIndex, TextureAtlasSprite sprite, int bakeFlags) {
		wrapped.spriteBake(sprite, bakeFlags);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter tangent(int vertexIndex, float x, float y, float z) {
		wrapped.tangent(vertexIndex, x, y, z);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter vertexColor(int vertexIndex, int color) {
		wrapped.vertexColor(vertexIndex, color);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter sprite(int vertexIndex, float u, float v) {
		wrapped.uv(vertexIndex, u, v);
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter spriteBake(TextureAtlasSprite sprite, int bakeFlags) {
		wrapped.spriteBake(sprite, bakeFlags);
		return this;
	}

	@Override
	public QuadEmitter emit() {
		wrapped.emit();
		return this;
	}
}
