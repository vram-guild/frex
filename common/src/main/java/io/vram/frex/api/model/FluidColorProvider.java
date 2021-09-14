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

import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Get the tint color for a fluid being rendered at a given position.
 *
 * @param view  The world view pertaining to the fluid. May be null!
 * @param pos   The position of the fluid in the world. May be null!
 * @param state The current state of the fluid.
 * @return The tint color of the fluid.
 */
@FunctionalInterface
public interface FluidColorProvider {
	int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state);
}
