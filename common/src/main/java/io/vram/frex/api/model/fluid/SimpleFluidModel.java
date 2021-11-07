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

package io.vram.frex.api.model.fluid;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.material.RenderMaterial;

/**
 * Implementation of {@link FluidModel}  with vanilla-like geometry.
 */
public class SimpleFluidModel implements FluidModel {
	protected final RenderMaterial material;
	protected final boolean blendColors;
	protected final FluidAppearance appearance;

	public SimpleFluidModel(RenderMaterial material, boolean blendColors, FluidAppearance appearance) {
		this.material = material;
		this.blendColors = blendColors;
		this.appearance = appearance;
	}

	// PERF: pass in normalized UVs so don't have to deinterpolate in renderer

	// WIP: handle degenerate quads by emitting two triangles so that face normals are correct
	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		final var appearance = this.appearance;
		final QuadEmitter qe = output.asQuadEmitter();
		final BlockState blockState = input.blockState();
		final FluidState fluidState = blockState.getFluidState();
		final var world = input.blockView();
		final var centerPos = input.pos();
		final TextureAtlasSprite[] sprites = appearance.getFluidSprites(world, centerPos, fluidState);
		final BlockPos.MutableBlockPos searchPos = SEARCH_POS.get();

		final int centerColor = appearance.getFluidColor(world, centerPos, fluidState) | 0xFF000000;

		final int nwColor, swColor, neColor, seColor;

