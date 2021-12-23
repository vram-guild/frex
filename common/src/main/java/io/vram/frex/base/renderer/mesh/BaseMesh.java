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

package io.vram.frex.base.renderer.mesh;

import java.util.function.Consumer;

import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.mesh.QuadView;

/**
 * Implementation of {@link Mesh}.
 * The way we encode meshes makes it very simple.
 */
public class BaseMesh implements Mesh {
	final int[] data;
	/**
	 * Used to satisfy external calls to {@link #forEach(Consumer)}.
	 */
	ThreadLocal<BaseQuadView> POOL = ThreadLocal.withInitial(this::newView);

	/** Override for custom view implementations. */
	protected BaseQuadView newView() {
		return new BaseQuadView();
	}

	BaseMesh(int[] data) {
		this.data = data;
	}

	public int[] data() {
		return data;
	}

	@Override
	public void forEach(Consumer<QuadView> consumer) {
		forEach(consumer, POOL.get());
	}

	/**
	 * The renderer will call this with it's own cursor
	 * to avoid the performance hit of a thread-local lookup.
	 * Also means renderer can hold final references to quad buffers.
	 */
	void forEach(Consumer<QuadView> consumer, BaseQuadView cursor) {
		final int limit = data.length;
		int index = 0;

		while (index < limit) {
			cursor.load(data, index);
			consumer.accept(cursor);
			index += MeshEncodingHelper.TOTAL_MESH_QUAD_STRIDE;
		}
	}
}
