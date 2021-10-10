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

package io.vram.frex.api.model;

import net.minecraft.client.resources.model.BakedModel;

import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.mesh.QuadEditor;
import io.vram.frex.base.renderer.context.BaseFallbackConsumer;

public interface ModelOuputContext {
	default void accept(BakedModel model, BakedInputContext input) {
		BaseFallbackConsumer.accept(model, input, this);
	}

	default void accept(Mesh mesh) {
		mesh.forEachWithEditor(q -> q.emit(), quadEmitter());
	}

	QuadEditor quadEmitter();

	void pushTransform(QuadTransform transform);

	void popTransform();

	// TODO: implement
	//MatrixStack matrixStack();
}
