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

import io.vram.frex.impl.model.FluidAppearanceRegistryImpl;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public interface FluidAppearance extends FluidColorProvider, FluidSpriteProvider {
	static FluidAppearance get(Fluid fluid) {
		return FluidAppearanceRegistryImpl.get(fluid);
	}

	static void register(Fluid fluid, FluidColorProvider colorProvider, FluidSpriteProvider spriteProvider) {
		FluidAppearanceRegistryImpl.register(fluid, colorProvider, spriteProvider);
	}

	static void register(Fluid fluid, int color, FluidSpriteProvider spriteProvider) {
		FluidAppearanceRegistryImpl.register(fluid, (v, p, s) -> color, spriteProvider);
	}

	static void register(Fluid fluid, FluidColorProvider colorProvider, String stillSpriteName, String flowingSpriteName) {
		FluidAppearanceRegistryImpl.register(fluid, colorProvider, new Identifier(stillSpriteName), new Identifier(flowingSpriteName));
	}

	static void register(Fluid fluid, int color, String stillSpriteName, String flowingSpriteName) {
		FluidAppearanceRegistryImpl.register(fluid, (v, p, s) -> color, new Identifier(stillSpriteName), new Identifier(flowingSpriteName));
	}

	static void register(Fluid fluid, FluidColorProvider colorProvider, Identifier stillSpriteName, Identifier flowingSpriteName) {
		FluidAppearanceRegistryImpl.register(fluid, colorProvider, stillSpriteName, flowingSpriteName);
	}

	static void register(Fluid fluid, int color, Identifier stillSpriteName, Identifier flowingSpriteName) {
		FluidAppearanceRegistryImpl.register(fluid, (v, p, s) -> color, stillSpriteName, flowingSpriteName);
	}
}
