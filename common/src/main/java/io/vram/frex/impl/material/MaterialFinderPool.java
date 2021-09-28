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

package io.vram.frex.impl.material;

import org.jetbrains.annotations.ApiStatus.Internal;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.renderer.Renderer;

@Internal
public final class MaterialFinderPool {
	private MaterialFinderPool() { }

	private static ThreadLocal<MaterialFinder> POOL = ThreadLocal.withInitial(() -> Renderer.get().materialFinder());

	public static MaterialFinder threadLocal() {
		final MaterialFinder result = POOL.get();
		result.clear();
		return result;
	}
}
