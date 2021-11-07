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

package io.vram.frex.base.renderer.ao;

import static io.vram.frex.api.math.FrexMathUtil.clampNormalized;
import static io.vram.frex.api.model.util.GeometryUtil.AXIS_ALIGNED_FLAG;
import static io.vram.frex.api.model.util.GeometryUtil.CUBIC_FLAG;
import static io.vram.frex.api.model.util.GeometryUtil.LIGHT_FACE_FLAG;
import static io.vram.frex.base.renderer.ao.AoFaceData.OPAQUE;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import io.vram.frex.api.math.PackedSectionPos;
import io.vram.frex.api.math.PackedVector3f;
import io.vram.frex.api.model.util.ColorUtil;
import io.vram.frex.api.model.util.FaceUtil;
import io.vram.frex.base.renderer.ao.AoFace.WeightFunction;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;
import io.vram.frex.base.renderer.mesh.BaseQuadView;

/**
 * Adaptation of inner, non-static class in BlockModelRenderer that serves same
 * purpose.
 */
public abstract class AoCalculator {
	protected static final float DIVIDE_BY_255 = 1f / 255f;

	//PERF: could be better - or wait for a diff Ao model
	protected static final int BLEND_CACHE_DIVISION = 16;
	protected static final int BLEND_CACHE_DEPTH = BLEND_CACHE_DIVISION - 1;
	protected static final int BLEND_CACHE_ARRAY_SIZE = BLEND_CACHE_DEPTH * 6;
	protected static final int BLEND_INDEX_NO_DEPTH = -1;
	protected static final int BLEND_INDEX_FULL_DEPTH = BLEND_CACHE_DIVISION - 1;
	protected static final int UP = Direction.UP.ordinal();
	protected static final int DOWN = Direction.DOWN.ordinal();
	protected static final int EAST = Direction.EAST.ordinal();
	protected static final int WEST = Direction.WEST.ordinal();
	protected static final int NORTH = Direction.NORTH.ordinal();
	protected static final int SOUTH = Direction.SOUTH.ordinal();

	protected static final int RECIPROCAL_DIVIDE_127_MAGIC = 33026;
	protected static final int RECIPROCAL_DIVIDE_127_SHIFT = 22;

	/**
	 * Fast re-scale of normal values from signed 127 to unsigned 0-255.
	 * See https://www.agner.org/optimize/optimizing_assembly.pdf Sec 16.8 "Division"
	 */
	protected static int scaleNormal255(int base127) {
		return ((Math.abs(base127) * 255 + 1) * RECIPROCAL_DIVIDE_127_MAGIC) >> RECIPROCAL_DIVIDE_127_SHIFT;
	}

	protected final AoFaceCalc[] blendCache = new AoFaceCalc[BLEND_CACHE_ARRAY_SIZE];

	/**
	 * Caches results of {@link #gatherFace(Direction, boolean)} for the current block.
	 */
	protected final AoFaceData[] faceData = new AoFaceData[12];

	/**
	 * Holds per-corner weights - used locally to avoid new allocation.
	 */
	protected final float[] w = new float[4];
	protected long blendCacheCompletionLowFlags;
	protected long blendCacheCompletionHighFlags;
	protected int targetSectionPos;
	protected int targetCacheIndex;

	/**
	 * Indicates which elements of {@link #faceData} have been computed for the current block.
	 */
	protected int completionFlags = 0;

	public AoCalculator() {
		for (int i = 0; i < 12; i++) {
			faceData[i] = new AoFaceData();
		}

		for (int i = 0; i < BLEND_CACHE_ARRAY_SIZE; i++) {
			blendCache[i] = new AoFaceCalc();
		}
	}

	static int blendIndex(int face, float depth) {
		final int depthIndex = Mth.clamp((((int) (depth * BLEND_CACHE_DIVISION * 2 + 1)) >> 1), 1, 15) - 1;
		return face * BLEND_CACHE_DEPTH + depthIndex;
	}

