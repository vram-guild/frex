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

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.renderloop.BlockOutlineListener;
import io.vram.frex.api.renderloop.BlockOutlinePreListener;
import io.vram.frex.api.renderloop.DebugRenderListener;
import io.vram.frex.api.renderloop.EntityRenderPostListener;
import io.vram.frex.api.renderloop.EntityRenderPreListener;
import io.vram.frex.api.renderloop.FrustumSetupListener;
import io.vram.frex.api.renderloop.TranslucentPostListener;
import io.vram.frex.api.renderloop.WorldRenderContextBase;
import io.vram.frex.api.renderloop.WorldRenderLastListener;
import io.vram.frex.api.renderloop.WorldRenderPostListener;
import io.vram.frex.api.renderloop.WorldRenderStartListener;

// Only loaded when Fabric API is not present
@Mixin(LevelRenderer.class)
public class MixinLevelRendererEvents {
	@Shadow private RenderBuffers renderBuffers;
	@Shadow private ClientLevel level;
	@Shadow private PostChain transparencyChain;
	@Shadow private Minecraft minecraft;

	@Unique private final WorldRenderContextBase context = new WorldRenderContextBase();
	@Unique private boolean didRenderParticles;

	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void beforeRenderLevel(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		context.prepare((LevelRenderer) (Object) this, matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f, renderBuffers.bufferSource(), level.getProfiler(), transparencyChain != null, level);
		WorldRenderStartListener.invoke(context);
		didRenderParticles = false;
	}

	@Inject(method = "setupRender", at = @At("RETURN"))
	private void afterSetupRender(Camera camera, Frustum frustum, boolean bl, boolean bl2, CallbackInfo ci) {
		context.setFrustum(frustum);
		FrustumSetupListener.invoke(context);
	}

	@Inject(
			method = "renderLevel",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V",
				ordinal = 2,
				shift = Shift.AFTER
			)
	)
	private void afterTerrainSolid(CallbackInfo ci) {
		EntityRenderPreListener.invoke(context);
	}

	@Inject(method = "renderLevel", at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0))
	private void afterEntities(CallbackInfo ci) {
		EntityRenderPostListener.invoke(context);
	}

	@Inject(
			method = "renderLevel",
			at = @At(
				value = "FIELD",
				target = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;",
				shift = At.Shift.AFTER,
				ordinal = 1
			)
	)
	private void beforeRenderOutline(CallbackInfo ci) {
		context.renderBlockOutline = BlockOutlinePreListener.invoke(context, minecraft.hitResult);
	}

	@Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
	private void onRenderHitOutline(PoseStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (!context.renderBlockOutline) {
			// Was cancelled before we got here, so do not
			// fire the BLOCK_OUTLINE event per contract of the API.
			ci.cancel();
		} else {
			context.prepareBlockOutline(entity, cameraX, cameraY, cameraZ, blockPos, blockState);

			if (!BlockOutlineListener.invoke(context, context)) {
				ci.cancel();
			}

			// The immediate mode VertexConsumers use a shared buffer, so we have to make sure that the immediate mode VCP
			// can accept block outline lines rendered to the existing vertexConsumer by the vanilla block overlay.
			context.consumers().getBuffer(RenderType.lines());
		}
	}

	@Inject(
			method = "renderLevel",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V",
				ordinal = 0
			)
	)
	private void beforeDebugRender(CallbackInfo ci) {
		DebugRenderListener.invoke(context);
	}

	@Inject(
			method = "renderLevel",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V"
			)
	)
	private void onRenderParticles(CallbackInfo ci) {
		// set a flag so we know the next pushMatrix call is after particles
		didRenderParticles = true;
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
	private void beforeClouds(CallbackInfo ci) {
		if (didRenderParticles) {
			didRenderParticles = false;
			TranslucentPostListener.invoke(context);
		}
	}

	@Inject(
			method = "renderLevel",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lnet/minecraft/client/Camera;)V"
			)
	)
	private void onChunkDebugRender(CallbackInfo ci) {
		WorldRenderLastListener.invoke(context);
	}

	@Inject(method = "renderLevel", at = @At("RETURN"))
	private void afterRender(CallbackInfo ci) {
		WorldRenderPostListener.invoke(context);
	}
}
