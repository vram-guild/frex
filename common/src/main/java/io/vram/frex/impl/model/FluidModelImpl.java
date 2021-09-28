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

package io.vram.frex.impl.model;

import java.util.IdentityHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import io.vram.frex.api.model.fluid.FluidModel;
import io.vram.frex.impl.FrexLog;

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