		if (blendColors) {
			final int n = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.NORTH), fluidState);
			final int w = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.WEST), fluidState);
			final int s = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.SOUTH), fluidState);
			final int e = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.EAST), fluidState);

			final int ne = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.NORTH).move(Direction.EAST), fluidState);
			final int nw = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.NORTH).move(Direction.WEST), fluidState);
			final int se = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.SOUTH).move(Direction.EAST), fluidState);
			final int sw = appearance.getFluidColor(world, searchPos.setWithOffset(centerPos, Direction.SOUTH).move(Direction.WEST), fluidState);

			nwColor = colorMix4(centerColor, n, w, nw) | 0xFF000000;
			swColor = colorMix4(centerColor, s, w, sw) | 0xFF000000;
			neColor = colorMix4(centerColor, n, e, ne) | 0xFF000000;
			seColor = colorMix4(centerColor, s, e, se) | 0xFF000000;
		} else {
			nwColor = centerColor;
			swColor = centerColor;
			neColor = centerColor;
			seColor = centerColor;
		}

		final Fluid fluid = fluidState.getType();
		final boolean isUpVisible = !world.getFluidState(searchPos.setWithOffset(centerPos, Direction.UP)).getType().isSame(fluid);

		final boolean isDownVisible = (!isSideBlocked(world, Direction.DOWN.getOpposite(), 1.0F, centerPos, blockState)
			&& !world.getFluidState(searchPos.setWithOffset(centerPos, Direction.DOWN)).getType().isSame(fluid))
			&& !isSideBlocked(world, searchPos.setWithOffset(centerPos, Direction.DOWN), Direction.DOWN, 0.8888889F);

		final boolean isNorthVisible = (!isSideBlocked(world, Direction.NORTH.getOpposite(), 1.0F, centerPos, blockState)
			&& !world.getFluidState(searchPos.setWithOffset(centerPos, Direction.NORTH)).getType().isSame(fluid));

		final boolean isSouthVisible = (!isSideBlocked(world, Direction.SOUTH.getOpposite(), 1.0F, centerPos, blockState)
			&& !world.getFluidState(searchPos.setWithOffset(centerPos, Direction.SOUTH)).getType().isSame(fluid));

		final boolean isWestVisible = (!isSideBlocked(world, Direction.WEST.getOpposite(), 1.0F, centerPos, blockState)
			&& !world.getFluidState(searchPos.setWithOffset(centerPos, Direction.WEST)).getType().isSame(fluid));

		final boolean isEastVisible = (!isSideBlocked(world, Direction.EAST.getOpposite(), 1.0F, centerPos, blockState)
			&& !world.getFluidState(searchPos.setWithOffset(centerPos, Direction.EAST)).getType().isSame(fluid));

		if (isUpVisible || isDownVisible || isEastVisible || isWestVisible || isNorthVisible || isSouthVisible) {
			final TextureAtlasSprite stillSprite = sprites[0];
			float centerNwHeight = nwHeight(world, searchPos.set(centerPos), fluid);
			float southNwHeight = nwHeight(world, searchPos.setWithOffset(centerPos, Direction.SOUTH), fluid);
			float southEastNwHeight = nwHeight(world, searchPos.setWithOffset(centerPos, Direction.SOUTH).move(Direction.EAST), fluid);
			float eastNwHeight = nwHeight(world, searchPos.setWithOffset(centerPos, Direction.EAST), fluid);
			final float downBasedOffset = isDownVisible ? 0.001F : 0.0F;

			if (isUpVisible && !isSideBlocked(world, searchPos.setWithOffset(centerPos, Direction.UP), Direction.UP, Math.min(Math.min(centerNwHeight, southNwHeight), Math.min(southEastNwHeight, eastNwHeight)))) {
				centerNwHeight -= 0.001F;
				southNwHeight -= 0.001F;
				southEastNwHeight -= 0.001F;
				eastNwHeight -= 0.001F;
				final Vec3 velocity = fluidState.getFlow(world, centerPos);
				TextureAtlasSprite topSprite;

				float u0, u1, u2, u3, v0, v1, v2, v3;

				if (velocity.x == 0.0D && velocity.z == 0.0D) {
					topSprite = stillSprite;
					u0 = 0f;
					v0 = 0f;
					u1 = 0f;
					v1 = 1f;
					u2 = 1f;
					v2 = 1f;
					u3 = 1f;
					v3 = 0f;
				} else {
					topSprite = sprites[1];
					final float angle = (float) Mth.atan2(velocity.z, velocity.x) - 1.5707964F;
					final float dx = Mth.sin(angle) * 0.25F;
					final float dy = Mth.cos(angle) * 0.25F;
					u0 = 0.5F + -dy - dx;
					v0 = 0.5F + -dy + dx;
					u1 = 0.5F + -dy + dx;
					v1 = 0.5F + dy + dx;
					u2 = 0.5F + dy + dx;
					v2 = 0.5F + dy - dx;
					u3 = 0.5F + dy - dx;
					v3 = 0.5F + -dy - dx;
				}

				final float uCentroid = (u0 + u1 + u2 + u3) * 0.25F;
				final float vCentroid = (v0 + v1 + v2 + v3) * 0.25F;

				final float dx = stillSprite.getWidth() / (stillSprite.getU1() - stillSprite.getU0());
				final float dy = stillSprite.getHeight() / (stillSprite.getV1() - stillSprite.getV0());
				final float centerScale = 4.0F / Math.max(dy, dx);

				u0 = Mth.lerp(centerScale, u0, uCentroid);
				u1 = Mth.lerp(centerScale, u1, uCentroid);
				u2 = Mth.lerp(centerScale, u2, uCentroid);
				u3 = Mth.lerp(centerScale, u3, uCentroid);
				v0 = Mth.lerp(centerScale, v0, vCentroid);
				v1 = Mth.lerp(centerScale, v1, vCentroid);
				v2 = Mth.lerp(centerScale, v2, vCentroid);
				v3 = Mth.lerp(centerScale, v3, vCentroid);

				qe.pos(0, 0, centerNwHeight, 0)
				.pos(1, 0, southNwHeight, 1)
				.pos(2, 1, southEastNwHeight, 1)
				.pos(3, 1, eastNwHeight, 0)
				.uvSprite(topSprite, u0, v0, u1, v1, u2, v2, u3, v3)
				.vertexColor(nwColor, swColor, seColor, neColor)
				.material(material).emit();

				// backface
				if (fluidState.shouldRenderBackwardUpFace(world, searchPos.setWithOffset(centerPos, Direction.UP))) {
					qe.pos(0, 0, centerNwHeight, 0)
					.pos(1, 1, eastNwHeight, 0)
					.pos(2, 1, southEastNwHeight, 1)
					.pos(3, 0, southNwHeight, 1)
					.uvSprite(topSprite, u0, v0, u3, v3, u2, v2, u1, v1)
					.vertexColor(nwColor, neColor, seColor, swColor)
					.material(material).emit();
				}
			}

			if (isDownVisible) {
				float u0, u1, v1, v0;

				u0 = stillSprite.getU0();
				u1 = stillSprite.getU1();
				v1 = stillSprite.getV0();
				v0 = stillSprite.getV1();

				qe.pos(0, 0, downBasedOffset, 1).uv(0, u0, v0).vertexColor(0, swColor)
				.pos(1, 0, downBasedOffset, 0).uv(1, u0, v1).vertexColor(1, nwColor)
				.pos(2, 1, downBasedOffset, 0).uv(2, u1, v1).vertexColor(2, neColor)
				.pos(3, 1, downBasedOffset, 1).uv(3, u1, v0).vertexColor(3, seColor)
				.material(material).emit();
			}

			for (int sideIndex = 0; sideIndex < 4; ++sideIndex) {
				float y0, y1, x0, x1, z0, z1;
				Direction face;
				int c0, c1;

				if (sideIndex == 0) {
					if (!isNorthVisible) continue;
					y0 = centerNwHeight;
					y1 = eastNwHeight;
					c0 = nwColor;
					c1 = neColor;
					x0 = 0;
					x1 = 1;
					z0 = 0.0010000000474974513F;
					z1 = 0.0010000000474974513F;
					face = Direction.NORTH;
				} else if (sideIndex == 1) {
					if (!isSouthVisible) continue;
					face = Direction.SOUTH;
					y0 = southEastNwHeight;
					y1 = southNwHeight;
					c0 = seColor;
					c1 = swColor;
					x0 = 1F;
					x1 = 0;
					z0 = 1.0F - 0.0010000000474974513F;
					z1 = 1.0F - 0.0010000000474974513F;
				} else if (sideIndex == 2) {
					if (!isWestVisible) continue;
					face = Direction.WEST;
					y0 = southNwHeight;
					y1 = centerNwHeight;
					c0 = swColor;
					c1 = nwColor;
					x0 = 0.0010000000474974513F;
					x1 = 0.0010000000474974513F;
					z0 = 1.0F;
					z1 = 0;
				} else {
					if (!isEastVisible) continue;
					face = Direction.EAST;
					y0 = eastNwHeight;
					y1 = southEastNwHeight;
					c0 = neColor;
					c1 = seColor;
					x0 = 1.0F - 0.0010000000474974513F;
					x1 = 1.0F - 0.0010000000474974513F;
					z0 = 0;
					z1 = 1F;
				}

				if (!isSideBlocked(world, searchPos.setWithOffset(centerPos, face), face, Math.max(y0, y1))) {
					final boolean overlay;
					final TextureAtlasSprite sideSprite;

					if (sprites.length >= 3) {
						final Block sideBlock = world.getBlockState(searchPos.setWithOffset(centerPos, face)).getBlock();

						if (sideBlock instanceof HalfTransparentBlock || sideBlock instanceof LeavesBlock) {
							sideSprite = sprites[2];
							overlay = true;
						} else {
							sideSprite = sprites[1];
							overlay = false;
						}
					} else {
						sideSprite = sprites[1];
						overlay = false;
					}

					final float u0 = sideSprite.getU(0.0D);
					final float u1 = sideSprite.getU(8.0D);
					final float v0 = sideSprite.getV((1.0F - y0) * 16.0F * 0.5F);
					final float v1 = sideSprite.getV((1.0F - y1) * 16.0F * 0.5F);
					final float vCenter = sideSprite.getV(8.0D);

					qe.pos(0, x0, y0, z0).uv(0, u0, v0).vertexColor(0, c0)
					.pos(1, x1, y1, z1).uv(1, u1, v1).vertexColor(1, c1)
					.pos(2, x1, downBasedOffset, z1).uv(2, u1, vCenter).vertexColor(2, c1)
					.pos(3, x0, downBasedOffset, z0).uv(3, u0, vCenter).vertexColor(3, c0)
					.material(material).emit();

					if (!overlay) {
						qe.pos(0, x0, downBasedOffset, z0).uv(0, u0, vCenter).vertexColor(0, c0)
						.pos(1, x1, downBasedOffset, z1).uv(1, u1, vCenter).vertexColor(1, c1)
						.pos(2, x1, y1, z1).uv(2, u1, v1).vertexColor(2, c1)
						.pos(3, x0, y0, z0).uv(3, u0, v0).vertexColor(3, c0)
						.material(material).emit();
					}
				}
			}
		}
	}

	private static boolean isSideBlocked(BlockGetter blockView, Direction direction, float height, BlockPos blockPos, BlockState blockState) {
		if (blockState.canOcclude()) {
			final VoxelShape voxelShape = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, height, 1.0D);
			final VoxelShape voxelShape2 = blockState.getOcclusionShape(blockView, blockPos);
			return Shapes.blockOccudes(voxelShape, voxelShape2, direction);
		} else {
			return false;
		}
	}

	private static boolean isSideBlocked(BlockGetter world, BlockPos pos, Direction direction, float height) {
		return isSideBlocked(world, direction, height, pos, world.getBlockState(pos));
	}

	private static float nwHeight(BlockGetter world, BlockPos.MutableBlockPos searchPos, Fluid fluid) {
		final long posIn = searchPos.asLong();

		int w = 0;
		float h = 0.0F;

		for (int j = 0; j < 4; ++j) {
			//  set to y+1 first for up check
			searchPos.set(posIn).move(-(j & 1), 1, -(j >> 1 & 1));

			if (world.getFluidState(searchPos).getType().isSame(fluid)) {
				return 1.0F;
			}

			// didn't find at up, lower to target pos
			searchPos.move(0, -1, 0);

			final FluidState fluidState = world.getFluidState(searchPos);

			if (fluidState.getType().isSame(fluid)) {
				final float g = fluidState.getHeight(world, searchPos);

				if (g >= 0.8F) {
					h += g * 10.0F;
					w += 10;
				} else {
					h += g;
					++w;
				}
			} else if (!world.getBlockState(searchPos).getMaterial().isSolid()) {
				++w;
			}
		}

		return h / w;
	}

	public static int colorMix4(int a, int b, int c, int d) {
		final int blue = (((a & 0xFF) + (b & 0xFF) + (c & 0xFF) + (d & 0xFF) + 1) >> 2) & 0xFF;
		final int green = (((a & 0xFF00) + (b & 0xFF00) + (c & 0xFF00) + (d & 0xFF00) + 0x100) >> 2) & 0xFF00;
		final int red = (((a & 0xFF0000) + (b & 0xFF0000) + (c & 0xFF0000) + (d & 0xFF0000) + 0x10000) >> 2) & 0xFF0000;
		return red | green | blue;
	}

	private static final ThreadLocal<BlockPos.MutableBlockPos> SEARCH_POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
}
