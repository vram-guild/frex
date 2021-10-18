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

package io.vram.frex.base.renderer.context;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.world.item.ItemStack;

import io.vram.frex.api.model.ItemModel.ItemInputContext;
import io.vram.frex.api.world.ItemColorRegistry;

public abstract class BaseItemContext extends BaseBakedContext implements ItemInputContext {
	protected final ItemColors colorMap = ItemColorRegistry.get();
	protected ItemStack itemStack;
	protected TransformType renderMode;

	public BaseItemContext() {
		super(Type.ITEM);
	}

	public void prepareForItem(ItemStack itemStack, TransformType renderMode, int overlay) {
		super.reset(overlay);
		this.itemStack = itemStack;
		this.renderMode = renderMode;
	}

	@Override
	public boolean cullTest(int faceId) {
		return true;
	}

	@Override
	public ItemStack itemStack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransformType mode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long randomSeed() {
		return ITEM_RANDOM_SEED;
	}

	@Override
	public int indexedColor(int colorIndex) {
		return colorIndex == -1 ? -1 : (colorMap.getColor(itemStack, colorIndex) | 0xFF000000);
	}

	/**
	 * Value vanilla uses for item rendering.  The only sensible choice, of course.
	 */
	public static final long ITEM_RANDOM_SEED = 42L;
}
