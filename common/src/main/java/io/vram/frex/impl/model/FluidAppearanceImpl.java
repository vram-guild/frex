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

package io.vram.frex.impl.model;

import io.vram.frex.api.model.FluidAppearance;
import io.vram.frex.api.model.FluidColorProvider;
import io.vram.frex.api.model.FluidSpriteProvider;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Internal
public class FluidAppearanceImpl implements FluidAppearance {
	private final FluidColorProvider colorProvider;
	private final FluidSpriteProvider spriteProvider;

	FluidAppearanceImpl(FluidColorProvider colorProvider, FluidSpriteProvider spriteProvider) {
		this.colorProvider = colorProvider;
		this.spriteProvider = spriteProvider;
	}

	@Override
	public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return colorProvider.getFluidColor(view, pos, state);
	}

	@Override
	public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return spriteProvider.getFluidSprites(view, pos, state);
	}
}
