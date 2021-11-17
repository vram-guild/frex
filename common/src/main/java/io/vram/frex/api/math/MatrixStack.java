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

package io.vram.frex.api.math;

import com.mojang.blaze3d.vertex.PoseStack;

import io.vram.frex.mixinterface.PoseStackExt;

public interface MatrixStack {
	void push();

	void pop();

	FastMatrix4f modelMatrix();

	FastMatrix3f normalMatrix();

	default void translate(float x, float y, float z) {
		modelMatrix().f_translate(x, y, z);
	}

	default void setIdentity() {
		modelMatrix().f_setIdentity();
		normalMatrix().f_setIdentity();
	}

	PoseStack toVanilla();

	static MatrixStack fromVanilla(PoseStack poseStack) {
		return ((PoseStackExt) poseStack).frx_asMatrixStack();
	}

	static MatrixStack create() {
		return fromVanilla(new PoseStack());
	}
}
