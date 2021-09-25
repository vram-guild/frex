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

package io.vram.frex.api.material;

import net.minecraft.resources.ResourceLocation;

public interface RenderMaterial {
	ResourceLocation STANDARD_MATERIAL_KEY = new ResourceLocation("frex", "standard");
	ResourceLocation MISSING_MATERIAL_KEY = new ResourceLocation("frex", "missing");

	int preset();

	boolean blur();

	MaterialCondition condition();

	boolean cull();

	int cutout();

	int decal();

	int depthTest();

	boolean disableAo();

	boolean disableColorIndex();

	boolean disableDiffuse();

	boolean discardsTexture();

	boolean emissive();

	boolean flashOverlay();

	boolean fog();

	ResourceLocation fragmentShaderId();

	String fragmentShader();

	boolean hurtOverlay();

	boolean lines();

	boolean sorted();

	int target();

	ResourceLocation textureId();

	String texture();

	int transparency();

	boolean unmipped();

	ResourceLocation vertexShaderId();

	String vertexShader();

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
}
