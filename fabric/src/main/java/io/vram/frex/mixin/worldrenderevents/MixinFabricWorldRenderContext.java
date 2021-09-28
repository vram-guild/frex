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

package io.vram.frex.mixin.worldrenderevents;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LightTexture;

import net.fabricmc.fabric.impl.client.rendering.WorldRenderContextImpl;

import io.vram.frex.api.renderloop.BlockOutlineListener.BlockOutlineContext;
import io.vram.frex.api.renderloop.WorldRenderContext;

@Mixin(WorldRenderContextImpl.class)
public abstract class MixinFabricWorldRenderContext implements WorldRenderContext, BlockOutlineContext {
	@Override
	public LightTexture lightmapTexture() {
		return ((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) this).lightmapTextureManager();
	}

	@Override
	public PoseStack poseStack() {
		return ((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) this).matrixStack();
	}
}
