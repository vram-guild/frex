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

package io.vram.frex.base.renderer.util;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;

import io.vram.frex.api.math.FrexMathUtil;
import io.vram.frex.api.math.PackedVector3f;
import io.vram.frex.api.model.InputContext;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;
import io.vram.frex.base.renderer.mesh.MeshEncodingHelper;

public abstract class EncoderUtil {
	public static void encodeQuad(BaseQuadEmitter quad, InputContext inputContext, VertexConsumer buff) {
		final var matrixStack = inputContext.matrixStack();
		final Matrix4f matrix = matrixStack.modelMatrix();
		final Matrix3f normalMatrix = matrixStack.normalMatrix();
		final boolean isNormalMatrixUseful = !FrexMathUtil.isIdentity(normalMatrix);

		final var mat = quad.material();
		final boolean emissive = mat.emissive() | mat.unlit();

		final boolean isHurt = mat.hurtOverlay();
		final boolean isFlash = mat.flashOverlay();
		final int overlayV = isHurt ? 3 : 10;
		// 10 is what TNT uses
		final int overlayU = isFlash ? 10 : 0;

		final int quadNormalFlags = quad.normalFlags();
		// don't retrieve if won't be used
		final int faceNormal = quadNormalFlags == 0b1111 ? 0 : quad.packedFaceNormal();
		int packedNormal = 0;
		float nx = 0, ny = 0, nz = 0;

		for (int i = 0; i < 4; i++) {
			quad.transformAndAppendVertex(i, matrix, buff);

			final int color = quad.vertexColor(i);
			buff.color(color & 0xFF, (color >> 8) & 0xFF, (color >> 16) & 0xFF, (color >> 24) & 0xFF);

			buff.uv(quad.u(i), quad.v(i));
			buff.overlayCoords(overlayU, overlayV);
			buff.uv2(emissive ? MeshEncodingHelper.FULL_BRIGHTNESS : quad.lightmap(i));

			final int p = ((quadNormalFlags & 1 << i) == 0) ? faceNormal : quad.packedNormal(i);

			if (p != packedNormal) {
				packedNormal = p;
				final int transformedNormal = isNormalMatrixUseful ? FrexMathUtil.transformPacked3f(normalMatrix, packedNormal) : packedNormal;
				nx = PackedVector3f.unpackX(transformedNormal);
				ny = PackedVector3f.unpackY(transformedNormal);
				nz = PackedVector3f.unpackZ(transformedNormal);
			}

			buff.normal(nx, ny, nz);
			buff.endVertex();
		}
	}

	/**
	 * Finds mean of per-face shading factors weighted by normal components.
	 * Not how light actually works but the vanilla diffuse shading model is a hack to start with
	 * and this gives reasonable results for non-cubic surfaces in a vanilla-style renderer.
	 */
	public static float normalShade(int packedNormal, BlockAndTintGetter blockView, boolean hasShade) {
		final float normalX = PackedVector3f.unpackX(packedNormal);
		final float normalY = PackedVector3f.unpackY(packedNormal);
		final float normalZ = PackedVector3f.unpackZ(packedNormal);

		float sum = 0;
		float div = 0;

		if (normalX > 0) {
			sum += normalX * blockView.getShade(Direction.EAST, hasShade);
			div += normalX;
		} else if (normalX < 0) {
			sum += -normalX * blockView.getShade(Direction.WEST, hasShade);
			div -= normalX;
		}

		if (normalY > 0) {
			sum += normalY * blockView.getShade(Direction.UP, hasShade);
			div += normalY;
		} else if (normalY < 0) {
			sum += -normalY * blockView.getShade(Direction.DOWN, hasShade);
			div -= normalY;
		}

		if (normalZ > 0) {
			sum += normalZ * blockView.getShade(Direction.SOUTH, hasShade);
			div += normalZ;
		} else if (normalZ < 0) {
			sum += -normalZ * blockView.getShade(Direction.NORTH, hasShade);
			div -= normalZ;
		}

		return sum / div;
	}
}
