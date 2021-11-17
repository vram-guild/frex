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

package grondag.frex.api.mesh;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

@Deprecated
public interface MutableQuadView extends net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView {
	@Override
	MutableQuadView material(RenderMaterial material);

	@Override
	@Nullable
	MutableQuadView cullFace(@Nullable Direction face);

	@Override
	@Nullable
	MutableQuadView nominalFace(Direction face);

	@Override
	MutableQuadView colorIndex(int colorIndex);

	@Override
	@Deprecated
	MutableQuadView fromVanilla(int[] quadData, int startIndex, boolean isItem);

	@Override
	MutableQuadView fromVanilla(BakedQuad quad, RenderMaterial material, Direction cullFace);

	@Override
	MutableQuadView tag(int tag);

	@Override
	MutableQuadView pos(int vertexIndex, float x, float y, float z);

	@Override
	default MutableQuadView pos(int vertexIndex, Vector3f vec) {
		return pos(vertexIndex, vec.x(), vec.y(), vec.z());
	}

	@Override
	MutableQuadView normal(int vertexIndex, float x, float y, float z);

	@Override
	default MutableQuadView normal(int vertexIndex, Vector3f vec) {
		return normal(vertexIndex, vec.x(), vec.y(), vec.z());
	}

	default MutableQuadView tangent(int vertexIndex, Vector3f vec) {
		tangent(vertexIndex, vec.x(), vec.y(), vec.z());
		return this;
	}

	MutableQuadView tangent(int vertexIndex, float x, float y, float z);

	@Override
	MutableQuadView lightmap(int vertexIndex, int lightmap);

	@Override
	default MutableQuadView lightmap(int b0, int b1, int b2, int b3) {
		lightmap(0, b0);
		lightmap(1, b1);
		lightmap(2, b2);
		lightmap(3, b3);
		return this;
	}

	@Override
	@Deprecated
	@ScheduledForRemoval
	default MutableQuadView spriteColor(int vertexIndex, int spriteIndex, int color) {
		return vertexColor(vertexIndex, color);
	}

	/**
	 * Set color for given vertex.
	 */
	MutableQuadView vertexColor(int vertexIndex, int color);

	@Override
	@Deprecated
	@ScheduledForRemoval
	default MutableQuadView spriteColor(int spriteIndex, int c0, int c1, int c2, int c3) {
		return quadColor(c0, c1, c2, c3);
	}

	/**
	 * Convenience: set color for all vertices at once.
	 */
	default MutableQuadView quadColor(int c0, int c1, int c2, int c3) {
		vertexColor(0, c0);
		vertexColor(1, c1);
		vertexColor(2, c2);
		vertexColor(3, c3);
		return this;
	}

	@Override
	@Deprecated
	@ScheduledForRemoval
	default MutableQuadView sprite(int vertexIndex, int spriteIndex, float u, float v) {
		return sprite(vertexIndex, u, v);
	}

	/**
	 * Set sprite atlas coordinates.
	 */
	MutableQuadView sprite(int vertexIndex, float u, float v);

	@Override
	@Deprecated
	@ScheduledForRemoval
	default MutableQuadView spriteBake(int spriteIndex, TextureAtlasSprite sprite, int bakeFlags) {
		return spriteBake(sprite, bakeFlags);
	}

	/**
	 * Assigns sprite atlas u,v coordinates to this quad for the given sprite.
	 * Can handle UV locking, rotation, interpolation, etc. Control this behavior
	 * by passing additive combinations of the BAKE_ flags defined in this interface.
	 * Behavior for {@code spriteIndex > 0} is currently undefined.
	 */
	MutableQuadView spriteBake(TextureAtlasSprite sprite, int bakeFlags);
}
