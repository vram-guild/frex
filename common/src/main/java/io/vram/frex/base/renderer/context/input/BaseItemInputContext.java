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

package io.vram.frex.base.renderer.context.input;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.api.model.ItemModel.ItemInputContext;
import io.vram.frex.api.rendertype.RenderTypeUtil;
import io.vram.frex.api.world.ItemColorRegistry;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;

public class BaseItemInputContext extends BaseBakedInputContext implements ItemInputContext {
	protected final ItemColors colorMap = ItemColorRegistry.get();
	protected ItemStack itemStack;
	protected ItemDisplayContext renderMode;
	protected boolean isBlockItem;
	protected boolean drawTranslucencyToMainTarget;
	protected int lightmap;
	protected boolean isLeftHand;

	public BaseItemInputContext() {
		super(Type.ITEM);
	}

	public void prepareForItem(BakedModel bakedModel, ItemStack itemStack, ItemDisplayContext renderMode, int lightmap, int overlay, boolean isLeftHand, MatrixStack matrixStack) {
		super.prepare(overlay, matrixStack);
		this.bakedModel = bakedModel;
		this.itemStack = itemStack;
		this.renderMode = renderMode;
		this.lightmap = lightmap;
		this.isLeftHand = isLeftHand;
		defaultRenderType = null;
		isBlockItem = itemStack.getItem() instanceof BlockItem;
		drawTranslucencyToMainTarget = isGui() || renderMode.firstPerson() || !isBlockItem();
	}

	@Override
	public int lightmap() {
		return lightmap;
	}

	@Override
	public ItemStack itemStack() {
		return itemStack;
	}

	@Override
	public ItemDisplayContext mode() {
		return renderMode;
	}

	@Override
	public boolean isGui() {
		return renderMode == ItemDisplayContext.GUI;
	}

	@Override
	public boolean isFrontLit() {
		return isGui() && bakedModel != null && !bakedModel.usesBlockLight();
	}

	@Override
	public boolean isLeftHand() {
		return isLeftHand;
	}

	@Override
	public boolean isBlockItem() {
		return isBlockItem;
	}

	@Override
	public boolean drawTranslucencyToMainTarget() {
		return drawTranslucencyToMainTarget;
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

	@Override
	public int flatBrightness(BaseQuadEmitter quad) {
		return lightmap;
	}

	@Override
	protected void computeDefaultRenderType() {
		if (defaultRenderType == null) {
			final var renderType = ItemBlockRenderTypes.getRenderType(itemStack, drawTranslucencyToMainTarget());
			defaultRenderType = renderType;
			defaultPreset = RenderTypeUtil.inferPreset(renderType);
		}
	}
}
