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

package io.vram.frex.impl.light;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.Iterator;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import io.vram.frex.api.light.ItemLight;
import io.vram.frex.impl.FrexLog;

public class ItemLightLoader {
	private ItemLightLoader() { }

	public void reload(ResourceManager manager) {
		MAP.clear();
		final Iterator<Item> items = Registry.ITEM.iterator();

		while (items.hasNext()) {
			loadItem(manager, items.next());
		}
	}

	private void loadItem(ResourceManager manager, Item item) {
		final ResourceLocation itemId = Registry.ITEM.getKey(item);
		final ResourceLocation id = new ResourceLocation(itemId.getNamespace(), "lights/item/" + itemId.getPath() + ".json");

		try (Resource res = manager.getResource(id)) {
			final ItemLight light = ItemLightDeserializer.deserialize(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8));

			if (light != ItemLight.NONE) {
				MAP.put(item, light);
			}
		} catch (final FileNotFoundException e) {
			// eat these, lights are not required
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
