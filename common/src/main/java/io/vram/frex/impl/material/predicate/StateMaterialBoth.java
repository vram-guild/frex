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

import io.vram.frex.api.material.RenderMaterial;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.state.BlockState;

public class StateMaterialBoth extends StateBiPredicate {
	private final StatePropertiesPredicate statePredicate;
	private final MaterialPredicate materialPredicate;

	public StateMaterialBoth(StatePropertiesPredicate statePredicate, MaterialPredicate materialPredicate) {
		this.statePredicate = statePredicate;
		this.materialPredicate = materialPredicate;
	}

	@Override
	public boolean test(BlockState blockState, RenderMaterial renderMaterial) {
		return statePredicate.matches(blockState) && materialPredicate.test(renderMaterial);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StateMaterialBoth) {
			return statePredicate.equals(((StateMaterialBoth) obj).statePredicate)
					&& materialPredicate.equals(((StateMaterialBoth) obj).materialPredicate);
		} else {
			return false;
		}
	}
}
