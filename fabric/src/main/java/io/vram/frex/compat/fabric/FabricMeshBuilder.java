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
