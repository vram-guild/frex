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

package io.vram.frex.api.material;

import java.util.function.BooleanSupplier;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.renderer.Renderer;

@FunctionalInterface
public interface MaterialCondition {
	/**
	 * Called at most once per frame.
	 */
	boolean compute();

	default int index() {
		return Renderer.get().conditions().indexOf(this);
	}

	static MaterialCondition create(BooleanSupplier supplier, boolean affectBlocks, boolean affectItems) {
		return Renderer.get().conditions().createCondition(supplier, affectBlocks, affectItems);
	}

	static MaterialCondition fromIndex(int index) {
		return Renderer.get().conditions().conditionByIndex(index);
	}

	static boolean registerCondition(ResourceLocation id, MaterialCondition condition) {
		return Renderer.get().conditions().registerCondition(id, condition);
	}

	static MaterialCondition fromId(ResourceLocation id) {
		return Renderer.get().conditions().conditionById(id);
	}

	static MaterialCondition alwaysTrue() {
		return Renderer.get().conditions().alwaysTrue();
	}
}
