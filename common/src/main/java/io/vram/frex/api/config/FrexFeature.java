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

package io.vram.frex.api.config;

import io.vram.frex.impl.config.FrexFeatureImpl;

public interface FrexFeature {
	static boolean isAvailable(int featureId) {
		return FrexFeatureImpl.isAvailable(featureId);
	}

	/**
	 * Renderers should call this exactly once during init to declare available features.
	 *
	 * @param features Array of feature flags declared here or third-party flags declared elsewhere.
	 */
	static void registerFeatures(int... features) {
		FrexFeatureImpl.registerFeatures(features);
		FrexConfig.computeVertexTangents = isAvailable(VERTEX_TANGENT);
	}

	// IDs 0 - 4095 are reserved for FREX.

	/**
	 * Present when renderer uses held item light API.
	 * Renderer-dependent, unavailable with Fabric API implementations.
	 * This feature is also pipeline-dependent.
	 *
	 * <p>Note that JSON loading and the event API are
	 * provided by FREX and will always be available - no
	 * special handling is required to create a soft dependency.
	 */
	int HELD_ITEM_LIGHTS = 1;

	/**
	 * Present when the renderer uses vertex tangents,
	 * making them available in shaders.  This flag
	 * also controls the execution of tangent computation
	 * in the FREX base classes - disabling it for performance
	 * reasons if not needed.
	 */
	int VERTEX_TANGENT = 2;

	int MATERIAL_SHADERS = 1024;

	/**
	 * Present when the renderer consumes physical texture maps from FREX and
	 * makes them available to shaders to enable physically-based-rendering.
	 */
	int PHYSICAL_TEXTURES = 1025;

	/** Third-party extension features begin numbering here. */
	int EXTENSION_BASE = 4096;
}
