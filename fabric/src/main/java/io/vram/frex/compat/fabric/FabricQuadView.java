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

package io.vram.frex.compat.fabric;

import com.mojang.math.Vector3f;
import io.vram.frex.api.mesh.QuadView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;

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
		return wrapped.faceNormal();
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
		return wrapped.normalX(vertexIndex);
	}

	@Override
	public float normalY(int vertexIndex) {
		return wrapped.normalY(vertexIndex);
	}

	@Override
	public float normalZ(int vertexIndex) {
		return wrapped.normalZ(vertexIndex);
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

	@Override
	public boolean hasTangent(int vertexIndex) {
		return wrapped.hasTangent(vertexIndex);
	}

	@Override
	public @Nullable Vector3f copyTangent(int vertexIndex, @Nullable Vector3f target) {
		return wrapped.copyTangent(vertexIndex, target);
	}

	@Override
	public float tangentX(int vertexIndex) {
		return wrapped.tangentX(vertexIndex);
	}

	@Override
	public float tangentY(int vertexIndex) {
		return wrapped.tangentY(vertexIndex);
	}

	@Override
	public float tangentZ(int vertexIndex) {
		return wrapped.tangentZ(vertexIndex);
	}
}
