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
	 * <p>The base implementation is suitable for most non-terrain
	 * scenarios with vanilla-style lighting models.
	 */
	protected void shadeQuad() {
		emitter.applyFlatLighting(inputContext.flatBrightness(emitter));
		emitter.colorize(inputContext);
	}

	protected abstract void adjustMaterial();

	@Override
	public void renderQuad() {
		final BaseQuadEmitter quad = emitter;

		if (inputContext.cullTest(quad.cullFaceId())) {
			finder.copyFrom(quad.material());
			mapMaterials();
			adjustMaterial();
			quad.material(finder.find());
			shadeQuad();
			encodeQuad();
		}
	}
}
