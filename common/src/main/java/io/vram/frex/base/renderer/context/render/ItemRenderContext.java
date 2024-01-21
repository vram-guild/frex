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

package io.vram.frex.base.renderer.context.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.base.renderer.context.input.BaseItemInputContext;

public abstract class ItemRenderContext extends BakedRenderContext<BaseItemInputContext> {
	@Override
	protected BaseItemInputContext createInputContext() {
		return new BaseItemInputContext();
	}

	protected abstract void prepareEncoding(MultiBufferSource vertexConsumers);

	@SuppressWarnings("resource")
	public void renderItem(ItemModelShaper models, ItemStack stack, ItemDisplayContext renderMode, boolean isLeftHand, PoseStack poseStack, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model) {
		if (stack.isEmpty()) return;
		final boolean detachedPerspective = renderMode == ItemDisplayContext.GUI || renderMode == ItemDisplayContext.GROUND || renderMode == ItemDisplayContext.FIXED;

		if (detachedPerspective) {
			if (stack.is(Items.TRIDENT)) {
				model = models.getItemModel(Items.TRIDENT);
			} else if (stack.is(Items.SPYGLASS)) {
				model = models.getItemModel(Items.SPYGLASS);
			}
		}

		final MatrixStack matrixStack = MatrixStack.fromVanilla(poseStack);
		inputContext.prepareForItem(model, stack, renderMode, light, overlay, isLeftHand, matrixStack);
		materialMap = MaterialMap.get(stack);
		final var itemTransforms = model.getTransforms();

		if (itemTransforms != null) {
			matrixStack.push();
			itemTransforms.getTransform(renderMode).apply(isLeftHand, poseStack);
			matrixStack.translate(-0.5f, -0.5f, -0.5f);
		}

		prepareEncoding(vertexConsumers);

		if (model.isCustomRenderer() || stack.getItem() == Items.TRIDENT && !detachedPerspective) {
			renderCustomModel(Minecraft.getInstance().getItemRenderer().blockEntityRenderer, vertexConsumers);
		} else {
			((ItemModel) model).renderAsItem(inputContext, emitter());
		}

		if (itemTransforms != null) {
			matrixStack.pop();
		}
	}

	protected abstract void renderCustomModel(BlockEntityWithoutLevelRenderer builtInRenderer, MultiBufferSource vertexConsumers);

	@Override
	protected void applyMaterialDefaults() {
		super.applyMaterialDefaults();

		if (finder.foilOverlayIsDefault()) {
			finder.foilOverlay(inputContext.itemStack().hasFoil());
		}

		if (finder.disableDiffuseIsDefault()) {
			finder.disableDiffuse(inputContext.isFrontLit());
		}
	}

	@Override
	protected void resolvePreset() {
		if (finder.preset() == MaterialConstants.PRESET_DEFAULT) {
			finder.preset(inputContext.defaultPreset());
		}

		if (finder.preset() == MaterialConstants.PRESET_TRANSLUCENT) {
			// Special case for translucent items
			finder.transparency(MaterialConstants.TRANSPARENCY_TRANSLUCENT)
					.cutout(inputContext.isBlockItem() ? MaterialConstants.CUTOUT_NONE : MaterialConstants.CUTOUT_TENTH)
					.unmipped(false)
					.target(inputContext.drawTranslucencyToMainTarget() ? MaterialConstants.TARGET_SOLID : MaterialConstants.TARGET_ENTITIES)
					.sorted(!inputContext.drawTranslucencyToMainTarget());

			// Importantly
			finder.preset(MaterialConstants.PRESET_NONE);

			return;
		}

		super.resolvePreset();
	}

	@Override
	protected void shadeQuad() {
		emitter.applyFlatLighting(inputContext.flatBrightness(emitter));
		emitter.colorize(inputContext);
	}

	@Override
	protected void adjustMaterialForEncoding() {
		if (inputContext.isGui()) {
			finder.fog(false);
		}

		if (finder.disableAoIsDefault()) {
			finder.disableAo(true);
		}
	}
}
