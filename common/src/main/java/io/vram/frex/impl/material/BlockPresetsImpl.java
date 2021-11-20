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

package io.vram.frex.impl.material;

import java.util.function.BiConsumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import io.vram.frex.api.material.MaterialConstants;

@Internal
public class BlockPresetsImpl {
	private BlockPresetsImpl() { }

	private static Object2ObjectOpenHashMap<Block, RenderType> blockMap = new Object2ObjectOpenHashMap<>();
	private static Object2ObjectOpenHashMap<Fluid, RenderType> fluidMap = new Object2ObjectOpenHashMap<>();

	private static BiConsumer<Block, RenderType> blockConsumer = (b, l) -> blockMap.put(b, l);
	private static BiConsumer<Fluid, RenderType> fluidConsumer = (f, b) -> fluidMap.put(f, b);

	public static void setConsumers(BiConsumer<Block, RenderType> blockConsumerIn, BiConsumer<Fluid, RenderType> fluidConsumerIn) {
		blockConsumer = blockConsumerIn;
		fluidConsumer = fluidConsumerIn;

		blockMap.forEach(blockConsumerIn);
		fluidMap.forEach(fluidConsumerIn);

		blockMap = null;
		fluidMap = null;
	}

	private static RenderType getRenderType (int preset) {
		switch (preset) {
			case MaterialConstants.PRESET_SOLID:
				return RenderType.solid();
			case MaterialConstants.PRESET_CUTOUT:
				return RenderType.cutout();
			case MaterialConstants.PRESET_CUTOUT_MIPPED:
				return RenderType.cutoutMipped();
			case MaterialConstants.PRESET_TRANSLUCENT:
				return RenderType.translucent();
			default:
				throw new IllegalArgumentException("Invalid preset in render type mapping request.");
		}
	}

	public static void mapBlocks(RenderType renderType, Block[] blocks) {
		for (final Block block : blocks) {
			if (block == null) throw new IllegalArgumentException("Request to map render type for null block.");
			if (renderType == null) throw new IllegalArgumentException("Request to map block " + block.toString() + " to null render type");
			blockConsumer.accept(block, renderType);
		}
	}

	public static void mapBlocks(int preset, Block[] blocks) {
		mapBlocks(getRenderType(preset), blocks);
	}

	public static void mapFluids(RenderType renderType, Fluid[] fluids) {
		for (final Fluid fluid : fluids) {
			if (fluid == null) throw new IllegalArgumentException("Request to map render type for null fluid.");
			if (renderType == null) throw new IllegalArgumentException("Request to map fluid " + fluid.toString() + " to null render type");
			fluidConsumer.accept(fluid, renderType);
		}
	}

	public static void mapFluids(int preset, Fluid[] fluids) {
		mapFluids(getRenderType(preset), fluids);
	}
}
