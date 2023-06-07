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

package io.vram.frex.fabric.compat;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;

import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.material.RenderMaterial;

public class FabricQuadEmitter extends FabricQuadView<QuadEmitter> implements net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView, net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter {
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
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter material(net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material) {
		if (material == null) {
			material = FabricMaterial.of(io.vram.frex.api.material.RenderMaterial.defaultMaterial());
		}

		wrapped.material(((FabricMaterial) material).wrapped);
		return this;
	}

	@Override
	public @Nullable net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter cullFace(@Nullable Direction face) {
		wrapped.cullFace(face);
		return this;
	}

	@Override
	public @Nullable net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter nominalFace(Direction face) {
		wrapped.nominalFace(face);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter colorIndex(int colorIndex) {
		wrapped.colorIndex(colorIndex);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter fromVanilla(int[] quadData, int startIndex) {
		wrapped.fromVanilla(quadData, startIndex);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter fromVanilla(BakedQuad quad, net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial material, Direction cullFace) {
		if (material == null) {
			material = FabricMaterial.of(RenderMaterial.defaultMaterial());
		}

		wrapped.fromVanilla(quad, ((FabricMaterial) material).wrapped, cullFace);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter tag(int tag) {
		wrapped.tag(tag);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter copyFrom(QuadView quad) {
		((FabricQuadView<?>) quad).wrapped.copyTo(wrapped);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter pos(int vertexIndex, float x, float y, float z) {
		wrapped.pos(vertexIndex, x, y, z);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter normal(int vertexIndex, float x, float y, float z) {
		wrapped.normal(vertexIndex, x, y, z);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter lightmap(int vertexIndex, int lightmap) {
		wrapped.lightmap(vertexIndex, lightmap);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter color(int vertexIndex, int color) {
		wrapped.vertexColor(vertexIndex, color);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter uv(int vertexIndex, float u, float v) {
		wrapped.uv(vertexIndex, u, v);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter spriteBake(TextureAtlasSprite sprite, int bakeFlags) {
		wrapped.spriteBake(sprite, bakeFlags);
		return this;
	}

	@Override
	public net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter emit() {
		wrapped.emit();
		return this;
	}
}
