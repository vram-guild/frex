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

package io.vram.frex.impl.light;

import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.util.GsonHelper;

import io.vram.frex.api.light.ItemLight;

@Internal
public class ItemLightDeserializer {
	private ItemLightDeserializer() { }

	public static ItemLight deserialize(InputStreamReader reader) {
		final JsonObject obj = GsonHelper.parse(reader);

		final float intensity = GsonHelper.getAsFloat(obj, "intensity", 0f);

		if (intensity == 0) {
			return ItemLight.NONE;
		}

		final float red = GsonHelper.getAsFloat(obj, "red", 1f);
		final float green = GsonHelper.getAsFloat(obj, "green", 1f);
		final float blue = GsonHelper.getAsFloat(obj, "blue", 1f);
		final boolean worksInFluid = GsonHelper.getAsBoolean(obj, "worksInFluid", true);
		final int innerConeAngleDegrees = GsonHelper.getAsInt(obj, "innerConeAngleDegrees", 360);
		final int outerConeAngleDegrees = GsonHelper.getAsInt(obj, "outerConeAngleDegrees", innerConeAngleDegrees);

		return ItemLight.of(intensity, red, green, blue, worksInFluid, innerConeAngleDegrees, outerConeAngleDegrees);
	}
}
