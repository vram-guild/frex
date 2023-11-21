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

package io.vram.frex.api.texture;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.mesh.QuadView;
import io.vram.frex.impl.texture.SpriteFinderImpl;

/**
 * Indexes a texture atlas to allow fast lookup of Sprites from
 * baked vertex coordinates.  Main use is for {@link Mesh}-based models
 * to generate vanilla quads on demand without tracking and retaining
 * the sprites that were baked into the mesh. In other words, this class
 * supplies the sprite parameter for {@link QuadView#toBakedQuad(int, TextureAtlasSprite, boolean)}.
 */
@FunctionalInterface
public interface SpriteFinder {
	/**
	 * Retrieves or creates the finder for the given atlas.
	 * Instances should not be retained as fields or they must be
	 * refreshed whenever there is a resource reload or other event
	 * that causes atlas textures to be re-stitched.
	 */
	static SpriteFinder get(TextureAtlas atlas) {
		return SpriteFinderImpl.get(atlas);
	}

	/**
	 * Finds the atlas sprite containing the vertex centroid of the quad.
	 * Vertex centroid is essentially the mean u,v coordinate - the intent being
	 * to find a point that is unambiguously inside the sprite (vs on an edge.)
	 *
	 * <p>Should be reliable for any convex quad or triangle. May fail for non-convex quads.
	 * Note that all the above refers to u,v coordinates. Geometric vertex does not matter,
	 * except to the extent it was used to determine u,v.
	 */
	default TextureAtlasSprite find(QuadView quad) {
		final float u = quad.u(0) + quad.u(1) + quad.u(2) + quad.u(3);
		final float v = quad.v(0) + quad.v(1) + quad.v(2) + quad.v(3);
		return find(u * 0.25f, v * 0.25f);
	}

	/**
	 * Alternative to {@link #find(QuadView)} when vertex centroid is already
	 * known or unsuitable.  Expects normalized (0-1) coordinates on the atlas texture,
	 * which should already be the case for u,v values in vanilla baked quads and in
	 * {@link QuadView} after calling {@link QuadEmitter#spriteBake(int, TextureAtlasSprite, int)}.
	 *
	 * <p>Coordinates must be in the sprite interior for reliable results. Generally will
	 * be easier to use {@link #find(QuadView, int)} unless you know the vertex
	 * centroid will somehow not be in the quad interior. This method will be slightly
	 * faster if you already have the centroid or another appropriate value.
	 */
	TextureAtlasSprite find(float u, float v);
}
