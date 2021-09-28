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

import java.util.function.Consumer;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;

import io.vram.frex.api.mesh.Mesh;

public class FabricMesh implements net.fabricmc.fabric.api.renderer.v1.mesh.Mesh {
	public static FabricMesh of(Mesh wrapped) {
		return new FabricMesh(wrapped);
	}

	final Mesh wrapped;
	private final FabricQuadView<io.vram.frex.api.mesh.QuadView> quadWrapper = FabricQuadView.of(null);

	protected FabricMesh(Mesh wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void forEach(Consumer<QuadView> consumer) {
		wrapped.forEach(q -> {
			consumer.accept(quadWrapper.wrap(q));
		});
	}
}
