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

package io.vram.frex.api.math;

import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface FrexMathUtil {
	/**
	 * Non-allocating substitute for {@link Vector3f#rotate(Quaternion)}.
	 */
	@Deprecated
	static void applyRotation(Vector3f vec, Quaternionf rotation) {
		final float rx = rotation.x;
		final float ry = rotation.y;
		final float rz = rotation.z;
		final float rw = rotation.w;

		final float x = vec.x();
		final float y = vec.y();
		final float z = vec.z();

		final float qx = rw * x + ry * z - rz * y;
		final float qy = rw * y - rx * z + rz * x;
		final float qz = rw * z + rx * y - ry * x;
		final float qw = -rx * x - ry * y - rz * z;

		vec.set(
			qw * -rx + qx * rw - qy * rz + qz * ry,
			qw * -ry + qx * rz + qy * rw - qz * rx,
			qw * -rz - qx * ry + qy * rx + qz * rw);
	}

	/**
	 * Non-allocating substitute for {@link Vector3f#rotate(Quaternion)} that assumes vec.z == 0.
	 */
	static void applyBillboardRotation(Vector3f vec, Quaternionf rotation) {
		final float rx = rotation.x;
		final float ry = rotation.y;
		final float rz = rotation.z;
		final float rw = rotation.w;

		final float x = vec.x();
		final float y = vec.y();

		final float qx = rw * x - rz * y;
		final float qy = rw * y + rz * x;
		final float qz = rx * y - ry * x;
		final float qw = -rx * x - ry * y;

		vec.set(
			qw * -rx + qx * rw - qy * rz + qz * ry,
			qw * -ry + qx * rz + qy * rw - qz * rx,
			qw * -rz - qx * ry + qy * rx + qz * rw);
	}

	static void setRadialRotation(Quaternionf target, Vector3f axis, float radians) {
		final float f = (float) Math.sin(radians / 2.0F);

		target.set(
			axis.x() * f,
			axis.y() * f,
			axis.z() * f,
			(float) Math.cos(radians / 2.0F));
	}

	static float squareDist(float x0, float y0, float z0, float x1, float y1, float z1) {
		final float dx = x1 - x0;
		final float dy = y1 - y0;
		final float dz = z1 - z0;
		return dx * dx + dy * dy + dz * dz;
	}

	static float dist(float x0, float y0, float z0, float x1, float y1, float z1) {
		return (float) Math.sqrt(squareDist(x0, y0, z0, x1, y1, z1));
	}

	static float clampNormalized(float val) {
		return val < 0f ? 0f : (val > 1f ? 1f : val);
	}

	static boolean isIdentity(Matrix3f mat) {
		return mat.m00 == 1.0F && mat.m01 == 0.0F && mat.m02 == 0.0F
				&& mat.m10 == 0.0F && mat.m11 == 1.0F && mat.m12 == 0.0
				&& mat.m20 == 0.0F && mat.m21 == 0.0F && mat.m22 == 1.0F;
	}

	static int transformPacked3f(Matrix3f mat, int packedVector3f) {
		final float x = PackedVector3f.unpackX(packedVector3f);
		final float y = PackedVector3f.unpackY(packedVector3f);
		final float z = PackedVector3f.unpackZ(packedVector3f);

		// PERF: not certain FMA is helping here because may be preventing parallel/out-of-order execution
		final float nx = Math.fma(mat.m00, x, Math.fma(mat.m01, y, mat.m02 * z));
		final float ny = Math.fma(mat.m10, x, Math.fma(mat.m11, y, mat.m12 * z));
		final float nz = Math.fma(mat.m20, x, Math.fma(mat.m21, y, mat.m22 * z));

		return PackedVector3f.pack(nx, ny, nz);
	}
}
