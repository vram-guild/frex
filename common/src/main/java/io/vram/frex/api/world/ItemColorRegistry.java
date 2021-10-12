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
