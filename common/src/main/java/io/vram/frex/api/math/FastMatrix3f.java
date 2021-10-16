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

import java.nio.FloatBuffer;

import com.mojang.math.Matrix3f;

/**
 * Element names are in column-major order, consistent with OpenGl and JOML conventions.
 */
public interface FastMatrix3f {
	float f_m00();

	float f_m10();

	float f_m20();

	float f_m01();

	float f_m11();

	float f_m21();

	float f_m02();

	float f_m12();

	float f_m22();

	void f_m00(float val);

	void f_m10(float val);

	void f_m20(float val);

	void f_m01(float val);

	void f_m11(float val);

	void f_m21(float val);

	void f_m02(float val);

	void f_m12(float val);

	void f_m22(float val);

	int f_transformPacked3f(int packedNormal);

	default void f_set(FastMatrix3f val) {
		f_m00(val.f_m00());
		f_m10(val.f_m10());
		f_m20(val.f_m20());

		f_m01(val.f_m01());
		f_m11(val.f_m11());
		f_m21(val.f_m21());

		f_m02(val.f_m02());
		f_m12(val.f_m12());
		f_m22(val.f_m22());
	}

	default void f_set(Matrix3f val) {
		f_set((FastMatrix3f) (Object) val);
	}

	default void f_writeToBuffer(FloatBuffer floatBuffer) {
		floatBuffer.put(0 * 3 + 0, f_m00());
		floatBuffer.put(1 * 3 + 0, f_m10());
		floatBuffer.put(2 * 3 + 0, f_m20());
		floatBuffer.put(0 * 3 + 1, f_m01());
		floatBuffer.put(1 * 3 + 1, f_m11());
		floatBuffer.put(2 * 3 + 1, f_m21());
		floatBuffer.put(0 * 3 + 2, f_m02());
		floatBuffer.put(1 * 3 + 2, f_m12());
		floatBuffer.put(2 * 3 + 2, f_m22());
	}

	boolean f_isIdentity();

	static FastMatrix3f cast(Matrix3f matrix) {
		return (FastMatrix3f) (Object) matrix;
	}
}
