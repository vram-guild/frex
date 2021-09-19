/*
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.vram.frex.compat.fabric;

import io.vram.frex.api.model.BlockModel;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.api.model.ModelRenderContext;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;

// WIP: What to do with this?
public class FabricModelWrapper implements BlockModel, ItemModel {
	private static final ThreadLocal<FabricModelWrapper> POOL = ThreadLocal.withInitial(FabricModelWrapper::new);

	public static FabricModelWrapper wrap(FabricBakedModel wrapped) {
		final var result = POOL.get();
		result.wrapped = wrapped;
		return result;
	}

	private FabricBakedModel wrapped;

	private final FabricContextWrapper contextWrapper = new FabricContextWrapper();

	@Override
	public void renderAsItem(ItemStack itemStack, TransformType mode, ModelRenderContext context) {
		wrapped.emitItemQuads(itemStack, context::random, contextWrapper.wrap(context));
	}

	@Override
	public void renderAsBlock(BlockAndTintGetter blockView, BlockState state, BlockPos pos, ModelRenderContext context) {
		wrapped.emitBlockQuads(blockView, state, pos, context::random, contextWrapper.wrap(context));
	}
}
