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

package io.vram.frex.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;

import io.vram.frex.api.model.BlockModel;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.api.model.ModelRenderContext;
import io.vram.frex.compat.fabric.FabricContextWrapper;

@Mixin(FabricBakedModel.class)
public interface MixinFabricBakedModel extends BlockModel, ItemModel {
	@Override
	default void renderAsItem(ItemInputContext input, ModelRenderContext context) {
		((FabricBakedModel) this).emitItemQuads(input.itemStack(), input::random, FabricContextWrapper.wrap(context));
	}

	@Override
	default void renderAsBlock(BlockInputContext input, ModelRenderContext context) {
		((FabricBakedModel) this).emitBlockQuads(input.blockView(), input.blockState(), input.pos(), input::random, FabricContextWrapper.wrap(context));
	}
}
