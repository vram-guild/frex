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

package io.vram.frex.impl.material.predicate;

import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.material.RenderMaterial;

public class StateMaterialOnly extends StateBiPredicate {
	private MaterialPredicate materialPredicate;

	public StateMaterialOnly(MaterialPredicate materialPredicate) {
		this.materialPredicate = materialPredicate;
	}

	@Override
	public boolean test(BlockState ignored, RenderMaterial renderMaterial) {
		return materialPredicate.test(renderMaterial);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StateMaterialOnly) {
			return materialPredicate.equals(((StateMaterialOnly) obj).materialPredicate);
		} else {
			return false;
		}
	}
}
