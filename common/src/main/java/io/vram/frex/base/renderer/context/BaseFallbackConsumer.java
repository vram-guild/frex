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

package io.vram.frex.base.renderer.context;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.mesh.QuadEditor;
import io.vram.frex.api.model.BakedInputContext;
import io.vram.frex.api.model.ModelOuputContext;
import io.vram.frex.api.model.util.FaceUtil;
import io.vram.frex.api.renderer.Renderer;

/**
 * Consumer for vanilla baked models. Generally intended to give visual results matching a vanilla render,
 * however there could be subtle (and desirable) lighting variations so is good to be able to render
 * everything consistently.
 *
 * <p>Also, the API allows multi-part models that hold multiple vanilla models to render them without
 * combining quad lists, but the vanilla logic only handles one model per block. To route all of
 * them through vanilla logic would require additional hooks.
 *
 * <p>Works by copying the quad data to an "editor" quad held in the instance(),
 * where all transformations are applied before buffering. Transformations should be
 * the same as they would be in a vanilla render - the editor is serving mainly
 * as a way to access vertex data without magical numbers. It also allows a consistent interface
 * for downstream tesselation routines.
 *
 * <p>Another difference from vanilla render is that all transformation happens before the
 * vertex data is sent to the byte buffer.  Generally POJO array access will be faster than
 * manipulating the data via NIO.
 */
public class BaseFallbackConsumer {
	protected static final RenderMaterial FLAT_MATERIAL;
	protected static final RenderMaterial SHADED_MATERIAL;
	protected static final RenderMaterial AO_FLAT_MATERIAL;
	protected static final RenderMaterial AO_SHADED_MATERIAL;

	static {
		final MaterialFinder finder = Renderer.get().materialFinder();
		FLAT_MATERIAL = finder.clear().preset(MaterialConstants.PRESET_DEFAULT).disableDiffuse(true).disableAo(true).find();
		SHADED_MATERIAL = finder.clear().preset(MaterialConstants.PRESET_DEFAULT).disableAo(true).find();
		AO_FLAT_MATERIAL = finder.clear().preset(MaterialConstants.PRESET_DEFAULT).disableDiffuse(true).find();
		AO_SHADED_MATERIAL = finder.clear().preset(MaterialConstants.PRESET_DEFAULT).find();
	}

	public static void accept(BakedModel model, BakedInputContext input, ModelOuputContext output) {
		final var blockState = input.blockState();
		final var random = input.random();
		final boolean useAo = blockState != null && model.useAmbientOcclusion() && blockState.getLightEmission() == 0 && Minecraft.useAmbientOcclusion();
		final var qe = output.quadEmitter();

		var quads = model.getQuads(blockState, Direction.DOWN, random);
		if (!quads.isEmpty() && input.cullTest(FaceUtil.DOWN_INDEX)) acceptFaceQuads(FaceUtil.DOWN_INDEX, useAo, quads, qe);

		quads = model.getQuads(blockState, Direction.UP, random);
		if (!quads.isEmpty() && input.cullTest(FaceUtil.UP_INDEX)) acceptFaceQuads(FaceUtil.UP_INDEX, useAo, quads, qe);

		quads = model.getQuads(blockState, Direction.NORTH, random);
		if (!quads.isEmpty() && input.cullTest(FaceUtil.NORTH_INDEX)) acceptFaceQuads(FaceUtil.NORTH_INDEX, useAo, quads, qe);

		quads = model.getQuads(blockState, Direction.SOUTH, random);
		if (!quads.isEmpty() && input.cullTest(FaceUtil.SOUTH_INDEX)) acceptFaceQuads(FaceUtil.SOUTH_INDEX, useAo, quads, qe);

		quads = model.getQuads(blockState, Direction.WEST, random);
		if (!quads.isEmpty() && input.cullTest(FaceUtil.WEST_INDEX)) acceptFaceQuads(FaceUtil.WEST_INDEX, useAo, quads, qe);

		quads = model.getQuads(blockState, Direction.EAST, random);
		if (!quads.isEmpty() && input.cullTest(FaceUtil.EAST_INDEX)) acceptFaceQuads(FaceUtil.EAST_INDEX, useAo, quads, qe);

		acceptInsideQuads(useAo, model.getQuads(blockState, null, random), qe);
	}

	protected static void acceptFaceQuads(int faceIndex, boolean useAo, List<BakedQuad> quads, QuadEditor qe) {
		final int count = quads.size();

		for (int j = 0; j < count; j++) {
			final BakedQuad q = quads.get(j);
			qe.fromVanilla(q, q.isShade() ? (useAo ? AO_SHADED_MATERIAL : SHADED_MATERIAL) : (useAo ? AO_FLAT_MATERIAL : FLAT_MATERIAL), faceIndex).emit();
		}
	}

	protected static void acceptInsideQuads(boolean useAo, List<BakedQuad> quads, QuadEditor qe) {
		final int count = quads.size();

		for (int j = 0; j < count; j++) {
			final BakedQuad q = quads.get(j);
			qe.fromVanilla(q, q.isShade() ? (useAo ? AO_SHADED_MATERIAL : SHADED_MATERIAL) : (useAo ? AO_FLAT_MATERIAL : FLAT_MATERIAL), FaceUtil.UNASSIGNED_INDEX).emit();
		}
	}
}
