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

package io.vram.frex.api.mesh;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.material.RenderMaterial;

/**
 * Similar in purpose to {@link BufferBuilder} but simpler
 * and not tied to NIO or any other specific implementation,
 * plus designed to handle both static and dynamic building.
 *
 * <p>Decouples models from the vertex format(s) used by
 * ModelRenderer to allow compatibility across diverse implementations.
 */
public interface MeshBuilder {
	/**
	 * Returns the {@link QuadEmitter} used to append quad to this mesh.
	 * Calling this method a second time invalidates any prior result.
	 * Do not retain references outside the context of building the mesh.
	 */
	QuadEmitter getEmitter();

	/**
	 * Returns a new {@link Mesh} instance containing all
	 * quads added to this builder and resets the builder to an empty state.
	 */
	Mesh build();

	default MeshBuilder box(
		RenderMaterial material,
		int color, TextureAtlasSprite sprite,
		float minX, float minY, float minZ,
		float maxX, float maxY, float maxZ
	) {
		getEmitter()
			.material(material)
			.square(Direction.UP, minX, minZ, maxX, maxZ, 1-maxY)
			.vertexColor(color, color, color, color)
			.uvUnitSquare()
			.spriteBake(sprite, QuadEmitter.BAKE_NORMALIZED)
			.emit()

			.material(material)
			.square(Direction.DOWN, minX, minZ, maxX, maxZ, minY)
			.vertexColor(color, color, color, color)
			.uvUnitSquare()
			.spriteBake(sprite, QuadEmitter.BAKE_NORMALIZED)
			.emit()

			.material(material)
			.square(Direction.EAST, minZ, minY, maxZ, maxY, 1-maxX)
			.vertexColor(color, color, color, color)
			.uvUnitSquare()
			.spriteBake(sprite, QuadEmitter.BAKE_NORMALIZED)
			.emit()

			.material(material)
			.square(Direction.WEST, minZ, minY, maxZ, maxY, minX)
			.vertexColor(color, color, color, color)
			.uvUnitSquare()
			.spriteBake(sprite, QuadEmitter.BAKE_NORMALIZED)
			.emit()

			.material(material)
			.square(Direction.SOUTH, minX, minY, maxX, maxY, 1-maxZ)
			.vertexColor(color, color, color, color)
			.uvUnitSquare()
			.spriteBake(sprite, QuadEmitter.BAKE_NORMALIZED)
			.emit()

			.material(material)
			.square(Direction.NORTH, minX, minY, maxX, maxY, minZ)
			.vertexColor(color, color, color, color)
			.uvUnitSquare()
			.spriteBake(sprite, QuadEmitter.BAKE_NORMALIZED)
			.emit();

		return this;
	}
}
