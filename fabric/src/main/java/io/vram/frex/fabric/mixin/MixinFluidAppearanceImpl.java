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

package io.vram.frex.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.level.material.Fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

import io.vram.frex.api.model.fluid.FluidAppearance;
import io.vram.frex.impl.model.FluidAppearanceImpl;

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
