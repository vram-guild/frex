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

package grondag.frex.api.material;

import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;

import io.vram.frex.api.material.MaterialCondition;

@Deprecated
public interface RenderMaterial extends net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial {
	@Nullable BlendMode blendMode();

	boolean blur();

	MaterialCondition condition();

	boolean cull();

	int cutout();

	int decal();

	int depthTest();

	@Deprecated
	@ScheduledForRemoval
	default boolean disableAo(int spriteIndex) {
		return disableAo();
	}

	boolean disableAo();

	@Deprecated
	@ScheduledForRemoval
	default boolean disableColorIndex(int spriteIndex) {
		return disableColorIndex();
	}

	boolean disableColorIndex();

	@Deprecated
	@ScheduledForRemoval
	default boolean disableDiffuse(int spriteIndex) {
		return disableDiffuse();
	}

	boolean disableDiffuse();

	boolean discardsTexture();

	@Deprecated
	@ScheduledForRemoval
	default boolean emissive(int spriteIndex) {
		return emissive();
	}

	boolean emissive();

	/**
	 * @deprecated No longer used or valid. Always returns true.
	 * Will be removed in a subsequent release.
	 */
	@Deprecated
	@ScheduledForRemoval
	default boolean enableLightmap() {
		return true;
	}

	boolean flashOverlay();

	boolean fog();

	/**
	 * @deprecated No longer used or valid. Always returns false.
	 * Will be removed in a subsequent release.
	 */
	@Deprecated
	@ScheduledForRemoval
	default boolean gui() {
		return false;
	}

	ResourceLocation fragmentShaderId();

	String fragmentShader();

	boolean hurtOverlay();

	boolean lines();

	boolean sorted();

	@Override
	@Deprecated
	@ScheduledForRemoval
	default int spriteDepth() {
		return 1;
	}

	int target();

	ResourceLocation textureId();

	String texture();

	int transparency();

	boolean unmipped();

	ResourceLocation vertexShaderId();

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
