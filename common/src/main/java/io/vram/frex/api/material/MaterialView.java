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

package io.vram.frex.api.material;

import io.vram.frex.api.texture.MaterialTexture;

/**
 * Getter methods for {@link RenderMaterial} (immutable) and {@link MaterialFinder} (mutable).
 */
public interface MaterialView {
	int preset();

	boolean blur();

	int conditionIndex();

	default MaterialCondition condition() {
		return MaterialCondition.fromIndex(conditionIndex());
	}

	boolean cull();

	int cutout();

	int decal();

	int depthTest();

	boolean disableAo();

	boolean disableAoIsDefault();

	boolean disableColorIndex();

	boolean disableDiffuse();

	boolean disableDiffuseIsDefault();

	boolean discardsTexture();

	boolean emissive();

	boolean emissiveIsDefault();

	boolean unlit();

	boolean flashOverlay();

	boolean flashOverlayIsDefault();

	boolean fog();

	boolean hurtOverlay();

	boolean hurtOverlayIsDefault();

	boolean lines();

	boolean sorted();

	int target();

	int textureIndex();

	default MaterialTexture texture() {
		return MaterialTexture.fromIndex(textureIndex());
	}

	int transparency();

	boolean unmipped();

	int shaderIndex();

	default MaterialShader shader() {
		return MaterialShader.fromIndex(shaderIndex());
	}

	int writeMask();

	/**
	 * A name associated with this material for diagnostic purposes.
	 *
	 * <p>This name is not reliable and renderers will generally not treat it as part
	 * of material state - if two materials have identical properties aside from name,
	 * most renderers will treat those as the same material.  Thus, this name will
	 * probably be the name assigned by whatever process requested it first, if any.
	 *
	 * <p>For materials that are derived from vanilla RenderType instances, FREX will
	 * attempt to label them with the an associated name (given by Mojang) of that type.
	 *
	 * @return label associated vanilla {@code RenderLayer} if any, undefined otherwise
	 */
	String label();

	/**
	 * True when material should cast shadows. Pipelines that lack
	 * shadowmaps or some other mechanism for realistic shadows will
	 * ignore this.  Defaults to true.
	 *
	 * <p>Set false for materials that render as solid for practical
	 * reasons but should not cast shadows.  Beacon beams are a vanilla example.
	 *
	 * @return true if material should cast shadows
	 */
	boolean castShadows();

	boolean foilOverlay();

	boolean foilOverlayIsDefault();

	boolean glintEntity();
}
