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

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import io.vram.frex.api.mesh.MeshBuilder;

/**
 * Interface for rendering plug-ins that provide enhanced capabilities
 * for model lighting, buffering and rendering. Such plug-ins implement the
 * enhanced model rendering interfaces specified by the Fabric API.
 */
public class FabricMeshBuilder implements net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder {
	public static FabricMeshBuilder of(MeshBuilder wrapped) {
		return new FabricMeshBuilder(wrapped);
	}

	final MeshBuilder wrapped;

	private final FabricQuadEmitter qe;

	protected FabricMeshBuilder(MeshBuilder wrapped) {
		this.wrapped = wrapped;
		qe = FabricQuadEmitter.of(wrapped.getEmitter());
	}

	@Override
	public QuadEmitter getEmitter() {
		return qe;
	}

	@Override
	public Mesh build() {
		return FabricMesh.of(wrapped.build());
	}
}
