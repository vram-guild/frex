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

package io.vram.frex.base.renderer;

import java.util.function.BooleanSupplier;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialCondition;
import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.renderer.ConditionManager;
import io.vram.frex.impl.FrexLog;

public class BaseConditionManager implements ConditionManager {
	public static final int CONDITION_FLAG_ARRAY_LENGTH = (MaterialConstants.MAX_CONDITIONS + 31) / 32;

	protected final Object2ObjectOpenHashMap<ResourceLocation, BaseMaterialCondition> conditionMap = new Object2ObjectOpenHashMap<>();
	public final BaseMaterialCondition[] conditions = new BaseMaterialCondition[MaterialConstants.MAX_CONDITIONS];
	public final int[] conditionFlags = new int[CONDITION_FLAG_ARRAY_LENGTH];
	protected int nextIndex = 0;
	public final BaseMaterialCondition alwaysTrue = createCondition(() -> true);

	@Override
	public BaseMaterialCondition conditionFromIndex(int index) {
		return conditions[index];
	}

	@Override
	public BaseMaterialCondition createCondition(BooleanSupplier supplier) {
		if (nextIndex >= MaterialConstants.MAX_CONDITIONS) {
			FrexLog.warn("Unable to create new render condition because max conditions have already been created.  Some renders may not work correctly.");
			return alwaysTrue;
		} else {
			return new BaseMaterialCondition(supplier);
		}
	}

	@Override
	public int indexOf(MaterialCondition condition) {
		return ((BaseMaterialCondition) condition).index;
	}

	@Override
	public MaterialCondition conditionFromId(ResourceLocation id) {
		return conditionMap.get(id);
	}

	@Override
	public boolean registerCondition(ResourceLocation id, MaterialCondition condition) {
		if (conditionMap.containsKey(id)) {
			return false;
		}

		// cast to prevent acceptance of impostor implementations
		conditionMap.put(id, (BaseMaterialCondition) condition);
		return true;
	}

	@Override
	public MaterialCondition alwaysTrue() {
		return alwaysTrue;
	}

	public void update() {
		for (int i = 0; i < CONDITION_FLAG_ARRAY_LENGTH; ++i) {
			conditionFlags[i] = 0;
		}

		for (int i = 0; i < nextIndex; ++i) {
			if (conditions[i].supplier.getAsBoolean()) {
				conditionFlags[i >> 5] |= (1 << (i & 31));
			}
		}
	}

	public class BaseMaterialCondition implements MaterialCondition {
		public final BooleanSupplier supplier;
		public final int index;
		private final int arrayIndex;
		private final int testMask;

		BaseMaterialCondition(BooleanSupplier supplier) {
			this.supplier = supplier;

			synchronized (conditions) {
				index = nextIndex++;
				conditions[index] = this;
			}

			arrayIndex = index >> 5;
			testMask = (1 << (index & 31));
		}

		@Override
		public boolean compute() {
			return (conditionFlags[arrayIndex] & testMask) != 0;
		}

		@Override
		public int index() {
			return index;
		}
	}
}
