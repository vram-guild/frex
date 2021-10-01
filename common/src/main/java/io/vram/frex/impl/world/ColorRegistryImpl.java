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
