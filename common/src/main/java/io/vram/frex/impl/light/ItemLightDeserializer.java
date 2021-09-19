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

package io.vram.frex.impl.light;

import java.io.InputStreamReader;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;
import io.vram.frex.api.light.ItemLight;
import org.jetbrains.annotations.ApiStatus.Internal;

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
