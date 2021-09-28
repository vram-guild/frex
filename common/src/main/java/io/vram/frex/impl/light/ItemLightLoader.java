/*
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
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
