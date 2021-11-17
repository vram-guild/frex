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

package io.vram.frex.impl.material;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.material.BlockEntityMaterialMap;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;

@Internal
class BlockEntityMultiMaterialMap implements BlockEntityMaterialMap {
	private final BiPredicate<BlockState, RenderMaterial>[] predicates;
	private final MaterialTransform[] transforms;

	BlockEntityMultiMaterialMap(BiPredicate<BlockState, RenderMaterial>[] predicates, MaterialTransform[] transforms) {
		assert predicates != null;
		assert transforms != null;

		this.predicates = predicates;
		this.transforms = transforms;
	}

	@Override
	public RenderMaterial getMapped(RenderMaterial material, BlockState blockState, MaterialFinder finder) {
		final int limit = predicates.length;

		for (int i = 0; i < limit; ++i) {
			if (predicates[i].test(blockState, material)) {
				return transforms[i].transform(material, finder);
			}
		}

		return material;
	}
}
