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

package io.vram.frex.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.BlockItemModel;
import io.vram.frex.api.model.BlockModel;
import io.vram.frex.api.model.ItemModel;

@Mixin(WeightedBakedModel.class)
public class MixinWeightedBakedModel implements BlockItemModel {
	@Shadow @Final private List<WeightedEntry.Wrapper<BakedModel>> list;
	@Shadow @Final private int totalWeight;

	private BakedModel getModel(RandomSource random) {
		return WeightedRandom.getWeightedItem(list, Math.abs((int) random.nextLong()) % totalWeight).get().getData();
	}

	@Override
	public void renderAsItem(ItemInputContext input, QuadSink output) {
		final BakedModel model = getModel(input.random());
		((ItemModel) model).renderAsItem(input, output);
	}

	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		final BakedModel model = getModel(input.random());
		((BlockModel) model).renderAsBlock(input, output);
	}
}
