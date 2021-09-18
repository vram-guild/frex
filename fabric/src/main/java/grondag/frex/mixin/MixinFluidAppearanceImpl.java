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

import io.vram.frex.api.model.FluidAppearance;
import io.vram.frex.impl.model.FluidAppearanceImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.fluid.Fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

//WIP: turn off via mixin config when FAPI lib not present
// Maxes FluidRenderHandler and FluidRenderHandler cross-compatible
@Mixin(FluidAppearanceImpl.class)
public abstract class MixinFluidAppearanceImpl implements FluidRenderHandler {
	/**
	 * @author Grondag
	 * @reason how we control interop on FAPI
	 */
	@Overwrite
	public static FluidAppearance get(Fluid fluid) {
		return (FluidAppearance) FluidRenderHandlerRegistry.INSTANCE.get(fluid);
	}

	/**
	 * @author Grondag
	 * @reason how we control interop on FAPI
	 */
	@Overwrite
	public static void register(FluidAppearance appearance, Fluid[] fluids) {
		for (final var f : fluids) {
			FluidRenderHandlerRegistry.INSTANCE.register(f, (FluidRenderHandler) appearance);
		}
	}
}
