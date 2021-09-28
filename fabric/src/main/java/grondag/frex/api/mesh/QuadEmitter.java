/*
 * Copyright © Contributing Authors
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

import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

@Deprecated
public interface QuadEmitter extends net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter, MutableQuadView {
	@Override
	QuadEmitter material(RenderMaterial material);

	@Override
	QuadEmitter cullFace(Direction face);

	@Override
	QuadEmitter nominalFace(Direction face);

	@Override
	QuadEmitter colorIndex(int colorIndex);

	@Override
	QuadEmitter fromVanilla(int[] quadData, int startIndex, boolean isItem);

	@Override
	QuadEmitter tag(int tag);

	@Override
	QuadEmitter pos(int vertexIndex, float x, float y, float z);

	@Override
	default QuadEmitter pos(int vertexIndex, Vector3f vec) {
		MutableQuadView.super.pos(vertexIndex, vec);
		return this;
	}

	@Override
	QuadEmitter normal(int vertexIndex, float x, float y, float z);

	@Override
	default QuadEmitter normal(int vertexIndex, Vector3f vec) {
		MutableQuadView.super.normal(vertexIndex, vec);
		return this;
	}

	@Override
	default QuadEmitter tangent(int vertexIndex, Vector3f vec) {
		MutableQuadView.super.tangent(vertexIndex, vec);
		return this;
	}

	@Override
	QuadEmitter tangent(int vertexIndex, float x, float y, float z);

	@Override
	QuadEmitter lightmap(int vertexIndex, int lightmap);

	@Override
	default QuadEmitter lightmap(int b0, int b1, int b2, int b3) {
		MutableQuadView.super.lightmap(b0, b1, b2, b3);
		return this;
	}

	@Override
	@Deprecated
	@ScheduledForRemoval
	default QuadEmitter spriteColor(int vertexIndex, int spriteIndex, int color) {
		vertexColor(vertexIndex, color);
		return this;
	}

	@Override
	@Deprecated
	@ScheduledForRemoval
	default QuadEmitter spriteColor(int spriteIndex, int c0, int c1, int c2, int c3) {
		quadColor(c0, c1, c2, c3);
		return this;
	}

	@Override
	@Deprecated
	@ScheduledForRemoval
	default QuadEmitter sprite(int vertexIndex, int spriteIndex, float u, float v) {
		sprite(vertexIndex, u, v);
		return this;
	}

	@Override
	@Deprecated
	@ScheduledForRemoval
	default QuadEmitter spriteUnitSquare(int spriteIndex) {
		sprite(0, 0, 0);
		sprite(1, 0, 1);
		sprite(2, 1, 1);
		sprite(3, 1, 0);
		return this;
	}

	@Override
	@Deprecated
	@ScheduledForRemoval
	default QuadEmitter spriteBake(int spriteIndex, TextureAtlasSprite sprite, int bakeFlags) {
		spriteBake(sprite, bakeFlags);
		return this;
	}

	@Override
	default QuadEmitter square(Direction nominalFace, float left, float bottom, float right, float top, float depth) {
		net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter.super.square(nominalFace, left, bottom, right, top, depth);
		return this;
	}

	@Override
	QuadEmitter emit();
}
