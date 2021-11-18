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

package io.vram.frex.base.client.model;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.mesh.MeshBuilder;
import io.vram.frex.api.renderer.Renderer;

@FunctionalInterface
public interface MeshFactory {
	default Mesh createMesh(SpriteProvider spriteProvider) {
		return createMesh(Renderer.get().meshBuilder(), MaterialFinder.threadLocal(), spriteProvider);
	}

	Mesh createMesh(MeshBuilder meshBuilder, MaterialFinder finder, SpriteProvider spriteProvider);

	static MeshFactory shared(MeshFactory factory) {
		return new MeshFactory() {
			Mesh mesh = null;

			@Override
			public Mesh createMesh(MeshBuilder meshBuilder, MaterialFinder finder, SpriteProvider spriteProvider) {
				Mesh result = mesh;

				if (result == null) {
					result = factory.createMesh(meshBuilder, finder, spriteProvider);
					mesh = result;
				}

				return result;
			}
		};
	}
}
