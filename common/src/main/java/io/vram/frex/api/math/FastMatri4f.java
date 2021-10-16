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

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

/**
 * Element names are in column-major order, consistent with OpenGl and JOML conventions.
 */
public interface FastMatri4f {
	float f_m00();

	float f_m10();

	float f_m20();

	float f_m30();

	float f_m01();

	float f_m11();

	float f_m21();

	float f_m31();

	float f_m02();

	float f_m12();

	float f_m22();

	float f_m32();

	float f_m03();

	float f_m13();

	float f_m23();

	float f_m33();

	void f_m00(float val);

	void f_m10(float val);

	void f_m20(float val);

	void f_m30(float val);

	void f_m01(float val);

	void f_m11(float val);

	void f_m21(float val);

	void f_m31(float val);

	void f_m02(float val);

	void f_m12(float val);

	void f_m22(float val);

	void f_m32(float val);

	void f_m03(float val);

	void f_m13(float val);

	void f_m23(float val);

	void f_m33(float val);

	void f_identity();

	default void f_mul(FastMatri4f val) {
		((Matrix4f) (Object) this).multiply((Matrix4f) (Object) val);
	}

	void f_set(FastMatri4f val);

	default void f_set(Matrix4f val) {
		f_set((FastMatri4f) (Object) val);
	}

	default boolean f_equals(FastMatri4f val) {
		return f_m00() == val.f_m00()
			&& f_m10() == val.f_m10()
			&& f_m20() == val.f_m20()
			&& f_m30() == val.f_m30()

			&& f_m01() == val.f_m01()
			&& f_m11() == val.f_m11()
			&& f_m21() == val.f_m21()
			&& f_m31() == val.f_m31()

			&& f_m02() == val.f_m02()
			&& f_m12() == val.f_m12()
			&& f_m22() == val.f_m22()
			&& f_m32() == val.f_m32()

			&& f_m03() == val.f_m03()
			&& f_m13() == val.f_m13()
			&& f_m23() == val.f_m23()
			&& f_m33() == val.f_m33();
	}

	default boolean f_equals(Matrix4f val) {
		return f_equals((FastMatri4f) (Object) val);
	}

	void f_transform(Vector3f vec);

	void f_translate(float x, float y, float z);

	void f_scale(float x, float y, float z);

	void f_writeToBuffer(int baseIndex, FloatBuffer floatBuffer);

	/**
	 * Maps view space (with camera pointing towards negative Z) to -1/+1 NDC
	 * coordinates expected by OpenGL.
	 *
	 * <P>Note comments on near and far distance! These are depth along z axis,
	 * or in other words, you must negate the view space z-axis bounds when passing them.
	 *
	 * @param left bound towards negative x axis
	 * @param right bound towards positive x axis
	 * @param bottom bound towards negative y axis
	 * @param top bound towards positive y axis
	 * @param near distance of near plane from camera (POSITIVE!)
	 * @param far distance of far plane from camera (POSITIVE!)
	 */
	default void f_setOrtho(float left, float right, float bottom, float top, float near, float far) {
		f_identity();
		f_m00(2.0f / (right - left));
		f_m30(-(right + left) / (right - left));

		f_m11(2.0f / (top - bottom));
		f_m31(-(top + bottom) / (top - bottom));

		f_m22(2.0f / (near - far));
		f_m32(-(far + near) / (far - near));
	}

	// best explanation seen so far:  http://www.songho.ca/opengl/gl_camera.html#lookat
	default void f_setLookAt(
		float fromX, float fromY, float fromZ,
		float toX, float toY, float toZ,
		float basisX, float basisY, float basisZ
	) {
		// the forward (Z) axis is the implied look vector
		float forwardX, forwardY, forwardZ;
		forwardX = fromX - toX;
		forwardY = fromY - toY;
		forwardZ = fromZ - toZ;

		final float inverseForwardLength = 1.0f / (float) Math.sqrt(forwardX * forwardX + forwardY * forwardY + forwardZ * forwardZ);
		forwardX *= inverseForwardLength;
		forwardY *= inverseForwardLength;
		forwardZ *= inverseForwardLength;

		// the left (X) axis is found with cross product of forward and given "up" vector
		float leftX, leftY, leftZ;
		leftX = basisY * forwardZ - basisZ * forwardY;
		leftY = basisZ * forwardX - basisX * forwardZ;
		leftZ = basisX * forwardY - basisY * forwardX;

		final float inverseLengthA = 1.0f / (float) Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= inverseLengthA;
		leftY *= inverseLengthA;
		leftZ *= inverseLengthA;

		// Orthonormal "up" axis (Y) is the cross product of those two
		// Should already be a unit vector as both inputs are.
		final float upX = forwardY * leftZ - forwardZ * leftY;
		final float upY = forwardZ * leftX - forwardX * leftZ;
		final float upZ = forwardX * leftY - forwardY * leftX;

		f_m00(leftX);
		f_m10(leftY);
		f_m20(leftZ);
		f_m30(-(leftX * fromX + leftY * fromY + leftZ * fromZ));
		f_m01(upX);
		f_m11(upY);
		f_m21(upZ);
		f_m31(-(upX * fromX + upY * fromY + upZ * fromZ));
		f_m02(forwardX);
		f_m12(forwardY);
		f_m22(forwardZ);
		f_m32(-(forwardX * fromX + forwardY * fromY + forwardZ * fromZ));
		f_m03(0.0f);
		f_m13(0.0f);
		f_m23(0.0f);
		f_m33(1.0f);
	}

	static FastMatri4f cast(Matrix4f matrix) {
		return (FastMatri4f) (Object) matrix;
	}
}
