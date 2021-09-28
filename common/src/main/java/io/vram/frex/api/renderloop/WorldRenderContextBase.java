/*
 * Copyright © Contributing Authors
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

package io.vram.frex.api.renderloop;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.renderloop.BlockOutlineListener.BlockOutlineContext;

/**
 * Recommended base class for renderers that need to provide context objects to world events.
 */
public class WorldRenderContextBase implements BlockOutlineContext, WorldRenderContext {
	private LevelRenderer worldRenderer;
	private PoseStack poseStack;
	private float tickDelta;
	private long limitTime;
	private boolean blockOutlines;
	private Camera camera;
	private Frustum frustum;
	private GameRenderer gameRenderer;
	private LightTexture lightmapTexture;
	private Matrix4f projectionMatrix;
	private MultiBufferSource consumers;
	private ProfilerFiller profiler;
	private boolean advancedTranslucency;
	private ClientLevel world;

	private Entity entity;
	private double cameraX;
	private double cameraY;
	private double cameraZ;
	private BlockPos blockPos;
	private BlockState blockState;

	public boolean renderBlockOutline = true;

	public void prepare(
			LevelRenderer worldRenderer,
			PoseStack poseStack,
			float tickDelta,
			long limitTime,
			boolean blockOutlines,
			Camera camera,
			GameRenderer gameRenderer,
			LightTexture lightmapTexture,
			Matrix4f projectionMatrix,
			MultiBufferSource consumers,
			ProfilerFiller profiler,
			boolean advancedTranslucency,
			ClientLevel world
	) {
		this.worldRenderer = worldRenderer;
		this.poseStack = poseStack;
		this.tickDelta = tickDelta;
		this.limitTime = limitTime;
		this.blockOutlines = blockOutlines;
		this.camera = camera;
		this.gameRenderer = gameRenderer;
		this.lightmapTexture = lightmapTexture;
		this.projectionMatrix = projectionMatrix;
		this.consumers = consumers;
		this.profiler = profiler;
		this.advancedTranslucency = advancedTranslucency;
		this.world = world;
	}

	public void setFrustum(Frustum frustum) {
		this.frustum = frustum;
	}

	public void prepareBlockOutline(
			Entity entity,
			double cameraX,
			double cameraY,
			double cameraZ,
			BlockPos blockPos,
			BlockState blockState
	) {
		this.entity = entity;
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.blockPos = blockPos;
		this.blockState = blockState;
	}

	@Override
	public LevelRenderer worldRenderer() {
		return worldRenderer;
	}

	@Override
	public PoseStack poseStack() {
		return poseStack;
	}

	// FAPI support
	@Override
	public PoseStack matrixStack() {
		return poseStack;
	}

	@Override
	public float tickDelta() {
		return tickDelta;
	}

	@Override
	public long limitTime() {
		return limitTime;
	}

	@Override
	public boolean blockOutlines() {
		return blockOutlines;
	}

	@Override
	public Camera camera() {
		return camera;
	}

	@Override
	public Matrix4f projectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public ClientLevel world() {
		return world;
	}

	@Override
	public Frustum frustum() {
		return frustum;
	}

	@Override
	public MultiBufferSource consumers() {
		return consumers;
	}

	@Override
	public GameRenderer gameRenderer() {
		return gameRenderer;
	}

	@Override
	public LightTexture lightmapTexture() {
		return lightmapTexture;
	}

	// FAPI support
	public LightTexture lightmapTextureManager() {
		return lightmapTexture;
	}

	@Override
	public ProfilerFiller profiler() {
		return profiler;
	}

	@Override
	public boolean advancedTranslucency() {
		return advancedTranslucency;
	}

	// For fabric API support
	public VertexConsumer vertexConsumer() {
		return consumers.getBuffer(RenderType.lines());
	}

	@Override
	public Entity entity() {
		return entity;
	}

	@Override
	public double cameraX() {
		return cameraX;
	}

	@Override
	public double cameraY() {
		return cameraY;
	}

	@Override
	public double cameraZ() {
		return cameraZ;
	}

	@Override
	public BlockPos blockPos() {
		return blockPos;
	}

	@Override
	public BlockState blockState() {
		return blockState;
	}
}
