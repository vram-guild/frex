/*
 * Copyright © Original Authors
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

package io.vram.frex.base.client.world;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ForwardingBlockView implements BlockAndTintGetter {
	protected BlockAndTintGetter wrapped;

	public ForwardingBlockView() { }

	public ForwardingBlockView(BlockAndTintGetter wrapped) {
		this.wrapped = wrapped;
	}

	public ForwardingBlockView prepare(BlockAndTintGetter wrapped) {
		this.wrapped = wrapped;
		return this;
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos blockPos) {
		return wrapped.getBlockEntity(blockPos);
	}

	@Override
	public BlockState getBlockState(BlockPos blockPos) {
		return wrapped.getBlockState(blockPos);
	}

	@Override
	public FluidState getFluidState(BlockPos blockPos) {
		return wrapped.getFluidState(blockPos);
	}

	@Override
	public int getHeight() {
		return wrapped.getHeight();
	}

	@Override
	public int getMinBuildHeight() {
		return wrapped.getMaxBuildHeight();
	}

	@Override
	public float getShade(Direction direction, boolean isShaded) {
		return wrapped.getShade(direction, isShaded);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return wrapped.getLightEngine();
	}

	@Override
	public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
		return wrapped.getBlockTint(blockPos, colorResolver);
	}

	@Override
	public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos blockPos, BlockEntityType<T> blockEntityType) {
		return wrapped.getBlockEntity(blockPos, blockEntityType);
	}

	@Override
	public int getLightEmission(BlockPos blockPos) {
		return wrapped.getLightEmission(blockPos);
	}

	@Override
	public int getMaxLightLevel() {
		return wrapped.getMaxLightLevel();
	}

	@Override
	public Stream<BlockState> getBlockStates(AABB aABB) {
		return wrapped.getBlockStates(aABB);
	}

	@Override
	public BlockHitResult isBlockInLine(ClipBlockStateContext clipBlockStateContext) {
		return wrapped.isBlockInLine(clipBlockStateContext);
	}

	@Override
	public BlockHitResult clip(ClipContext clipContext) {
		return wrapped.clip(clipContext);
	}

	@Override
	public BlockHitResult clipWithInteractionOverride(Vec3 vec3, Vec3 vec32, BlockPos blockPos, VoxelShape voxelShape, BlockState blockState) {
		return wrapped.clipWithInteractionOverride(vec3, vec32, blockPos, voxelShape, blockState);
	}

	@Override
	public double getBlockFloorHeight(VoxelShape voxelShape, Supplier<VoxelShape> supplier) {
		return wrapped.getBlockFloorHeight(voxelShape, supplier);
	}

	@Override
	public double getBlockFloorHeight(BlockPos blockPos) {
		return wrapped.getBlockFloorHeight(blockPos);
	}

	@Override
	public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
		return wrapped.getBrightness(lightLayer, blockPos);
	}

	@Override
	public int getRawBrightness(BlockPos blockPos, int i) {
		return wrapped.getRawBrightness(blockPos, i);
	}

	@Override
	public boolean canSeeSky(BlockPos blockPos) {
		return wrapped.canSeeSky(blockPos);
	}

	@Override
	public int getMaxBuildHeight() {
		return wrapped.getMaxBuildHeight();
	}

	@Override
	public int getSectionsCount() {
		return wrapped.getSectionsCount();
	}

	@Override
	public int getMinSection() {
		return wrapped.getMinSection();
	}

	@Override
	public int getMaxSection() {
		return wrapped.getMaxSection();
	}

	@Override
	public boolean isOutsideBuildHeight(BlockPos blockPos) {
		return wrapped.isOutsideBuildHeight(blockPos);
	}

	@Override
	public boolean isOutsideBuildHeight(int i) {
		return wrapped.isOutsideBuildHeight(i);
	}

	@Override
	public int getSectionIndex(int i) {
		return wrapped.getSectionIndex(i);
	}

	@Override
	public int getSectionIndexFromSectionY(int i) {
		return wrapped.getSectionIndexFromSectionY(i);
	}

	@Override
	public int getSectionYFromSectionIndex(int i) {
		return wrapped.getSectionYFromSectionIndex(i);
	}
}
