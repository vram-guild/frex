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

package io.vram.frex.impl.material.predicate;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.material.RenderMaterial;

public class StateOnly extends StateBiPredicate {
	private final StatePropertiesPredicate statePredicate;

	public StateOnly(StatePropertiesPredicate statePredicate) {
		this.statePredicate = statePredicate;
	}

	@Override
	public boolean test(BlockState blockState, RenderMaterial renderMaterial) {
		return statePredicate.matches(blockState);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StateOnly) {
			return statePredicate.equals(((StateOnly) obj).statePredicate);
		} else {
			return false;
		}
	}
}
