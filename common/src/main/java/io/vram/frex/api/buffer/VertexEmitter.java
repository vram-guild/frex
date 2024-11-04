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

package io.vram.frex.api.buffer;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.vram.frex.api.material.RenderMaterial;

@Experimental
public interface VertexEmitter extends VertexConsumer, QuadSink {
	VertexEmitter defaultMaterial(RenderMaterial material);

	/**
	 * Sets state to be included with normals and material if they are included.  Call once
	 * whenever material changes, including default state or revert
	 * to default state of the render state.
	 */
	VertexEmitter material(RenderMaterial material);

	/**
	 * @param color rgba - alpha is high byte, red and blue pre-swapped if needed
	 */
	@Override
	VertexEmitter setColor(int color);

	@Override
	default VertexEmitter addVertex(float x, float y, float z) {
		addVertex(x, y, z);
		return this;
	}

	@Override
	VertexEmitter addVertex(Matrix4f matrix, float x, float y, float z);

	@Override
	VertexEmitter setNormal(PoseStack.Pose matrix, float x, float y, float z);

	@Override
	VertexEmitter setColor(int red, int green, int blue, int alpha);

	@Override
	VertexEmitter setUv(float u, float v);

	@Override
	VertexEmitter setUv1(int u, int v);

	@Override
	VertexEmitter setUv2(int u, int v);

	@Override
	VertexEmitter setNormal(float x, float y, float z);

	@Override
	default VertexEmitter asVertexEmitter() {
		return this;
	}
}
