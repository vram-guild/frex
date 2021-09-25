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

package io.vram.frex.api.rendertype;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.impl.material.RenderTypeUtilImpl;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.client.renderer.RenderType;

@NonExtendable
public interface RenderTypeUtil {
	static boolean toMaterialFinder(MaterialFinder finder, RenderType renderType) {
		return RenderTypeUtilImpl.toMaterialFinder(finder, renderType);
	}

	static RenderMaterial toMaterial(RenderType renderType) {
		return toMaterial(renderType, false);
	}

	static RenderMaterial toMaterial(RenderType renderType, boolean foilOverlay) {
		return RenderTypeUtilImpl.toMaterial(renderType, foilOverlay);
	}
}
