/*
 * Copyright © Original Authors
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

import static java.lang.Math.fma;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.math.Matrix3f;

import io.vram.frex.api.math.FastMatrix3f;
import io.vram.frex.api.math.PackedVector3f;

@Mixin(Matrix3f.class)
public abstract class MixinMatrix3f implements FastMatrix3f {
	// NB: mojang uses row-major notation in variable names
	// and we use colum-major notation in methods to be consistent with OpenGL and JOML
	@Shadow protected float m00;
	@Shadow protected float m01;
	@Shadow protected float m02;
	@Shadow protected float m10;
	@Shadow protected float m11;
	@Shadow protected float m12;
	@Shadow protected float m20;
	@Shadow protected float m21;
	@Shadow protected float m22;
	@Shadow public abstract void setIdentity();

	@Unique
	@Override
	public int f_transformPacked3f(int packedVector3f) {
		final float x = PackedVector3f.unpackX(packedVector3f);
		final float y = PackedVector3f.unpackY(packedVector3f);
		final float z = PackedVector3f.unpackZ(packedVector3f);

		final float nx = fma(m00, x, fma(m01, y, m02 * z));
		final float ny = fma(m10, x, fma(m11, y, m12 * z));
		final float nz = fma(m20, x, fma(m21, y, m22 * z));

		return PackedVector3f.pack(nx, ny, nz);
	}

	@Unique
	@Override
	public boolean f_isIdentity() {
		return m00 == 1.0F && m01 == 0.0F && m02 == 0.0F
				&& m10 == 0.0F && m11 == 1.0F && m12 == 0.0
				&& m20 == 0.0F && m21 == 0.0F && m22 == 1.0F;
	}

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

	@Override
	public void f_setIdentity() {
		setIdentity();
	}
}
