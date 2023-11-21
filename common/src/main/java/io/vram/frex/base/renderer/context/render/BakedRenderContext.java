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

package io.vram.frex.base.renderer.context.render;

import net.minecraft.client.renderer.texture.OverlayTexture;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.base.renderer.context.input.BaseBakedInputContext;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;

/**
 * Base class for block and item contexts, or "baked" models.
 *
 * <p>Baked models generally require CPU-side color modification
 * for block/item tinting. For blocks in vanilla-like lighting
 * models, diffuse and AO shading are also computed and applied
 * CPU side.  In terrain, face culling based on neighbor blocks
 * is used to reduce polygon count.
 */
public abstract class BakedRenderContext<C extends BaseBakedInputContext> extends BaseRenderContext<C> {
	/**
	 * Applies CPU-side color, which should generally always include
	 * block/item tint and diffuse/AO shading for blocks (unless those
	 * are handled in GPU.)
	 *
	 *
	 * <p>To be called from {@link #renderQuad()} during model output phase.
	 * Should be invoked after material is final because results
	 * are material-dependent.
	 *
	 * <p>No base implementation is provided because shading is highly
	 * dependent on context and lighting model.
	 */
	protected abstract void shadeQuad();

	/**
	 * Applies defaults that are context-sensitive. This covers a handful
	 * of material attributes that should differ from their specification
	 * needed to emulate vanilla behavior. This includes enchantment glint,
	 * flash/health overlay and GUI-specific shading.
	 *
	 * <p>These should be applied both before material transforms so that
	 * transforms have the ability to change them.
	 *
	 * <p>It would improve the API if these material attributes had tri-state
	 * values so that material specification could override the default vanilla
	 * behavior if desired - for example, making some parts of an enchanted item
	 * appear without glint.  If that improvement is made, this routine will have
	 * to be updated to honor those new values.
	 */
	protected void applyMaterialDefaults() {
		// See io.vram.frex.api.material.MaterialFinder#overlay(int, int) javadoc
		if (inputContext.overlay() != OverlayTexture.NO_OVERLAY) {
			final int uv = inputContext.overlay();
			final int v = uv >> 16 & '\uffff';

			if (finder.hurtOverlayIsDefault()) {
				finder.hurtOverlay(v == 3);
			}

			if (finder.flashOverlayIsDefault()) {
				final int u = uv & '\uffff';
				finder.flashOverlay(v == 10 && u > 7);
			}
		}
	}

	/**
	 * Applies preset if present.  Preset resolution is context-sensitive.
	 * This makes material attributes related to presets fully specified.
	 * Should be applied both before and after material transforms.
	 */
	protected void resolvePreset() {
		int preset = finder.preset();

		if (preset == MaterialConstants.PRESET_NONE) {
			return;
		}

		if (preset == MaterialConstants.PRESET_DEFAULT) {
			preset = inputContext.defaultPreset();
		}

		switch (preset) {
			case MaterialConstants.PRESET_CUTOUT: {
				finder.transparency(MaterialConstants.TRANSPARENCY_NONE)
						.cutout(MaterialConstants.CUTOUT_HALF)
						.unmipped(true)
						.target(MaterialConstants.TARGET_SOLID)
						.sorted(false);
				break;
			}
			case MaterialConstants.PRESET_CUTOUT_MIPPED:
				finder
						.transparency(MaterialConstants.TRANSPARENCY_NONE)
						.cutout(MaterialConstants.CUTOUT_HALF)
						.unmipped(false)
						.target(MaterialConstants.TARGET_SOLID)
						.sorted(false);
				break;
			case MaterialConstants.PRESET_TRANSLUCENT:
				finder.transparency(MaterialConstants.TRANSPARENCY_TRANSLUCENT)
						.cutout(MaterialConstants.CUTOUT_NONE)
						.unmipped(false)
						.target(MaterialConstants.TARGET_TRANSLUCENT)
						.sorted(true);
				break;
			case MaterialConstants.PRESET_SOLID:
				finder.transparency(MaterialConstants.TRANSPARENCY_NONE)
						.cutout(MaterialConstants.CUTOUT_NONE)
						.unmipped(false)
						.target(MaterialConstants.TARGET_SOLID)
						.sorted(false);
				break;
			default:
				assert false : "Unhandled preset";
		}

		finder.preset(MaterialConstants.PRESET_NONE);
	}

	/**
	 * Adjustments made for the renderer implementation. For example, to disable
	 * or force material properties to match limitations/expectations of a given context.
	 */
	protected void adjustMaterialForEncoding() { }

	@Override
	public void renderQuad() {
		final BaseQuadEmitter quad = emitter;

		if (inputContext.cullTest(quad.cullFaceId())) {
			finder.copyFrom(quad.material());
			applyMaterialDefaults();
			resolvePreset();
			mapMaterials();
			resolvePreset();
			adjustMaterialForEncoding();
			quad.material(finder.find());
			shadeQuad();
			encodeQuad();
		}
	}
}
