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

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.BlockItemModel;
import io.vram.frex.api.model.BlockModel;

@Mixin(MultiPartBakedModel.class)
public class MixinMultipartBakedModel implements BlockItemModel {
	@Shadow private List<Pair<Predicate<BlockState>, BakedModel>> selectors;
	@Shadow protected Map<BlockState, BitSet> selectorCache;

	@Override
	public void renderAsItem(ItemInputContext input, QuadSink output) {
		// NOOP
	}

	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		final var state = input.blockState();

		if (state != null) {
			BitSet bits = selectorCache.get(state);

			if (bits == null) {
				bits = new BitSet();

				for (int i = 0; i < selectors.size(); ++i) {
					final Pair<Predicate<BlockState>, BakedModel> pair = selectors.get(i);

					if (pair.getLeft().test(state)) {
						bits.set(i);
					}
				}

				selectorCache.put(state, bits);
			}

			final int limit = bits.length();

			for (int i = 0; i < limit; ++i) {
				if (bits.get(i)) {
					((BlockModel) selectors.get(i).getRight()).renderAsBlock(input, output);
				}
			}
		}
	}
}
