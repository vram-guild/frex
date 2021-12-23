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

package io.vram.frex.impl.math;

import java.lang.reflect.Constructor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

public class MatrixStackEntryHelper {
	private static final Constructor<PoseStack.Pose> CONSTRUCTOR;

	static {
		Constructor<PoseStack.Pose> c;

		try {
			c = PoseStack.Pose.class.getDeclaredConstructor(Matrix4f.class, Matrix3f.class);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		c.setAccessible(true);
		CONSTRUCTOR = c;
	}

	public static PoseStack.Pose create(Matrix4f matrix4f, Matrix3f matrix3f) {
		try {
			return CONSTRUCTOR.newInstance(matrix4f, matrix3f);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
