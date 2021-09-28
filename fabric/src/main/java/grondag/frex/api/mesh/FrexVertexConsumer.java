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

/*
 * Copyright 2019, 2020 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package grondag.frex.api.mesh;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

@Deprecated
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
	FrexVertexConsumer uv(float u, float v);

	@Override
	FrexVertexConsumer overlayCoords(int u, int v);

	@Override
	FrexVertexConsumer uv2(int u, int v);

	@Override
	FrexVertexConsumer normal(float x, float y, float z);
}
