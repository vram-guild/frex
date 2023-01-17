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

package io.vram.frex.impl.light;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import io.vram.frex.api.light.ItemLight;
import io.vram.frex.impl.FrexLog;

public class ItemLightLoader {
	private ItemLightLoader() { }

	public void reload(ResourceManager manager) {
		MAP.clear();

		for (Item item : BuiltInRegistries.ITEM) {
			loadItem(manager, item);
		}
	}

	private void loadItem(ResourceManager manager, Item item) {
		final ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
		final ResourceLocation id = new ResourceLocation(itemId.getNamespace(), "lights/item/" + itemId.getPath() + ".json");

		try {
			final var res = manager.getResource(id);

			if (res.isPresent()) {
				final ItemLight light = ItemLightDeserializer.deserialize(new InputStreamReader(res.get().open(), StandardCharsets.UTF_8));

				if (light != ItemLight.NONE) {
					MAP.put(item, light);
				}
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load item light data for " + id.toString() + " due to exception " + e.toString());
		}
	}

	public static final ItemLightLoader INSTANCE = new ItemLightLoader();
	private static final IdentityHashMap<Item, ItemLight> MAP = new IdentityHashMap<>();

	public static ItemLight get(ItemStack stack) {
		return MAP.getOrDefault(stack.getItem(), ItemLight.NONE);
	}
}
