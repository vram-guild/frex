package io.vram.frex.api.world;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;

import io.vram.frex.impl.world.ColorRegistryImpl;

/**
 * Use this to safely register block colors during initialization
 * when the vanilla instances may not yet exist. Guarantees the registrations
 * will take place before rendering starts, irrespective of initialization order.
 */
@NonExtendable
public interface BlockColorRegistry {
	static void register(BlockColor blockColor, Block... blocks) {
		ColorRegistryImpl.register(blockColor, blocks);
	}

	/**
	 * Convenient access for the default block colors instance.
	 * Will be null until the game client initialization creates it.
	 */
	static @Nullable BlockColors get() {
		return ColorRegistryImpl.getBlockColors();
	}
}
