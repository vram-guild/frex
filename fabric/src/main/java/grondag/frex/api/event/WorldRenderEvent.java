/*
 * Copyright © Original Authors
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

package grondag.frex.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.profiling.ProfilerFiller;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 *
 * @deprecated Migrate to to fabric render events module when available. Or to frex-events as interim. Will be removed for 1.17.
 */
@Deprecated
@Environment(EnvType.CLIENT)
public final class WorldRenderEvent {
	/**
	 * Called at the start of world rendering.
	 */
	@Deprecated
	public static final Event<BeforeWorldRender> BEFORE_WORLD_RENDER = EventFactory.createArrayBacked(BeforeWorldRender.class, callbacks -> (matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f) -> {
		if (EventFactory.isProfilingEnabled()) {
			final ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
			profiler.push("frexBeforeWorldRender");

			for (final BeforeWorldRender event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.beforeWorldRender(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (final BeforeWorldRender event : callbacks) {
				event.beforeWorldRender(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f);
			}
		}
	});

	/**
	 * Called after world rendering.
	 */
	@Deprecated
	public static final Event<AfterWorldRender> AFTER_WORLD_RENDER = EventFactory.createArrayBacked(AfterWorldRender.class, callbacks -> (matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f) -> {
		if (EventFactory.isProfilingEnabled()) {
			final ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
			profiler.push("frexAfterWorldRender");

			for (final AfterWorldRender event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.afterWorldRender(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (final AfterWorldRender event : callbacks) {
				event.afterWorldRender(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, matrix4f);
			}
		}
	});

	@Deprecated
	public interface BeforeWorldRender {
		void beforeWorldRender(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f);
	}

	@Deprecated
	public interface AfterWorldRender {
		void afterWorldRender(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f);
	}
}
