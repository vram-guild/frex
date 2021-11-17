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

package io.vram.frex.compat.fabric;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.material.RenderMaterial;

@SuppressWarnings("deprecation")
public class FabricQuadEmitter extends FabricQuadView<QuadEmitter> implements grondag.frex.api.mesh.MutableQuadView, grondag.frex.api.mesh.QuadEmitter {
	public static FabricQuadEmitter of(QuadEmitter wrapped) {
		return new FabricQuadEmitter(wrapped);
	}

	protected FabricQuadEmitter(QuadEmitter wrapped) {
		super(wrapped);
	}

	@Override
	public FabricQuadEmitter wrap(QuadEmitter wrapped) {
		this.wrapped = wrapped;
		return this;
	}

	@Override
	public grondag.frex.api.mesh.QuadEmitter material(net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material) {
		if (material == null) {
			material = FabricMaterial.of(io.vram.frex.api.material.RenderMaterial.defaultMaterial());
		}

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
		if (material == null) {
			material = FabricMaterial.of(RenderMaterial.defaultMaterial());
		}

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
	public grondag.frex.api.mesh.QuadEmitter emit() {
		wrapped.emit();
		return this;
	}
}
