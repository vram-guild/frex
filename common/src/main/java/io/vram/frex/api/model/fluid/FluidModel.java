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

package io.vram.frex.api.model.fluid;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import io.vram.frex.api.model.BlockModel;
import io.vram.frex.impl.model.FluidModelImpl;

/**
 * Identical in operation to {@link BlockModel} but for fluids.
 *
 * <p>A FREX-compliant renderer will call this - in addition to the block quad emitter - for
 * block state with a non-empty fluid state.  Block state is passed instead of fluid state
 * to keep the method signature compact and provide access to the block state if needed.
 */
@FunctionalInterface
public interface FluidModel extends BlockModel {
	static FluidModel get(Fluid forFluid) {
		return FluidModelImpl.get(forFluid);
	}

	/**
	 * Add a FluidQuadSupplier factory for the given fluid.
	 *
	 * <p>Accepts a factory so that instances can be recreated when render state
	 * is invalidated. This allows implementations to cache sprites or other elements of
	 * render state without checking for or handling reloads.
	 */
	static void registerFactory(Function<Fluid, FluidModel> factory, ResourceLocation forFluid) {
		FluidModelImpl.registerFactory(factory, forFluid);
	}

	// WIP: the factory provided by the renderer should probably be implemented by FREX - unclear why renderer would do it

	/**
	 * To be called 1X by renderer implementation. Provides the logic
	 * that will implement fluid : supplier factory.
	 *
	 * <p>Handler gets the fluid and the associated factory if available.
	 */
	static void setReloadHandler(BiFunction<Fluid, Function<Fluid, FluidModel>, FluidModel> handler) {
		FluidModelImpl.setReloadHandler(handler);
	}
}
