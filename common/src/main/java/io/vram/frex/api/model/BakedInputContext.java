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

package io.vram.frex.api.model;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.model.util.FaceUtil;
import io.vram.frex.api.rendertype.RenderTypeUtil;

public interface BakedInputContext extends InputContext {
	/**
	 * The vanilla baked model instance that should be queried for model-level
	 * attributes that control model shading and output in some scenarios. These include:
	 * <ul><li>{@link BakedModel#isGui3d()}</li>
	 * <li>{@link BakedModel#useAmbientOcclusion()}</li>
	 * <li>{@link BakedModel#usesBlockLight()}</li>
	 * <li>{@link BakedModel#isCustomRenderer()}</li>
	 * <li>{@link BakedModel#getTransforms()}</li></ul>
	 *
	 * <p>All block and item models that are registered must also implement {@code BakedModel},
	 * otherwise they cannot be registered and some vanilla and modded code will break.
	 * As a result, in most cases, {@link #bakedModel()} will be the same instance of
	 * {@code ItemModel} or {@code BlockModel} that is currently being rendered.
	 *
	 * <p>However, there is no requirement or guarantee that this instance is also
	 * the model instance currently being rendered. It should not be used to generate
	 * quads - it's only valid usage is to query model-level attributes.
	 *
	 * <p>Note that many different model functions could reside within the same {@code BakedModel}
	 * instance, chosen and executed dynamically from top-level model functions.
	 * The only requirement is that they share the same model-level characteristics.
	 *
	 * <p>Will be {@code null} for fluid models, because they never have an associated BakedModel.
	 *
	 * @return The baked model instance that will provide model-level attributes for
	 * the model currently being rendered. Returns {@code null} for fluid models.
	 */
	@Nullable BakedModel bakedModel();

	/**
	 * Will be null for pure item models.  Will be AIR blockstate for the strange
	 * case of item frames, which have a block-like JSON
	 * model but are entities and thus have no block state.
	 */
	default @Nullable BlockState blockState() {
		return null;
	}

	default @Nullable BlockPos pos() {
		return null;
	}

	boolean cullTest(int faceId);

	default boolean cullTest(Direction face) {
		return cullTest(FaceUtil.toFaceIndex(face));
	}

	int indexedColor(int colorIndex);

	RenderType defaultRenderType();

	/**
	 * The {@link RenderMaterial#preset()} value that will be substituted
	 * for materials where {@link RenderMaterial#preset()} == {@link MaterialConstants#PRESET_DEFAULT}.
	 *
	 * <p>This is useful for renderers (which need to do this translation before buffering a quad)
	 * and also potentially useful for mods that transform other models and need to inspect
	 * transparency and cutout.  Note that if {@link RenderMaterial#preset()} == {@link MaterialConstants#PRESET_NONE}
	 * then the material is already fully specified - whatever values are present for {@link RenderMaterial#transparency()}
	 * and {@link RenderMaterial#cutout()} are already accurate.
	 *
	 * <p>The same information could be obtained by getting the Block or Item render type from {@code ItemBlockRenderTypes}
	 * and then passing that to {@link RenderTypeUtil#inferPreset(net.minecraft.client.renderer.RenderType)} but
	 * that would create needless overhead because the renderer already needs this information.
	 */
	int defaultPreset();
}
