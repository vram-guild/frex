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

package io.vram.frex.api.mesh;

import org.jetbrains.annotations.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import io.vram.frex.api.material.RenderMaterial;

public interface QuadEditor extends QuadView {
	/**
	 * Causes texture to appear with no rotation.
	 * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
	 */
	int BAKE_ROTATE_NONE = 0;

	/**
	 * Causes texture to appear rotated 90 deg. relative to nominal face.
	 * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
	 */
	int BAKE_ROTATE_90 = 1;

	/**
	 * Causes texture to appear rotated 180 deg. relative to nominal face.
	 * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
	 */
	int BAKE_ROTATE_180 = 2;

	/**
	 * Causes texture to appear rotated 270 deg. relative to nominal face.
	 * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
	 */
	int BAKE_ROTATE_270 = 3;

	/**
	 * When enabled, texture coordinate are assigned based on vertex position.
	 * Any existing uv coordinates will be replaced.
	 * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
	 *
	 * <p>UV lock always derives texture coordinates based on nominal face, even
	 * when the quad is not co-planar with that face, and the result is
	 * the same as if the quad were projected onto the nominal face, which
	 * is usually the desired result.
	 */
	int BAKE_LOCK_UV = 4;

	/**
	 * When set, U texture coordinates for the given sprite are
	 * flipped as part of baking. Can be useful for some randomization
	 * and texture mapping scenarios. Results are different than what
	 * can be obtained via rotation and both can be applied.
	 * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
	 */
	int BAKE_FLIP_U = 8;

	/**
	 * Same as {@link QuadEditor#BAKE_FLIP_U} but for V coordinate.
	 */
	int BAKE_FLIP_V = 16;

	/**
	 * UV coordinates by default are assumed to be 0-16 scale for consistency
	 * with conventional Minecraft model format. This is scaled to 0-1 during
	 * baking before interpolation. Model loaders that already have 0-1 coordinates
	 * can avoid wasteful multiplication/division by passing 0-1 coordinates directly.
	 * Pass in bakeFlags parameter to {@link #spriteBake(int, TextureAtlasSprite, int)}.
	 */
	int BAKE_NORMALIZED = 32;

	QuadEditor material(RenderMaterial material);

	@Nullable
	QuadEditor cullFace(@Nullable Direction face);

	@Nullable
	QuadEditor nominalFace(Direction face);

	QuadEditor colorIndex(int colorIndex);

	QuadEditor fromVanilla(int[] quadData, int startIndex);

	QuadEditor fromVanilla(BakedQuad quad, RenderMaterial material, Direction cullFace);

	QuadEditor tag(int tag);

	QuadEditor pos(int vertexIndex, float x, float y, float z);

	default QuadEditor pos(int vertexIndex, Vector3f vec) {
		return pos(vertexIndex, vec.x(), vec.y(), vec.z());
	}

	QuadEditor normal(int vertexIndex, float x, float y, float z);

	default QuadEditor normal(int vertexIndex, Vector3f vec) {
		return normal(vertexIndex, vec.x(), vec.y(), vec.z());
	}

	default QuadEditor tangent(int vertexIndex, Vector3f vec) {
		tangent(vertexIndex, vec.x(), vec.y(), vec.z());
		return this;
	}

	QuadEditor tangent(int vertexIndex, float x, float y, float z);

	QuadEditor lightmap(int vertexIndex, int lightmap);

	default QuadEditor lightmap(int b0, int b1, int b2, int b3) {
		lightmap(0, b0);
		lightmap(1, b1);
		lightmap(2, b2);
		lightmap(3, b3);
		return this;
	}

	/**
	 * Set color for given vertex.
	 */
	QuadEditor vertexColor(int vertexIndex, int color);

	/**
	 * Convenience: set color for all vertices at once.
	 */
	default QuadEditor vertexColor(int c0, int c1, int c2, int c3) {
		vertexColor(0, c0);
		vertexColor(1, c1);
		vertexColor(2, c2);
		vertexColor(3, c3);
		return this;
	}

	/**
	 * Set texture coordinates relative to the "raw" texture.
	 * For sprite atlas textures, the coordinates must be
	 * relative to the atlas texture, not relative to the sprite.
	 */
	QuadEditor uv(int vertexIndex, float u, float v);

	/**
	 * Sets texture coordinates relative to the given texture sprite.
	 * For sprite atlas textures, this may be more convenient than
	 * interpolating the coordinates directly.  Additionally, this method
	 * will often be more performant for renderers that need to track
	 * sprite identity within meshes or that use sprite-relative coordinates
	 * in shaders.
	 *
	 * <p>Note that the material for this quad must be associated with a
	 * sprite atlas texture, and the sprite must belong to that atlas texture.
	 * If this condition is unmet, or if the sprite is null, will operate
	 * as if {@link #uv(int, float, float)} had been called for all four vertices.
	 */
	QuadEditor uvSprite(@Nullable TextureAtlasSprite sprite, float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3);

	/**
	 * Assigns sprite atlas u,v coordinates to this quad for the given sprite.
	 * Can handle UV locking, rotation, interpolation, etc. Control this behavior
	 * by passing additive combinations of the BAKE_ flags defined in this interface.
	 * Behavior for {@code spriteIndex > 0} is currently undefined.
	 */
	QuadEditor spriteBake(TextureAtlasSprite sprite, int bakeFlags);

	/**
	 * Tolerance for determining if the depth parameter to {@link #square(Direction, float, float, float, float, float)}
	 * is effectively zero - meaning the face is a cull face.
	 */
	float CULL_FACE_EPSILON = 0.00001f;

	/**
	 * Helper method to assign vertex coordinates for a square aligned with the given face.
	 * Ensures that vertex order is consistent with vanilla convention. (Incorrect order can
	 * lead to bad AO lighting unless enhanced lighting logic is available/enabled.)
	 *
	 * <p>Square will be parallel to the given face and coplanar with the face (and culled if the
	 * face is occluded) if the depth parameter is approximately zero. See {@link #CULL_FACE_EPSILON}.
	 *
	 * <p>All coordinates should be normalized (0-1).
	 */
	default QuadEditor square(Direction nominalFace, float left, float bottom, float right, float top, float depth) {
		if (Math.abs(depth) < CULL_FACE_EPSILON) {
			cullFace(nominalFace);
			depth = 0; // avoid any inconsistency for face quads
		} else {
			cullFace(null);
		}

		nominalFace(nominalFace);
		switch (nominalFace) {
			case UP:
				depth = 1 - depth;
				top = 1 - top;
				bottom = 1 - bottom;

			case DOWN:
				pos(0, left, depth, top);
				pos(1, left, depth, bottom);
				pos(2, right, depth, bottom);
				pos(3, right, depth, top);
				break;

			case EAST:
				depth = 1 - depth;
				left = 1 - left;
				right = 1 - right;

			case WEST:
				pos(0, depth, top, left);
				pos(1, depth, bottom, left);
				pos(2, depth, bottom, right);
				pos(3, depth, top, right);
				break;

			case SOUTH:
				depth = 1 - depth;
				left = 1 - left;
				right = 1 - right;

			case NORTH:
				pos(0, 1 - left, top, depth);
				pos(1, 1 - left, bottom, depth);
				pos(2, 1 - right, bottom, depth);
				pos(3, 1 - right, top, depth);
				break;
		}

		return this;
	}

	QuadEditor emit();
}
