/*
 * Copyright © Original Authors
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

package io.vram.frex.api.light;

import net.minecraft.world.item.ItemStack;

import io.vram.frex.impl.light.ItemLightLoader;
import io.vram.frex.impl.light.SimpleItemLight;

@FunctionalInterface
public interface ItemLight {
	/**
	 * How far the light can reach.  A value of 1.0 means the light
	 * can reach the maximum configured distance, 12 blocks by default.
	 * Zero disables the light source.
	 * @return
	 */
	float intensity();

	/**
	 * Red light component.
	 * @return 0 to 1
	 */
	default float red() {
		return 1f;
	}

	/**
	 * Green light component.
	 * @return 0 to 1
	 */
	default float green() {
		return 1f;
	}

	/**
	 * Blue light component.
	 * @return 0 to 1
	 */
	default float blue() {
		return 1f;
	}

	/**
	 * Control if the light should work when submerged.
	 * @return true if works in a fluid
	 */
	default boolean worksInFluid() {
		return true;
	}

	/**
	 * Setting to a value < 360 will result in a spot light effect.
	 * This is the angle of full brightness within the light cone.
	 * Attenuation is assumed to be the same as for non-spot lights.
	 */
	default int innerConeAngleDegrees() {
		return 360;
	}

	/**
	 * The angle of reduced brightness around the inner light cone.
	 * Set to a value < 360 but greater than {@link #innerConeAngleDegrees()}
	 * to create a fall-off effect around a spot light.
	 * Attenuation is assumed to be the same as for non-spot lights.
	 */
	default int outerConeAngleDegrees() {
		return 360;
	}

	ItemLight NONE = () -> 0;

	static ItemLight get(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return NONE;
		} else if (stack.getItem() instanceof ItemLightProvider) {
			return ((ItemLightProvider) stack.getItem()).getItemLight(stack);
		} else {
			return ItemLightLoader.get(stack);
		}
	}

	static ItemLight of(float intensity, float red, float green, float blue, boolean worksInFluid) {
		return new SimpleItemLight(intensity, red, green, blue, worksInFluid, 360, 360);
	}

	static ItemLight of(float intensity, float red, float green, float blue, boolean worksInFluid, int innerConeAngleDegrees, int outerConeAngleDegrees) {
		innerConeAngleDegrees = Math.min(360, Math.max(1, innerConeAngleDegrees));
		outerConeAngleDegrees = Math.min(360, Math.max(innerConeAngleDegrees, outerConeAngleDegrees));
		return new SimpleItemLight(intensity, red, green, blue, worksInFluid, innerConeAngleDegrees, outerConeAngleDegrees);
	}
}