	/* 0 to 255 */
	protected abstract int ao(int cacheIndex);

	protected abstract int brightness(int cacheIndex);

	protected abstract boolean isOpaque(int cacheIndex);

	protected abstract int cacheIndexFromSectionIndex(int packedSectionIndex);

	private boolean checkBlendDirty(int blendIndex) {
		if (blendIndex < 64) {
			final long mask = 1L << blendIndex;

			if ((blendCacheCompletionLowFlags & mask) == 0) {
				blendCacheCompletionLowFlags |= mask;
				return true;
			} else {
				return false;
			}
		} else {
			final long mask = 1L << (blendIndex - 64);

			if ((blendCacheCompletionHighFlags & mask) == 0) {
				blendCacheCompletionHighFlags |= mask;
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Call at start of each new block.
	 *
	 * @param index region-relative index - must be an interior index - for block context, will always be 0
	 */
	public void prepare(int packedSectionIndex) {
		this.targetSectionPos = packedSectionIndex;
		targetCacheIndex = this.cacheIndexFromSectionIndex(packedSectionIndex);

		completionFlags = 0;
		blendCacheCompletionLowFlags = 0;
		blendCacheCompletionHighFlags = 0;
	}

	public void compute(BaseQuadEmitter quad) {
		if (quad.hasVertexNormals()) {
			// these can only be lit this way
			irregularFace(quad);
			return;
		}

		final int flags = quad.geometryFlags();

		switch (flags) {
			case AXIS_ALIGNED_FLAG | CUBIC_FLAG | LIGHT_FACE_FLAG:
			case AXIS_ALIGNED_FLAG | LIGHT_FACE_FLAG:
				blockFace(quad, true);
				break;

			case AXIS_ALIGNED_FLAG | CUBIC_FLAG:
			case AXIS_ALIGNED_FLAG:
				blendedFace(quad);
				break;

			default:
				irregularFace(quad);
				break;
		}
	}

	// PERF: quite bad - essentially pay whole cost of AO on flat lighting
	// needs specialized routines or segregate brightness/AO computation
	public void computeFlat(BaseQuadEmitter quad) {
		final int flags = quad.geometryFlags();

		switch (flags) {
			case AXIS_ALIGNED_FLAG | CUBIC_FLAG | LIGHT_FACE_FLAG:
			case AXIS_ALIGNED_FLAG | LIGHT_FACE_FLAG:
				blockFaceFlat(quad, true);
				break;

			case AXIS_ALIGNED_FLAG | CUBIC_FLAG:
			case AXIS_ALIGNED_FLAG:
				blendedFaceFlat(quad);
				break;

			default:
				irregularFaceFlat(quad);
				break;
		}
	}

	private void blockFace(BaseQuadEmitter quad, boolean isOnLightFace) {
		final int lightFace = quad.lightFaceId();
		final AoFaceCalc faceData = gatherFace(lightFace, isOnLightFace).calc;
		final AoFace face = AoFace.get(lightFace);
		final WeightFunction wFunc = face.weightFunc;
		final float[] w = this.w;
		final float[] ao = quad.ao;

		for (int i = 0; i < 4; i++) {
			wFunc.apply(quad, i, w);
			quad.lightmap(i, ColorUtil.maxBrightness(quad.lightmap(i), faceData.weightedCombinedLight(w)));
			ao[i] = clampNormalized(faceData.weigtedAo(w) * DIVIDE_BY_255);
		}
	}

	private void blockFaceFlat(BaseQuadEmitter quad, boolean isOnLightFace) {
		final int lightFace = quad.lightFaceId();
		final AoFaceCalc faceData = gatherFace(lightFace, isOnLightFace).calc;
		final AoFace face = AoFace.get(lightFace);
		final WeightFunction wFunc = face.weightFunc;
		final float[] w = this.w;

		for (int i = 0; i < 4; i++) {
			wFunc.apply(quad, i, w);
			quad.lightmap(i, ColorUtil.maxBrightness(quad.lightmap(i), faceData.weightedCombinedLight(w)));
		}
	}

	/**
	 * Returns linearly interpolated blend of outer and inner face based on depth of vertex in face.
	 */
	private AoFaceCalc blendedInsetData(BaseQuadView quad, int vertexIndex, int lightFace) {
		final float w1 = AoFace.get(lightFace).depthFunc.apply(quad, vertexIndex);

		if (w1 <= 0.03125f) {
			return gatherFace(lightFace, true).calc;
		} else if (w1 >= 0.96875f) {
			return gatherFace(lightFace, false).calc;
		} else {
			final int blendIndex = blendIndex(lightFace, w1);
			final AoFaceCalc result = blendCache[blendIndex];

			if (checkBlendDirty(blendIndex)) {
				final float w0 = 1 - w1;
				result.weightedMean(
						gatherFace(lightFace, true).calc, w0,
						gatherFace(lightFace, false).calc, w1);
			}

			return result;
		}
	}

	private void blendedFace(BaseQuadEmitter quad) {
		final int lightFace = quad.lightFaceId();
		final AoFaceCalc faceData = blendedInsetData(quad, 0, lightFace);
		final AoFace face = AoFace.get(lightFace);
		final WeightFunction wFunc = face.weightFunc;
		final float[] w = this.w;
		final float[] ao = quad.ao;

		for (int i = 0; i < 4; i++) {
			wFunc.apply(quad, i, w);
			quad.lightmap(i, ColorUtil.maxBrightness(quad.lightmap(i), faceData.weightedCombinedLight(w)));
			ao[i] = clampNormalized(faceData.weigtedAo(w) * DIVIDE_BY_255);
		}
	}

	private void blendedFaceFlat(BaseQuadEmitter quad) {
		final int lightFace = quad.lightFaceId();
		final AoFaceCalc faceData = blendedInsetData(quad, 0, lightFace);
		final AoFace face = AoFace.get(lightFace);
		final WeightFunction wFunc = face.weightFunc;
		final float[] w = this.w;

		for (int i = 0; i < 4; i++) {
			wFunc.apply(quad, i, w);
			quad.lightmap(i, ColorUtil.maxBrightness(quad.lightmap(i), faceData.weightedCombinedLight(w)));
		}
	}

	private void irregularFace(BaseQuadEmitter quad) {
		int normal = 0;
		int rawNormalX = 0, rawNormalY = 0, rawNormalZ = 0;
		int scaledNormalX = 0, scaledNormalY = 0, scaledNormalZ = 0;
		final float[] aoResult = quad.ao;

		for (int i = 0; i < 4; i++) {
			final int vNormal = quad.packedNormal(i);

			if (vNormal != normal) {
				normal = vNormal;
				rawNormalX = PackedVector3f.packedByteX(normal);
				rawNormalY = PackedVector3f.packedByteY(normal);
				rawNormalZ = PackedVector3f.packedByteZ(normal);
				scaledNormalX = scaleNormal255(rawNormalX);
				scaledNormalY = scaleNormal255(rawNormalY);
				scaledNormalZ = scaleNormal255(rawNormalZ);
			}

			int aoSum = 0, aoMax = 0;
			int skySum = 0, blockSum = 0, skyMax = 0, blockMax = 0;

			if (rawNormalX != 0) {
				final int face = rawNormalX > 0 ? EAST : WEST;
				// PERF: really need to cache these
				final AoFaceCalc fd = blendedInsetData(quad, i, face);
				final int weights = AoFace.get(face).fastWeightFunc.apply(quad, i);
				final int normalSq = AoMath.mul(scaledNormalX, scaledNormalX);
				final int ao = fd.weigtedAo(weights);
				final int sky = fd.weightedSkyLight(weights);
				final int block = fd.weightedBlockLight(weights);

				aoSum += normalSq * ao;
				skySum += normalSq * sky;
				blockSum += normalSq * block;
				aoMax = ao;
				skyMax = sky;
				blockMax = block;
			}

			if (rawNormalY != 0) {
				final int face = rawNormalY > 0 ? UP : DOWN;
				final AoFaceCalc fd = blendedInsetData(quad, i, face);
				final int weights = AoFace.get(face).fastWeightFunc.apply(quad, i);
				final int normalSq = AoMath.mul(scaledNormalY, scaledNormalY);
				final int ao = fd.weigtedAo(weights);
				final int sky = fd.weightedSkyLight(weights);
				final int b = fd.weightedBlockLight(weights);

				aoSum += normalSq * ao;
				skySum += normalSq * sky;
				blockSum += normalSq * b;
				aoMax = Math.max(ao, aoMax);
				skyMax = Math.max(sky, skyMax);
				blockMax = Math.max(b, blockMax);
			}

			if (rawNormalZ != 0) {
				final int face = rawNormalZ > 0 ? SOUTH : NORTH;
				final AoFaceCalc fd = blendedInsetData(quad, i, face);
				final int weights = AoFace.get(face).fastWeightFunc.apply(quad, i);
				final int normalSq = AoMath.mul(scaledNormalZ, scaledNormalZ);
				final int ao = fd.weigtedAo(weights);
				final int sky = fd.weightedSkyLight(weights);
				final int block = fd.weightedBlockLight(weights);

				aoSum += normalSq * ao;
				skySum += normalSq * sky;
				blockSum += normalSq * block;
				aoMax = Math.max(ao, aoMax);
				skyMax = Math.max(sky, skyMax);
				blockMax = Math.max(block, blockMax);
			}

			aoSum = (aoSum + AoMath.HALF_VALUE) >> AoMath.UNIT_SHIFT;
			aoSum = (aoSum + aoMax) >> 1;
			assert (aoSum & 0xFF) == aoSum;
			aoResult[i] = aoSum * DIVIDE_BY_255;

			skySum = (skySum + AoMath.HALF_VALUE) >> AoMath.UNIT_SHIFT;
			skySum = (skySum + skyMax) >> 1;

			blockSum = (blockSum + AoMath.HALF_VALUE) >> AoMath.UNIT_SHIFT;
			blockSum = (blockSum + blockMax) >> 1;

			quad.lightmap(i, ColorUtil.maxBrightness(quad.lightmap(i), (skySum << 16) | blockSum));
		}
	}

	private void irregularFaceFlat(BaseQuadEmitter quad) {
		int normal = 0;
		int rawNormalX = 0, rawNormalY = 0, rawNormalZ = 0;
		int scaledNormalX = 0, scaledNormalY = 0, scaledNormalZ = 0;

		for (int i = 0; i < 4; i++) {
			final int vNormal = quad.packedNormal(i);

			if (vNormal != normal) {
				normal = vNormal;
				rawNormalX = PackedVector3f.packedByteX(normal);
				rawNormalY = PackedVector3f.packedByteY(normal);
				rawNormalZ = PackedVector3f.packedByteZ(normal);
				scaledNormalX = scaleNormal255(rawNormalX);
				scaledNormalY = scaleNormal255(rawNormalY);
				scaledNormalZ = scaleNormal255(rawNormalZ);
			}

			int skySum = 0, blockSum = 0, skyMax = 0, blockMax = 0;

			if (rawNormalX != 0) {
				final int face = rawNormalX > 0 ? EAST : WEST;
				// PERF: really need to cache these
				final AoFaceCalc fd = blendedInsetData(quad, i, face);
				final int weights = AoFace.get(face).fastWeightFunc.apply(quad, i);
				final int normalSq = AoMath.mul(scaledNormalX, scaledNormalX);
				final int sky = fd.weightedSkyLight(weights);
				final int block = fd.weightedBlockLight(weights);

				skySum += normalSq * sky;
				blockSum += normalSq * block;
				skyMax = sky;
				blockMax = block;
			}

			if (rawNormalY != 0) {
				final int face = rawNormalY > 0 ? UP : DOWN;
				final AoFaceCalc fd = blendedInsetData(quad, i, face);
				final int weights = AoFace.get(face).fastWeightFunc.apply(quad, i);
				final int normalSq = AoMath.mul(scaledNormalY, scaledNormalY);
				final int sky = fd.weightedSkyLight(weights);
				final int block = fd.weightedBlockLight(weights);

				skySum += normalSq * sky;
				blockSum += normalSq * block;
				skyMax = Math.max(sky, skyMax);
				blockMax = Math.max(block, blockMax);
			}

			if (rawNormalZ != 0) {
				final int face = rawNormalZ > 0 ? SOUTH : NORTH;
				final AoFaceCalc fd = blendedInsetData(quad, i, face);
				final int weights = AoFace.get(face).fastWeightFunc.apply(quad, i);
				final int normalSq = AoMath.mul(scaledNormalZ, scaledNormalZ);
				final int sky = fd.weightedSkyLight(weights);
				final int block = fd.weightedBlockLight(weights);

				skySum += normalSq * sky;
				blockSum += normalSq * block;
				skyMax = Math.max(sky, skyMax);
				blockMax = Math.max(block, blockMax);
			}

			skySum = (skySum + AoMath.HALF_VALUE) >> AoMath.UNIT_SHIFT;
			skySum = (skySum + skyMax) >> 1;

			blockSum = (blockSum + AoMath.HALF_VALUE) >> AoMath.UNIT_SHIFT;
			blockSum = (blockSum + blockMax) >> 1;

			quad.lightmap(i, ColorUtil.maxBrightness(quad.lightmap(i), (skySum << 16) | blockSum));
		}
	}

	/**
	 * Computes smoothed brightness and Ao shading for four corners of a block face.
	 * Outer block face is what you normally see and what you get get when second
	 * parameter is true. Inner is light *within* the block and usually darker. It
	 * is blended with the outer face for inset surfaces, but is also used directly
	 * in vanilla logic for some blocks that aren't full opaque cubes. Except for
	 * parameterization, the logic itself is practically identical to vanilla.
	 */
	private AoFaceData gatherFace(final int lightFace, boolean isOnBlockFace) {
		final int faceDataIndex = isOnBlockFace ? lightFace : (lightFace + 6);
		final int mask = 1 << faceDataIndex;
		final AoFaceData fd = faceData[faceDataIndex];

		if ((completionFlags & mask) == 0) {
			completionFlags |= mask;
			updateFace(fd, lightFace, isOnBlockFace);
		}

		return fd;
	}

	private void updateFace(AoFaceData fd, final int lightFace, boolean isOnBlockFace) {
		int centerSectionPos = targetSectionPos;
		int centerCacheIndex = targetCacheIndex;

		// Overall this is different from vanilla, which seems to be buggy
		// basically, use neighbor pos unless it is full opaque - in that case cheat and use
		// this block's position.
		// A key difference from vanilla is that this position is then used as the center for
		// all following offsets, which avoids anisotropy in smooth lighting.
		if (isOnBlockFace) {
			final int offsetSectionPos = PackedSectionPos.offset(targetSectionPos, FaceUtil.faceFromIndex(lightFace));
			final int offsetCacheIndex = cacheIndexFromSectionIndex(offsetSectionPos);

			if (!isOpaque(offsetCacheIndex)) {
				centerSectionPos = offsetSectionPos;
				centerCacheIndex = offsetCacheIndex;
			}
		}

		fd.center = brightness(centerCacheIndex);
		final int aoCenter = ao(centerCacheIndex);
		fd.aoCenter = aoCenter;

		final AoFace aoFace = AoFace.get(lightFace);

		// vanilla was further offsetting these in the direction of the light face
		// but it was actually mis-sampling and causing visible artifacts in certain situation
		int cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.bottomOffset));
		final boolean bottomClear = !isOpaque(cacheIndex);
		fd.bottom = bottomClear ? brightness(cacheIndex) : OPAQUE;
		final int aoBottom = ao(cacheIndex);
		fd.aoBottom = aoBottom;

		cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.topOffset));
		final boolean topClear = !isOpaque(cacheIndex);
		fd.top = topClear ? brightness(cacheIndex) : OPAQUE;
		final int aoTop = ao(cacheIndex);
		fd.aoTop = aoTop;

		cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.leftOffset));
		final boolean leftClear = !isOpaque(cacheIndex);
		fd.left = leftClear ? brightness(cacheIndex) : OPAQUE;
		final int aoLeft = ao(cacheIndex);
		fd.aoLeft = aoLeft;

		cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.rightOffset));
		final boolean rightClear = !isOpaque(cacheIndex);
		fd.right = rightClear ? brightness(cacheIndex) : OPAQUE;
		final int aoRight = ao(cacheIndex);
		fd.aoRight = aoRight;

		if (!(leftClear || bottomClear)) {
			// both not clear
			fd.aoBottomLeft = (Math.min(aoLeft, aoBottom) + aoBottom + aoLeft + 1 + aoCenter) >> 2;
			fd.bottomLeft = OPAQUE;
		} else { // at least one clear
			cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.bottomLeftOffset));
			final boolean cornerClear = !isOpaque(cacheIndex);
			fd.bottomLeft = cornerClear ? brightness(cacheIndex) : OPAQUE;
			fd.aoBottomLeft = (ao(cacheIndex) + aoBottom + aoCenter + aoLeft + 1) >> 2;  // bitwise divide by four, rounding up
		}

		if (!(rightClear || bottomClear)) {
			// both not clear
			fd.aoBottomRight = (Math.min(aoRight, aoBottom) + aoBottom + aoRight + 1 + aoCenter) >> 2;
			fd.bottomRight = OPAQUE;
		} else { // at least one clear
			cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.bottomRightOffset));
			final boolean cornerClear = !isOpaque(cacheIndex);
			fd.bottomRight = cornerClear ? brightness(cacheIndex) : OPAQUE;
			fd.aoBottomRight = (ao(cacheIndex) + aoBottom + aoCenter + aoRight + 1) >> 2;
		}

		if (!(leftClear || topClear)) {
			// both not clear
			fd.aoTopLeft = (Math.min(aoLeft, aoTop) + aoTop + aoLeft + 1 + aoCenter) >> 2;
			fd.topLeft = OPAQUE;
		} else { // at least one clear
			cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.topLeftOffset));
			final boolean cornerClear = !isOpaque(cacheIndex);
			fd.topLeft = cornerClear ? brightness(cacheIndex) : OPAQUE;
			fd.aoTopLeft = (ao(cacheIndex) + aoTop + aoCenter + aoLeft + 1) >> 2;
		}

		if (!(rightClear || topClear)) {
			// both not clear
			fd.aoTopRight = (Math.min(aoRight, aoTop) + aoTop + aoRight + 1 + aoCenter) >> 2;
			fd.topRight = OPAQUE;
		} else { // at least one clear
			cacheIndex = cacheIndexFromSectionIndex(PackedSectionPos.add(centerSectionPos, aoFace.topRightOffset));
			final boolean cornerClear = !isOpaque(cacheIndex);
			fd.topRight = cornerClear ? brightness(cacheIndex) : OPAQUE;
			fd.aoTopRight = (ao(cacheIndex) + aoTop + aoCenter + aoRight + 1) >> 2;
		}

		fd.calc.compute(fd);
	}
}
