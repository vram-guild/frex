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
