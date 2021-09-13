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

package grondag.frex.mixin;

import io.vram.frex.api.model.BlockModel;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.api.model.ModelRenderContext;
import io.vram.frex.compat.fabric.FabricModelWrapper;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;

@Mixin(FabricBakedModel.class)
public interface MixinFabricBakedModel extends BlockModel, ItemModel {
	@Override
	default void renderAsItem(ItemStack itemStack, Mode mode, ModelRenderContext context) {
		FabricModelWrapper.wrap((FabricBakedModel) this).renderAsItem(itemStack, mode, context);
	}

	@Override
	default void renderAsBlock(BlockRenderView blockView, BlockState state, BlockPos pos, ModelRenderContext context) {
		FabricModelWrapper.wrap((FabricBakedModel) this).renderAsBlock(blockView, state, pos, context);
	}
}
