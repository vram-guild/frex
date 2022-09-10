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

package io.vram.frex.base.renderer.material;

import java.util.function.BooleanSupplier;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialCondition;
import io.vram.frex.api.renderer.ConditionManager;

public class UnsupportedConditionManager implements ConditionManager {
	@Override
	public MaterialCondition createCondition(BooleanSupplier supplier) {
		return ALWAYS_TRUE;
	}

	@Override
	public int indexOf(MaterialCondition condition) {
		return 0;
	}

	@Override
	public MaterialCondition conditionFromIndex(int index) {
		return ALWAYS_TRUE;
	}

	@Override
	public boolean registerCondition(ResourceLocation id, MaterialCondition condition) {
		return false;
	}

	@Override
	public MaterialCondition conditionFromId(ResourceLocation id) {
		return ALWAYS_TRUE;
	}

	@Override
	public MaterialCondition alwaysTrue() {
		return ALWAYS_TRUE;
	}

	MaterialCondition ALWAYS_TRUE = new MaterialCondition() {
		@Override
		public boolean compute() {
			return true;
		}

		@Override
		public int index() {
			return 0;
		}
	};
}
