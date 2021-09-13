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

import java.util.function.BiFunction;
import java.util.function.Function;

import io.vram.frex.impl.model.FluidModelImpl;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

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
	static void registerFactory(Function<Fluid, FluidModel> factory, Identifier forFluid) {
		FluidModelImpl.registerFactory(factory, forFluid);
	}

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
