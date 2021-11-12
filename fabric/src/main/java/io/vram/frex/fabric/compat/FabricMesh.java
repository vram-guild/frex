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

package io.vram.frex.fabric.compat;

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
