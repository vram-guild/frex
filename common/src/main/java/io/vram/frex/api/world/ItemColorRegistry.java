package io.vram.frex.api.world;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;

import io.vram.frex.impl.world.ColorRegistryImpl;

/**
 * Use this to safely register item colors during initialization
 * when the vanilla instances may not yet exist. Guarantees the registrations
 * will take place before rendering starts, irrespective of initialization order.
 */
@NonExtendable
public interface ItemColorRegistry {
	static void register(ItemColor itemColor, ItemLike... items) {
		ColorRegistryImpl.register(itemColor, items);
	}

	/**
	 * Convenient access for the default item colors instance.
	 * Will be null until the game client initialization creates it.
	 */
	static @Nullable ItemColors get() {
		return ColorRegistryImpl.getItemColors();
	}
}
