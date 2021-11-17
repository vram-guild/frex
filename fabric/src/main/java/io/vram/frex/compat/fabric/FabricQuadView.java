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

package io.vram.frex.compat.fabric;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.core.Direction;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;

import io.vram.frex.api.math.PackedVector3f;
import io.vram.frex.api.mesh.QuadView;

@SuppressWarnings("deprecation")
public class FabricQuadView<T extends QuadView> implements grondag.frex.api.mesh.QuadView {
	public static FabricQuadView<QuadView> of(QuadView wrapped) {
		return new FabricQuadView<>(wrapped);
	}

	protected T wrapped;

	public FabricQuadView<T> wrap(T wrapped) {
		this.wrapped = wrapped;
		return this;
	}

	protected FabricQuadView(T wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void toVanilla(int spriteIndex, int[] target, int targetIndex, boolean isItem) {
		wrapped.toVanilla(target, targetIndex);
	}

	@Override
	public void copyTo(MutableQuadView target) {
		wrapped.copyTo(((FabricQuadEmitter) target).wrapped);
	}

	@Override
	public grondag.frex.api.material.RenderMaterial material() {
		return FabricMaterial.of(wrapped.material());
	}

	@Override
	public int colorIndex() {
		return wrapped.colorIndex();
	}

	@Override
	public @NotNull Direction lightFace() {
		return wrapped.lightFace();
	}

	@Override
	public @Nullable Direction cullFace() {
		return wrapped.cullFace();
	}

	@Override
	public Direction nominalFace() {
		return wrapped.nominalFace();
	}

	@Override
	public Vector3f faceNormal() {
		return PackedVector3f.unpackTo(wrapped.packedFaceNormal(), FACE_NORMAL_THREADLOCAL.get());
	}

	@Override
	public int tag() {
		return wrapped.tag();
	}

	@Override
	public Vector3f copyPos(int vertexIndex, @Nullable Vector3f target) {
		return wrapped.copyPos(vertexIndex, target);
	}

	@Override
	public float posByIndex(int vertexIndex, int coordinateIndex) {
		return wrapped.posByIndex(vertexIndex, coordinateIndex);
	}

	@Override
	public float x(int vertexIndex) {
		return wrapped.x(vertexIndex);
	}

	@Override
	public float y(int vertexIndex) {
		return wrapped.y(vertexIndex);
	}

	@Override
	public float z(int vertexIndex) {
		return wrapped.z(vertexIndex);
	}

	@Override
	public boolean hasNormal(int vertexIndex) {
		return wrapped.hasNormal(vertexIndex);
	}

	@Override
	public @Nullable Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target) {
		return wrapped.copyNormal(vertexIndex, target);
	}

	@Override
	public float normalX(int vertexIndex) {
		return hasNormal(vertexIndex) ? PackedVector3f.packedX(wrapped.packedNormal(vertexIndex)) : Float.NaN;
	}

	@Override
	public float normalY(int vertexIndex) {
		return hasNormal(vertexIndex) ? PackedVector3f.packedY(wrapped.packedNormal(vertexIndex)) : Float.NaN;
	}

	@Override
	public float normalZ(int vertexIndex) {
		return hasNormal(vertexIndex) ? PackedVector3f.packedZ(wrapped.packedNormal(vertexIndex)) : Float.NaN;
	}

	@Override
	public int lightmap(int vertexIndex) {
		return wrapped.lightmap(vertexIndex);
	}

	@Override
	public int spriteColor(int vertexIndex, int spriteIndex) {
		return wrapped.vertexColor(vertexIndex);
	}

	@Override
	public float spriteU(int vertexIndex, int spriteIndex) {
		return wrapped.u(vertexIndex);
	}

	@Override
	public float spriteV(int vertexIndex, int spriteIndex) {
		return wrapped.v(vertexIndex);
	}

	@Override
	public void toVanilla(int[] target, int targetIndex) {
		wrapped.toVanilla(target, targetIndex);
	}

	@Override
	public int vertexColor(int vertexIndex) {
		return wrapped.vertexColor(vertexIndex);
	}

	@Override
	public float spriteU(int vertexIndex) {
		return wrapped.u(vertexIndex);
	}

	@Override
	public float spriteV(int vertexIndex) {
		return wrapped.v(vertexIndex);
	}

	private static final ThreadLocal<Vector3f> FACE_NORMAL_THREADLOCAL = ThreadLocal.withInitial(Vector3f::new);
}
