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

package io.vram.frex.api.model;

import io.vram.frex.impl.model.FluidAppearanceImpl;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public interface FluidAppearance extends FluidColorProvider, FluidSpriteProvider {
	static FluidAppearance get(Fluid fluid) {
		return FluidAppearanceImpl.get(fluid);
	}

	static FluidAppearance register(Fluid fluid, FluidColorProvider colorProvider, FluidSpriteProvider spriteProvider) {
		return FluidAppearanceImpl.register(fluid, colorProvider, spriteProvider);
	}

	FluidAppearance STILL_WATER_APPEARANCE = register(Fluids.WATER, FluidColorProvider.WATER_COLOR, FluidSpriteProvider.WATER_SPRITES);
	FluidAppearance FLOWING_WATER_APPEARANCE = register(Fluids.FLOWING_WATER, FluidColorProvider.WATER_COLOR, FluidSpriteProvider.WATER_SPRITES);
	FluidAppearance STILL_LAVA_APPEARANCE = register(Fluids.LAVA, FluidColorProvider.WHITE_COLOR, FluidSpriteProvider.LAVA_SPRITES);
	FluidAppearance FLOWING_LAVA_APPEARANCE = register(Fluids.FLOWING_LAVA, FluidColorProvider.WHITE_COLOR, FluidSpriteProvider.LAVA_SPRITES);
}
