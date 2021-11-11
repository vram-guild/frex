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

package io.vram.frex.pastel.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.pastel.PastelBlockRenderContext;
import io.vram.frex.pastel.mixinterface.ModelBlockRendererExt;

@Mixin(ModelBlockRenderer.class)
public abstract class MixinModelBlockRenderer implements ModelBlockRendererExt {
	@Inject(at = @At("HEAD"), method = "tesselateBlock", cancellable = true)
	private void onTesselate(BlockAndTintGetter blockView, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, boolean checkSides, Random rand, long seed, int overlay, CallbackInfoReturnable<Boolean> ci) {
		PastelBlockRenderContext.get().render((ModelBlockRenderer) (Object) this, blockView, model, state, pos, poseStack, buffer, checkSides, seed, overlay);
		ci.setReturnValue(true);
	}
}
