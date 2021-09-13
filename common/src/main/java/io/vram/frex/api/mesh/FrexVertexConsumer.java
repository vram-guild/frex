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

import io.vram.frex.api.material.RenderMaterial;
import org.jetbrains.annotations.ApiStatus.Experimental;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Experimental
public interface FrexVertexConsumer extends VertexConsumer {
	/**
	 * Sets state to be included with normals and material if they are included.  Call once
	 * whenever material changes, including default state or revert
	 * to default state of the render state.
	 */
	FrexVertexConsumer material(RenderMaterial material);

	FrexVertexConsumer defaultMaterial(RenderMaterial material);

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
	FrexVertexConsumer texture(float u, float v);

	@Override
	FrexVertexConsumer overlay(int u, int v);

	@Override
	FrexVertexConsumer light(int u, int v);

	@Override
	FrexVertexConsumer normal(float x, float y, float z);
}
