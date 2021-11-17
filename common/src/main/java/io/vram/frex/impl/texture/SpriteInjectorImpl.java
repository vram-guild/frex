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

package io.vram.frex.impl.texture;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.texture.SpriteInjector;

public class SpriteInjectorImpl {
	protected static final Object2ObjectOpenHashMap<ResourceLocation, ObjectOpenHashSet<ResourceLocation>> constantSprites = new Object2ObjectOpenHashMap<>();
	protected static final Object2ObjectOpenHashMap<ResourceLocation, ObjectArrayList<Consumer<SpriteInjector>>> listeners = new Object2ObjectOpenHashMap<>();

	public static void injectAlways(ResourceLocation atlasId, ResourceLocation spriteId) {
		constantSprites.computeIfAbsent(atlasId, k -> new ObjectOpenHashSet<>()).add(spriteId);
	}

	public static void injectOnAtlasStitch(ResourceLocation atlasId, Consumer<SpriteInjector> listener) {
		listeners.computeIfAbsent(atlasId, k -> new ObjectArrayList<>()).add(listener);
	}

	public static void forEachInjection(ResourceLocation atlasId, SpriteInjector injector) {
		final var set = constantSprites.get(atlasId);

		if (set != null) {
			set.forEach(injector::inject);
		}

		final var list = listeners.get(atlasId);

		if (list != null) {
			list.forEach(c -> c.accept(injector));
		}
	}
}
