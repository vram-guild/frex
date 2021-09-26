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

import java.util.IdentityHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.vram.frex.api.model.fluid.FluidModel;
import io.vram.frex.impl.FrexLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

@Internal
public class FluidModelImpl {
	private static final Object2ObjectOpenHashMap<ResourceLocation, Function<Fluid, FluidModel>> FACTORIES = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Fluid, FluidModel> SUPPLIERS = new IdentityHashMap<>();
	private static BiFunction<Fluid, Function<Fluid, FluidModel>, FluidModel> handler;

	public static void reload() {
		SUPPLIERS.clear();

		if (handler != null) {
			Registry.FLUID.forEach(fluid -> {
				final Function<Fluid, FluidModel> factory = FACTORIES.get(Registry.FLUID.getKey(fluid));
				SUPPLIERS.put(fluid, handler.apply(fluid, factory));
			});
		}
	}

	public static FluidModel get(Fluid forFluid) {
		return SUPPLIERS.get(forFluid);
	}

	public static void registerFactory(Function<Fluid, FluidModel> factory, ResourceLocation forFluidId) {
		if (FACTORIES.put(forFluidId, factory) != null) {
			FrexLog.warn("A FluidModel was registered more than once for fluid " + forFluidId.toString() + ". This is probably a mod conflict and the fluid may not render correctly.");
		}
	}

	public static void setReloadHandler(BiFunction<Fluid, Function<Fluid, FluidModel>, FluidModel> handlerIn) {
		assert handler == null;
		handler = handlerIn;
	}
}
