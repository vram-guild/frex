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

package io.vram.frex.fabric.compat;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.BakedInputContext;
import io.vram.frex.base.renderer.util.BakedModelTranscoder;

public class FabricContextWrapper implements RenderContext {
	// NB: We need to keep a stack of the QE wrappers also because we don't
	// control how the wrappers are used or the order or number of operations.

	private static class Output {
		QuadSink sink;
		final FabricQuadEmitter emitter = FabricQuadEmitter.of(null);

		private void prepare(QuadSink sink) {
			this.sink = sink;
			emitter.wrapped = sink.asQuadEmitter();
		}

		private void clear() {
			sink.close();
			sink = null;
			emitter.wrapped = null;
		}
	}

	private BakedInputContext input;
	private Output output = new Output();
	private final ObjectArrayList<Output> outputStack = new ObjectArrayList<>();
	private final ObjectArrayList<Output> pool = new ObjectArrayList<>();

	private final Consumer<Mesh> meshConsumer = m -> {
		(((FabricMesh) m).wrapped).outputTo(output.sink.asQuadEmitter());
	};

	private final BakedModelConsumer fallbackConsumer = new BakedModelConsumer() {
		@Override
		public void accept(BakedModel model) {
			BakedModelTranscoder.accept(model, input, output.sink.asQuadEmitter());
		}

		@Override
		public void accept(BakedModel model, @Nullable BlockState state) {
			BakedModelTranscoder.accept(model, input, output.sink.asQuadEmitter());
		}
	};

	@Override
	public Consumer<Mesh> meshConsumer() {
		return meshConsumer;
	}

	@Override
	public BakedModelConsumer bakedModelConsumer() {
		return fallbackConsumer;
	}

	@Override
	public Consumer<BakedModel> fallbackConsumer() {
		return fallbackConsumer;
	}

	@Override
	public QuadEmitter getEmitter() {
		return output.emitter;
	}

	@Override
	public void pushTransform(QuadTransform transform) {
		outputStack.push(output);

		final var outEmitter = output.emitter;

		final var newSink = output.sink.withTransformQuad(input, (ctx, in, out) -> {
			in.copyTo(out);

			if (transform.transform(outEmitter)) {
				out.emit();
			}
		});

		output = createOutput(newSink);
	}

	private Output createOutput(QuadSink sink) {
		final var result = pool.isEmpty() ? new Output() : pool.pop();
		result.prepare(sink);
		return result;
	}

	@Override
	public void popTransform() {
		output.clear();
		pool.push(output);
		output = outputStack.pop();
	}

	private static final ThreadLocal<FabricContextWrapper> POOL = ThreadLocal.withInitial(FabricContextWrapper::new);

	public static FabricContextWrapper wrap(BakedInputContext input, QuadSink output) {
		final var result = POOL.get();
		result.input = input;
		result.output.prepare(output.asQuadEmitter());
		return result;
	}
}
