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

package io.vram.frex.fabric.mixin.events;

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
