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

package io.vram.frex.impl.material.predicate;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.material.RenderMaterial;

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
