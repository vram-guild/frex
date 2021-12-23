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

package io.vram.frex.api.model;

import java.util.Random;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.InputContext.Type;
import io.vram.frex.impl.model.ModelLookups;

@FunctionalInterface
public interface ItemModel extends DynamicModel {
	void renderAsItem(ItemInputContext input, QuadSink output);

	@Override
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

		boolean isGui();

		/**
		 * True for generated models when in GUI and diffuse shading shouldn't be used.
		 * True only when isGui is true;
		 */
		boolean isFrontLit();

		/**
		 * When false, assume item models are generated and should be rendered with cutout enabled if blend mode is translucent.
		 */
		boolean isBlockItem();

		/**
		 * True when drawing to GUI or first person perspective.
		 */
		boolean drawTranslucencyToMainTarget();

		/**
		 * True only when rendering in first person and item is held in left hand.
		 */
		boolean isLeftHand();

		int lightmap();
	}

	/**
	 * Result can be safely cast to ItemModel.  Return type is BakedModel
	 * because it may still be needed for model-level attributes.
	 */
	static BakedModel get(Item item) {
		return ModelLookups.ITEM_MODEL_SHAPER.getItemModel(item);
	}

	/**
	 * Result can be safely cast to ItemModel.  Return type is BakedModel
	 * because it may still be needed for model-level attributes.
	 */
	static BakedModel get(ItemStack itemStack) {
		return ModelLookups.ITEM_MODEL_SHAPER.getItemModel(itemStack);
	}
}
