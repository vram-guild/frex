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

package io.vram.frex.impl.model;

import java.util.IdentityHashMap;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.model.fluid.FluidAppearance;
import io.vram.frex.api.model.fluid.FluidModel;
import io.vram.frex.api.model.fluid.SimpleFluidModel;
import io.vram.frex.impl.FrexLog;

@Internal
public class FluidModelImpl {
	private static final Object2ObjectOpenHashMap<ResourceLocation, Function<Fluid, FluidModel>> FACTORIES = new Object2ObjectOpenHashMap<>();
	private static final IdentityHashMap<Fluid, FluidModel> SUPPLIERS = new IdentityHashMap<>();

	public static void reload() {
		SUPPLIERS.clear();

		Registry.FLUID.forEach(fluid -> {
			final Function<Fluid, FluidModel> factory = FACTORIES.get(Registry.FLUID.getKey(fluid));
			SUPPLIERS.put(fluid, factory == null ? defaultModel(fluid) : factory.apply(fluid));
		});
	}

	public static FluidModel get(Fluid forFluid) {
		return SUPPLIERS.get(forFluid);
	}

	public static void registerFactory(Function<Fluid, FluidModel> factory, ResourceLocation forFluidId) {
		if (FACTORIES.put(forFluidId, factory) != null) {
			FrexLog.warn("A FluidModel was registered more than once for fluid " + forFluidId.toString() + ". This is probably a mod conflict and the fluid may not render correctly.");
		}
	}

	private static final RenderMaterial WATER_MATERIAL = MaterialFinder.threadLocal()
			.preset(MaterialConstants.PRESET_TRANSLUCENT).disableAo(true).disableColorIndex(true).find();

	private static final RenderMaterial LAVA_MATERIAL = MaterialFinder.threadLocal()
			.preset(MaterialConstants.PRESET_SOLID).disableAo(true).disableColorIndex(true).emissive(true).find();

	private static FluidModel defaultModel(Fluid fluid) {
		if (fluid == Fluids.FLOWING_LAVA || fluid == Fluids.LAVA) {
			return new SimpleFluidModel(LAVA_MATERIAL, false, FluidAppearance.LAVA_APPEARANCE);
		} else if (fluid == Fluids.FLOWING_WATER || fluid == Fluids.WATER) {
			return new SimpleFluidModel(WATER_MATERIAL, false, FluidAppearance.WATER_APPEARANCE);
		} else {
			final var app = FluidAppearance.get(fluid);
			return app == null ? new SimpleFluidModel(WATER_MATERIAL, false, FluidAppearance.WATER_APPEARANCE) : new SimpleFluidModel(WATER_MATERIAL, false, app);
		}
	}
}
