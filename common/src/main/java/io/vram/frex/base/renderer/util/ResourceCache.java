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

package io.vram.frex.base.renderer.util;

import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class ResourceCache<T> {
	public ResourceCache(Supplier<T> loader) {
		CACHED.add(this);
		this.loader = loader;
	}

	protected final Supplier<T> loader;
	protected T value;

	protected void invalidate() {
		value = null;
	}

	public T getOrLoad() {
		if (value == null) {
			value = loader.get();
		}

		return value;
	}

	protected static final ObjectArrayList<ResourceCache<?>> CACHED = new ObjectArrayList<>(64);

	public static void invalidateAll() {
		CACHED.forEach(ResourceCache::invalidate);
	}
}
