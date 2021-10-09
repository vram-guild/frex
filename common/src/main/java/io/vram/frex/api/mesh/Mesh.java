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

package io.vram.frex.api.mesh;

import java.util.function.Consumer;

/**
 * A bundle of one or more {@link QuadView} instances encoded by the renderer,
 * typically via {@link Renderer#meshBuilder()}.
 *
 * <p>Similar in purpose to the {@code List<BakedQuad>} instances returned by BakedModel, but
 * affords the renderer the ability to optimize the format for performance
 * and memory allocation.
 *
 * <p>Only the renderer should implement or extend this interface.
 */
public interface Mesh {
	/**
	 * Use to access all of the quads encoded in this mesh. The quad instances
	 * sent to the consumer will likely be threadlocal/reused and should never
	 * be retained by the consumer.
	 */
	void forEach(Consumer<QuadView> consumer);

	default void forEachWithEditor(Consumer<QuadEditor> consumer, QuadEditor editor) {
		forEach(v -> {
			v.copyTo(editor);
			consumer.accept(editor);
		});
	}
}
