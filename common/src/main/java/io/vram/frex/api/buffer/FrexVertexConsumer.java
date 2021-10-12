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

package io.vram.frex.api.buffer;

import org.jetbrains.annotations.ApiStatus.Experimental;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import io.vram.frex.api.material.RenderMaterial;

@Experimental
public interface FrexVertexConsumer extends VertexConsumer, QuadSink {
	FrexVertexConsumer defaultMaterial(RenderMaterial material);

	/**
	 * Sets state to be included with normals and material if they are included.  Call once
	 * whenever material changes, including default state or revert
	 * to default state of the render state.
	 */
	FrexVertexConsumer material(RenderMaterial material);

	FrexVertexConsumer vertex(float x, float y, float z);

	/**
	 * @param color rgba - alpha is high byte, red and blue pre-swapped if needed
	 */
	FrexVertexConsumer color(int color);

	@Override
	default FrexVertexConsumer vertex(double x, double y, double z) {
		vertex((float) x, (float) y, (float) z);
		return this;
	}

	@Override
	FrexVertexConsumer vertex(Matrix4f matrix, float x, float y, float z);

	@Override
	FrexVertexConsumer normal(Matrix3f matrix, float x, float y, float z);

	@Override
	FrexVertexConsumer color(int red, int green, int blue, int alpha);

	@Override
	FrexVertexConsumer uv(float u, float v);

	@Override
	FrexVertexConsumer overlayCoords(int u, int v);

	@Override
	FrexVertexConsumer uv2(int u, int v);

	@Override
	FrexVertexConsumer normal(float x, float y, float z);

	@Override
	default FrexVertexConsumer asVertexConsumer() {
		return this;
	}
}
