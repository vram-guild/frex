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

package io.vram.frex.mixin;

import io.vram.frex.api.renderer.Renderer;
import io.vram.frex.compat.fabric.FabricRenderer;
import io.vram.frex.impl.RendererInitializerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;

import grondag.frex.Frex;

@Mixin(Frex.class)
public abstract class MixinFrex {
	/**
	 * @author grondag
	 * @reason Fabric API compatibility
	 */
	@Overwrite(remap = false)
	private static void setupRenderer() {
		if (RendererInitializerImpl.hasCandidate()) {
			RendererAccess.INSTANCE.registerRenderer(FabricRenderer.of(Renderer.get()));
		}
	}
}
