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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import io.vram.frex.api.material.RenderMaterial;

public interface QuadView {
	/** Count of integers in a conventional (un-modded) block or item vertex. */
	int VANILLA_VERTEX_STRIDE = DefaultVertexFormat.BLOCK.getIntegerSize();

	/** Count of integers in a conventional (un-modded) block or item quad. */
	int VANILLA_QUAD_STRIDE = VANILLA_VERTEX_STRIDE * 4;

	/**
	 * Extracts all quad properties except material to the given {@link QuadEditor} instance.
	 * Must be used before calling {link QuadEmitter#emit()} on the target instance.
	 * Meant for re-texturing, analysis and static transformation use cases.
	 */
	void copyTo(QuadEditor target);

	/**
	 * Retrieves the quad color index serialized with the quad.
	 */
	int colorIndex();

	/**
	 * Equivalent to {@link BakedQuad#getDirection()}. This is the face used for vanilla lighting
	 * calculations and will be the block face to which the quad is most closely aligned. Always
	 * the same as cull face for quads that are on a block face, but never null.
	 */
	@NotNull
	Direction lightFace();

	/**
	 * If non-null, quad should not be rendered in-world if the
	 * opposite face of a neighbor block occludes it.
	 *
	 * @see QuadEditor#cullFace(Direction)
	 */
	@Nullable Direction cullFace();

	/**
	 * See {@link QuadEditor#nominalFace(Direction)}.
	 */
	Direction nominalFace();

	/**
	 * Normal of the quad as implied by geometry. Will be invalid
	 * if quad vertices are not co-planar.  Typically computed lazily
	 * on demand and not encoded.
	 *
	 * <p>Not typically needed by models. Exposed to enable standard lighting
	 * utility functions for use by renderers.
	 */
	Vector3f faceNormal();

	/**
	 * Retrieves the integer tag encoded with this quad via {@link QuadEditor#tag(int)}.
	 * Will return zero if no tag was set.  For use by models.
	 */
	int tag();

	/**
	 * Pass a non-null target to avoid allocation - will be returned with values.
	 * Otherwise returns a new instance.
	 */
	Vector3f copyPos(int vertexIndex, @Nullable Vector3f target);

	/**
	 * Convenience: access x, y, z by index 0-2.
	 */
	float posByIndex(int vertexIndex, int coordinateIndex);

	/**
	 * Geometric position, x coordinate.
	 */
	float x(int vertexIndex);

	/**
	 * Geometric position, y coordinate.
	 */
	float y(int vertexIndex);

	/**
	 * Geometric position, z coordinate.
	 */
	float z(int vertexIndex);

	/**
	 * If false, no vertex normal was provided.
	 * Lighting should use face normal in that case.
	 */
	boolean hasNormal(int vertexIndex);

	/**
	 * Pass a non-null target to avoid allocation - will be returned with values.
	 * Otherwise returns a new instance. Returns null if normal not present.
	 */
	@Nullable
	Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target);

	/**
	 * Will return {@link Float#NaN} if normal not present.
	 */
	float normalX(int vertexIndex);

	/**
	 * Will return {@link Float#NaN} if normal not present.
	 */
	float normalY(int vertexIndex);

	/**
	 * Will return {@link Float#NaN} if normal not present.
	 */
	float normalZ(int vertexIndex);

	/**
	 * Minimum block brightness. Zero if not set.
	 */
	int lightmap(int vertexIndex);

	/**
	 * Retrieves the material serialized with the quad.
	 */
	RenderMaterial material();

	/**
	 * Reads baked vertex data and outputs standard baked quad
	 * vertex data in the given array and location.
	 *
	 * @param spriteIndex The sprite to be used for the quad.
	 * Behavior for values &gt; 0 is currently undefined.
	 *
	 * @param target Target array for the baked quad data.
	 *
	 * @param targetIndex Starting position in target array - array must have
	 * at least 28 elements available at this index.
	 *
	 * @param isItem If true, will output vertex normals. Otherwise will output
	 * lightmaps, per Minecraft vertex formats for baked models.
	 */
	void toVanilla(int[] target, int targetIndex);

	/**
	 * Generates a new BakedQuad instance with texture
	 * coordinates and colors from the given sprite.
	 *
	 * @param spriteIndex The sprite to be used for the quad.
	 * Behavior for {@code spriteIndex > 0} is currently undefined.
	 *
	 * @param sprite  {@link QuadEditor} does not serialize sprites
	 * so the sprite must be provided by the caller.
	 *
	 * @param isItem If true, will output vertex normals. Otherwise will output
	 * lightmaps, per Minecraft vertex formats for baked models.
	 *
	 * @return A new baked quad instance with the closest-available appearance
	 * supported by vanilla features. Will retain emissive light maps, for example,
	 * but the standard Minecraft renderer will not use them.
	 */
	default BakedQuad toBakedQuad(TextureAtlasSprite sprite) {
		final int[] vertexData = new int[VANILLA_QUAD_STRIDE];
		toVanilla(vertexData, 0);
		return new BakedQuad(vertexData, colorIndex(), lightFace(), sprite, true);
	}

	/**
	 * Retrieve vertex color.
	 */
	int vertexColor(int vertexIndex);

	/**
	 * Retrieve horizontal texture coordinates.
	 *
	 * <p>For sprite atlas textures, the coordinates will be
	 * relative to the atlas texture, not relative to the sprite.
	 */
	float u(int vertexIndex);

	/**
	 * Retrieve vertical texture coordinate.
	 *
	 * <p>For sprite atlas textures, the coordinates will be
	 * relative to the atlas texture, not relative to the sprite.
	 */
	float v(int vertexIndex);

	/**
	 * Retrieve sprite-relative horizontal texture coordinates.
	 *
	 * <p>For sprite atlas textures, the coordinates will be
	 * relative to the sprite texture, not relative to the atlas.
	 *
	 * <p>For non-atlas textures, the result will be the
	 * same as #{@link #u(int)}
	 */
	float uSprite(int vertexIndex);

	/**
	 * Retrieve sprite-relative vertical texture coordinate.
	 *
	 * <p>For sprite atlas textures, the coordinates will be
	 * relative to the sprite texture, not relative to the atlas.
	 *
	 * <p>For non-atlas textures, the result will be the
	 * same as #{@link #v(int)}
	 */
	float vSprite(int vertexIndex);

	/**
	 * If false, no vertex tangent was provided.
	 * Lighting will use automatically computed tangents.
	 */
	boolean hasTangent(int vertexIndex);

	/**
	 * Pass a non-null target to avoid allocation - will be returned with values.
	 * Otherwise returns a new instance. Returns null if tangent not present.
	 */
	@Nullable
	Vector3f copyTangent(int vertexIndex, @Nullable Vector3f target);

	/**
	 * Will return {@link Float#NaN} if tangent not present.
	 */
	float tangentX(int vertexIndex);

	/**
	 * Will return {@link Float#NaN} if tangent not present.
	 */
	float tangentY(int vertexIndex);

	/**
	 * Will return {@link Float#NaN} if tangent not present.
	 */
	float tangentZ(int vertexIndex);
}
