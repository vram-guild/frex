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

import io.vram.frex.api.model.ModelRenderContext;

import net.minecraft.client.render.model.BakedModel;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

public class FabricContextWrapper implements RenderContext {
	private ModelRenderContext wrapped;

	public FabricContextWrapper wrap(ModelRenderContext wrapped) {
		this.wrapped = wrapped;
		return this;
	}

	private final Consumer<Mesh> meshConsumer = m -> {
		wrapped.accept(((FabricMesh) m).wrapped);
	};

	private final Consumer<BakedModel> fallbackConsumer = bm -> {
		wrapped.accept(bm, null);
	};

	private final FabricQuadEmitter qe = FabricQuadEmitter.of(null);

	@Override
	public Consumer<Mesh> meshConsumer() {
		return meshConsumer;
	}

	@Override
	public Consumer<BakedModel> fallbackConsumer() {
		return fallbackConsumer;
	}

	@Override
	public QuadEmitter getEmitter() {
		return qe.wrap(wrapped.quadEmitter());
	}

	@Override
	public void pushTransform(QuadTransform transform) {
		wrapped.pushTransform(q -> transform.transform(qe.wrap(q)));
	}

	@Override
	public void popTransform() {
		wrapped.popTransform();
	}
}
