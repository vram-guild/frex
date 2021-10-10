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

package io.vram.frex.api.model;

import java.util.Random;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.InputContext.Type;

@FunctionalInterface
public interface ItemModel {
	void renderAsItem(ItemInputContext input, QuadSink output);

	default void renderDynamic(InputContext input, QuadSink output) {
		if (input.type() == Type.ITEM) {
			renderAsItem((ItemInputContext) input, output);
		}
	}

	public interface ItemInputContext extends BakedInputContext {
		@Override
		default Type type() {
			return Type.ITEM;
		}

		ItemStack itemStack();

		ItemTransforms.TransformType mode();

		@Override
		Random random();
	}
}
