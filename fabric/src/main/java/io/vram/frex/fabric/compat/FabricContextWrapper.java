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

package io.vram.frex.fabric.compat;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.client.resources.model.BakedModel;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.BakedInputContext;
import io.vram.frex.base.renderer.util.BakedModelTranscoder;

public class FabricContextWrapper implements RenderContext {
	private BakedInputContext input;
	private QuadSink output;
	private final ObjectArrayList<QuadSink> outputStack = new ObjectArrayList<>();

	private final Consumer<Mesh> meshConsumer = m -> {
		(((FabricMesh) m).wrapped).outputTo(output.asQuadEmitter());
	};

	private final Consumer<BakedModel> fallbackConsumer = bm -> {
		BakedModelTranscoder.accept(bm, input, output.asQuadEmitter());
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
		return qe.wrap(output.asQuadEmitter());
	}

	@Override
	public void pushTransform(QuadTransform transform) {
		outputStack.push(output);

		output = output.withTransformQuad(input, (ctx, in, out) -> {
			in.copyTo(out);

			if (transform.transform(qe.wrap(out))) {
				out.emit();
			}
		});
	}

	@Override
	public void popTransform() {
		output.close();
		output = outputStack.pop();
	}

	private static final ThreadLocal<FabricContextWrapper> POOL = ThreadLocal.withInitial(FabricContextWrapper::new);

	public static FabricContextWrapper wrap(BakedInputContext input, QuadSink output) {
		final var result = POOL.get();
		result.input = input;
		result.output = output;
		return result;
	}
}
