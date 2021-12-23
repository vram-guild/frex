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

package io.vram.frex.base.renderer.context.input;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.api.model.BlockModel.BlockInputContext;
import io.vram.frex.api.model.util.FaceUtil;
import io.vram.frex.api.model.util.GeometryUtil;
import io.vram.frex.api.rendertype.RenderTypeUtil;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;
import io.vram.frex.base.renderer.mesh.MeshEncodingHelper;

public class BaseBlockInputContext<T extends BlockAndTintGetter> extends BaseBakedInputContext implements BlockInputContext {
	protected final BlockColors blockColorMap = Minecraft.getInstance().getBlockColors();
	protected final BlockPos.MutableBlockPos internalSearchPos = new BlockPos.MutableBlockPos();

	protected T blockView;
	protected BlockPos blockPos;
	protected BlockState blockState;
	protected long seed;
	protected int lastColorIndex = -1;
	protected int blockColor = -1;
	protected int fullCubeCache = 0;

	protected boolean enableCulling = true;
	protected int cullCompletionFlags;
	protected int cullResultFlags;
	protected boolean isFluidModel = false;

	public BaseBlockInputContext() {
		super(Type.BLOCK);
	}

	public void prepareForWorld(T blockView, boolean enableCulling, MatrixStack matrixStack) {
		this.blockView = blockView;
		this.enableCulling = enableCulling;
		setMatrixStack(matrixStack);
	}

	public void setWorld(T blockView) {
		this.blockView = blockView;
	}

	public void release() {
		blockView = null;
		matrixStack = null;
		bakedModel = null;
	}

	/**
	 * @param blockState
	 * @param blockPos
	 * @param modelAO
	 * @param seed       pass -1 for default behavior
	 * @param overlay
	 */
	public void prepareForBlock(BakedModel bakedModel, BlockState blockState, BlockPos blockPos, long seed, int overlay) {
		super.prepare(overlay);
		this.seed = seed;
		prepareCommon(bakedModel, blockState, blockPos);
		isFluidModel = false;
	}

	public void prepareForBlock(BakedModel bakedModel, BlockState blockState, BlockPos blockPos) {
		super.prepare(OverlayTexture.NO_OVERLAY);
		this.seed = -1L;
		prepareCommon(bakedModel, blockState, blockPos);
		isFluidModel = false;
	}

	public void prepareForFluid(BlockState blockState, BlockPos blockPos) {
		super.prepare(OverlayTexture.NO_OVERLAY);
		this.seed = -1L;
		prepareCommon(null, blockState, blockPos);
		isFluidModel = true;
	}

	protected void prepareCommon(BakedModel bakedModel, BlockState blockState, BlockPos blockPos) {
		this.bakedModel = bakedModel;
		this.blockState = blockState;
		this.blockPos = blockPos;
		this.seed = -1L;
		defaultRenderType = null;
		lastColorIndex = -1;
		fullCubeCache = 0;
		cullCompletionFlags = 0;
		cullResultFlags = 0;
	}

	@Override
	protected long randomSeed() {
		long result = seed;

		if (result == -1L) {
			result = blockState.getSeed(blockPos);
			seed = result;
		}

		return result;
	}

	@Override
	public BlockAndTintGetter blockView() {
		return blockView;
	}

	@Override
	public BlockState blockState() {
		return blockState;
	}

	@Override
	public BlockPos pos() {
		return blockPos;
	}

	@Override
	public boolean cullTest(int faceIndex) {
		if (faceIndex == FaceUtil.UNASSIGNED_INDEX || !enableCulling) {
			return true;
		}

		final int mask = 1 << faceIndex;

		if ((cullCompletionFlags & mask) == 0) {
			cullCompletionFlags |= mask;
			final Direction face = FaceUtil.faceFromIndex(faceIndex);

			if (Block.shouldRenderFace(blockState, blockView, blockPos, face, internalSearchPos.setWithOffset(blockPos, face))) {
				cullResultFlags |= mask;
				return true;
			} else {
				return false;
			}
		} else {
			return (cullResultFlags & mask) != 0;
		}
	}

	@Override
	public @Nullable Object blockEntityRenderData(BlockPos pos) {
		return null;
	}

	@Override
	public int indexedColor(int colorIndex) {
		if (colorIndex == -1) {
			return -1;
		} else if (lastColorIndex == colorIndex) {
			return blockColor;
		} else {
			lastColorIndex = colorIndex;
			final int result = 0xFF000000 | blockColorMap.getColor(blockState, blockView, blockPos, colorIndex);
			blockColor = result;
			return result;
		}
	}

	public boolean isFullCube() {
		if (fullCubeCache == 0) {
			fullCubeCache = Block.isShapeFullBlock(blockState.getCollisionShape(blockView, blockPos)) ? 1 : -1;
		}

		return fullCubeCache == 1;
	}

	@Override
	public int flatBrightness(BaseQuadEmitter quad) {
		/**
		 * Handles geometry-based check for using self brightness or neighbor brightness.
		 * That logic only applies in flat lighting.
		 */
		if (blockState.emissiveRendering(blockView, blockPos)) {
			return MeshEncodingHelper.FULL_BRIGHTNESS;
		}

		internalSearchPos.set(blockPos);

		// To mirror Vanilla's behavior, if the face has a cull-face, always sample the light value
		// offset in that direction. See net.minecraft.client.render.block.BlockModelRenderer.renderFlat
		// for reference.
		if (quad.cullFaceId() != FaceUtil.UNASSIGNED_INDEX) {
			internalSearchPos.move(quad.cullFace());
		} else if ((quad.geometryFlags() & GeometryUtil.LIGHT_FACE_FLAG) != 0 || isFullCube()) {
			internalSearchPos.move(quad.lightFace());
		}

		return fastBrightness(internalSearchPos);
	}

	protected int fastBrightness(BlockPos pos) {
		return LevelRenderer.getLightColor(blockView, blockState, pos);
	}

	@Override
	public boolean isFluidModel() {
		return isFluidModel;
	}

	@Override
	protected void computeDefaultRenderType() {
		if (defaultRenderType == null) {
			final var renderType = isFluidModel
					? ItemBlockRenderTypes.getRenderLayer(blockState.getFluidState())
					: ItemBlockRenderTypes.getChunkRenderType(blockState);
			defaultRenderType = renderType;
			defaultPreset = RenderTypeUtil.inferPreset(renderType);
		}
	}
}
