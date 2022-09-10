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

	default boolean registerWithId(ResourceLocation id) {
		return Renderer.get().conditions().registerCondition(id, this);
	}

	@Deprecated
	/**
	 * @deprecated Not practical for implementations to segregate item/block usage.
	 * If this is really needed can use separate conditions.
	 */
	static MaterialCondition create(BooleanSupplier supplier, boolean affectBlocks, boolean affectItems) {
		return create(supplier);
	}

	static MaterialCondition create(BooleanSupplier supplier) {
		return Renderer.get().conditions().createCondition(supplier);
	}

	static MaterialCondition fromIndex(int index) {
		return Renderer.get().conditions().conditionFromIndex(index);
	}

	static MaterialCondition fromId(ResourceLocation id) {
		return Renderer.get().conditions().conditionFromId(id);
	}

	static MaterialCondition alwaysTrue() {
		return Renderer.get().conditions().alwaysTrue();
	}
}
