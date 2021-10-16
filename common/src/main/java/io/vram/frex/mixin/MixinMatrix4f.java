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

package io.vram.frex.mixin;

import java.nio.FloatBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import io.vram.frex.api.math.FastMatri4f;

@Mixin(Matrix4f.class)
public abstract class MixinMatrix4f implements FastMatri4f {
	// NB: mojang uses row-major notation in variable names
	// and we use colum-major notation in methods to be consistent with OpenGL and JOML
	@Shadow protected float m00;
	@Shadow protected float m01;
	@Shadow protected float m02;
	@Shadow protected float m03;
	@Shadow protected float m10;
	@Shadow protected float m11;
	@Shadow protected float m12;
	@Shadow protected float m13;
	@Shadow protected float m20;
	@Shadow protected float m21;
	@Shadow protected float m22;
	@Shadow protected float m23;
	@Shadow protected float m30;
	@Shadow protected float m31;
	@Shadow protected float m32;
	@Shadow protected float m33;
	@Shadow public abstract void setIdentity();

	@Unique
	@Override
	public float f_m00() {
		return m00;
	}

	@Unique
	@Override
	public float f_m10() {
		return m01;
	}

	@Unique
	@Override
	public float f_m20() {
		return m02;
	}

	@Unique
	@Override
	public float f_m30() {
		return m03;
	}

	@Unique
	@Override
	public float f_m01() {
		return m10;
	}

	@Unique
	@Override
	public float f_m11() {
		return m11;
	}

	@Unique
	@Override
	public float f_m21() {
		return m12;
	}

	@Unique
	@Override
	public float f_m31() {
		return m13;
	}

	@Unique
	@Override
	public float f_m02() {
		return m20;
	}

	@Unique
	@Override
	public float f_m12() {
		return m21;
	}

	@Unique
	@Override
	public float f_m22() {
		return m22;
	}

	@Unique
	@Override
	public float f_m32() {
		return m23;
	}

	@Unique
	@Override
	public float f_m03() {
		return m30;
	}

	@Unique
	@Override
	public float f_m13() {
		return m31;
	}

	@Unique
	@Override
	public float f_m23() {
		return m32;
	}

	@Unique
	@Override
	public float f_m33() {
		return m33;
	}

	@Unique
	@Override
	public void f_m00(float val) {
		m00 = val;
	}

	@Unique
	@Override
	public void f_m10(float val) {
		m01 = val;
	}

	@Unique
	@Override
	public void f_m20(float val) {
		m02 = val;
	}

	@Unique
	@Override
	public void f_m30(float val) {
		m03 = val;
	}

	@Unique
	@Override
	public void f_m01(float val) {
		m10 = val;
	}

	@Unique
	@Override
	public void f_m11(float val) {
		m11 = val;
	}

	@Unique
	@Override
	public void f_m21(float val) {
		m12 = val;
	}

	@Unique
	@Override
	public void f_m31(float val) {
		m13 = val;
	}

	@Unique
	@Override
	public void f_m02(float val) {
		m20 = val;
	}

	@Unique
	@Override
	public void f_m12(float val) {
		m21 = val;
	}

	@Unique
	@Override
	public void f_m22(float val) {
		m22 = val;
	}

	@Unique
	@Override
	public void f_m32(float val) {
		m23 = val;
	}

	@Unique
	@Override
	public void f_m03(float val) {
		m30 = val;
	}

	@Unique
	@Override
	public void f_m13(float val) {
		m31 = val;
	}

	@Unique
	@Override
	public void f_m23(float val) {
		m32 = val;
	}

	@Unique
	@Override
	public void f_m33(float val) {
		m33 = val;
	}

	@Unique
	@Override
	public void f_writeToBuffer(int baseIndex, FloatBuffer floatBuffer) {
		floatBuffer.put(baseIndex + 0, m00);
		floatBuffer.put(baseIndex + 1, m10);
		floatBuffer.put(baseIndex + 2, m20);
		floatBuffer.put(baseIndex + 3, m30);

		floatBuffer.put(baseIndex + 4, m01);
		floatBuffer.put(baseIndex + 5, m11);
		floatBuffer.put(baseIndex + 6, m21);
		floatBuffer.put(baseIndex + 7, m31);

		floatBuffer.put(baseIndex + 8, m02);
		floatBuffer.put(baseIndex + 9, m12);
		floatBuffer.put(baseIndex + 10, m22);
		floatBuffer.put(baseIndex + 11, m32);

		floatBuffer.put(baseIndex + 12, m03);
		floatBuffer.put(baseIndex + 13, m13);
		floatBuffer.put(baseIndex + 14, m23);
		floatBuffer.put(baseIndex + 15, m33);
	}

	@Override
	@Unique
	public void f_identity() {
		setIdentity();
	}

	@Override
	@Unique
	public void f_set(FastMatri4f val) {
		m00 = val.f_m00();
		m01 = val.f_m10();
		m02 = val.f_m20();
		m03 = val.f_m30();

		m10 = val.f_m01();
		m11 = val.f_m11();
		m12 = val.f_m21();
		m13 = val.f_m31();

		m20 = val.f_m02();
		m21 = val.f_m12();
		m22 = val.f_m22();
		m23 = val.f_m32();

		m30 = val.f_m03();
		m31 = val.f_m13();
		m32 = val.f_m23();
		m33 = val.f_m33();
	}

	@Override
	@Unique
	public void f_scale(float x, float y, float z) {
		m00 *= x;
		m01 *= y;
		m02 *= z;
		m10 *= x;
		m11 *= y;
		m12 *= z;
		m20 *= x;
		m21 *= y;
		m22 *= z;
		m30 *= x;
		m31 *= y;
		m32 *= z;
	}

	@Override
	@Unique
	public void f_transform(Vector3f vec) {
		final float x = vec.x();
		final float y = vec.y();
		final float z = vec.z();

		vec.set(
			m00 * x + m01 * y + m02 * z + m03,
			m10 * x + m11 * y + m12 * z + m13,
			m20 * x + m21 * y + m22 * z + m23);
	}

	@Override
	@Unique
	public void f_translate(float x, float y, float z) {
		final float b03 = m00 * x + m01 * y + m02 * z + m03;
		final float b13 = m10 * x + m11 * y + m12 * z + m13;
		final float b23 = m20 * x + m21 * y + m22 * z + m23;
		final float b33 = m30 * x + m31 * y + m32 * z + m33;

		m03 = b03;
		m13 = b13;
		m23 = b23;
		m33 = b33;
	}
}
