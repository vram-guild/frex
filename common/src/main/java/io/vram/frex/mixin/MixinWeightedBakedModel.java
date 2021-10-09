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

package io.vram.frex.mixin;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import io.vram.frex.api.model.BlockModel;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.api.model.ModelOuputContext;

@Mixin(WeightedBakedModel.class)
public class MixinWeightedBakedModel implements BlockModel, ItemModel {
	@Shadow private List<WeightedEntry.Wrapper<BakedModel>> list;
	@Shadow private int totalWeight;

	private BakedModel getModel(Random random) {
		return WeightedRandom.getWeightedItem(list, Math.abs((int) random.nextLong()) % totalWeight).get().getData();
	}

	@Override
	public void renderAsItem(ItemInputContext input, ModelOuputContext output) {
		final BakedModel model = getModel(input.random());
		((ItemModel) model).renderAsItem(input, output);
	}

	@Override
	public void renderAsBlock(BlockInputContext input, ModelOuputContext output) {
		final BakedModel model = getModel(input.random());
		((BlockModel) model).renderAsBlock(input, output);
	}
}
