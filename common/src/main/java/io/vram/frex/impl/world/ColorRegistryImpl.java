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

package io.vram.frex.impl.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

@Internal
public class ColorRegistryImpl {
	private static ObjectArrayList<Pair<ItemColor, ItemLike[]>> tempItemColors = new ObjectArrayList<>();
	private static ObjectArrayList<Pair<BlockColor, Block[]>> tempBlockColors = new ObjectArrayList<>();

	private static BlockColors blockColors;
	private static ItemColors itemColors;

	public static void register(ItemColor itemColor, ItemLike[] items) {
		if (itemColors != null) {
			itemColors.register(itemColor, items);
		} else {
			tempItemColors.add(Pair.of(itemColor, items));
		}
	}

	public static void register(BlockColor blockColor, Block[] blocks) {
		if (blockColors != null) {
			blockColors.register(blockColor, blocks);
		} else {
			tempBlockColors.add(Pair.of(blockColor, blocks));
		}
	}

	public static ItemColors getItemColors() {
		return itemColors;
	}

	public static BlockColors getBlockColors() {
		return blockColors;
	}

	public static void setBlockColors(BlockColors blockColorsIn) {
		blockColors = blockColorsIn;

		for (final var t : tempBlockColors) {
			blockColors.register(t.getLeft(), t.getRight());
		}

		tempBlockColors.clear();
		tempBlockColors = null;
	}

	public static void setItemColors(ItemColors itemColorsIn) {
		itemColors = itemColorsIn;

		for (final var t : tempItemColors) {
			itemColors.register(t.getLeft(), t.getRight());
		}

		tempItemColors.clear();
		tempItemColors = null;
	}
}
