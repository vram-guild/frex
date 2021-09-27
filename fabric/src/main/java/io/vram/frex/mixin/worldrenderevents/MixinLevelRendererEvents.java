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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import io.vram.frex.api.renderloop.BlockOutlineListener;
import io.vram.frex.api.renderloop.BlockOutlinePreListener;
import io.vram.frex.api.renderloop.DebugRenderListener;
import io.vram.frex.api.renderloop.EntityRenderPostListener;
import io.vram.frex.api.renderloop.EntityRenderPreListener;
import io.vram.frex.api.renderloop.FrustumSetupListener;
import io.vram.frex.api.renderloop.TranslucentPostListener;
import io.vram.frex.api.renderloop.WorldRenderLastListener;
import io.vram.frex.api.renderloop.WorldRenderPostListener;
import io.vram.frex.api.renderloop.WorldRenderStartListener;
import io.vram.frex.impl.renderloop.WorldRenderContextImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

// Only loaded when Fabric API is not present
@Mixin(LevelRenderer.class)
public class MixinLevelRendererEvents {
	@Shadow private RenderBuffers bufferBuilders;
	@Shadow private ClientLevel world;
	@Shadow private PostChain transparencyShader;
	@Shadow private Minecraft client;
	@Unique private final WorldRenderContextImpl context = new WorldRenderContextImpl();
	@Unique private boolean didRenderParticles;

	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void beforeRenderLevel(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		context.prepare((LevelRenderer) (Object) this, matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f, bufferBuilders.bufferSource(), world.getProfiler(), transparencyShader != null, world);
		WorldRenderStartListener.invoke(context);
		didRenderParticles = false;
	}

	@Inject(method = "setupRender", at = @At("RETURN"))
	private void afterSetupRender(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator, CallbackInfo ci) {
		context.setFrustum(frustum);
		FrustumSetupListener.invoke(context);
	}

	@Inject(
			method = "renderLevel",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
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
		context.renderBlockOutline = BlockOutlinePreListener.invoke(context, client.hitResult);
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
