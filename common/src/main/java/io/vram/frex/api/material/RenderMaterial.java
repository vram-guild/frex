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

import net.minecraft.util.Identifier;

public interface RenderMaterial {
	Identifier MATERIAL_STANDARD = new Identifier("frex", "standard");

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

	Identifier fragmentShaderId();

	String fragmentShader();

	boolean hurtOverlay();

	boolean lines();

	boolean sorted();

	int target();

	Identifier textureId();

	String texture();

	int transparency();

	boolean unmipped();

	Identifier vertexShaderId();

	String vertexShader();

	int writeMask();

	/**
	 * If this material is derived from and/or represents a vanilla {@code RenderLayer}
	 * and that layer has an associated name (given by Mojang) the name of that layer.
	 * Value is undefined in other cases.
	 *
	 * @return name of associated vanilla {@code RenderLayer} if any, undefined otherwise
	 */
	String renderLayerName();

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
