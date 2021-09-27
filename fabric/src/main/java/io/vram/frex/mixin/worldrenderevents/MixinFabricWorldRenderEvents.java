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

import java.util.function.Function;

import io.vram.frex.compat.fabric.FabricWorldRenderEventBypass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Mixin(WorldRenderEvents.class)
public class MixinFabricWorldRenderEvents {
	@SuppressWarnings("unchecked")
	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/EventFactory;createArrayBacked(Ljava/lang/Class;Ljava/lang/Object;Ljava/util/function/Function;)Lnet/fabricmc/fabric/api/event/Event;", remap = false), require = 1)
	private static <T> Event<T> onCreateArrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		if (type == WorldRenderEvents.Start.class) {
			return (Event<T>) FabricWorldRenderEventBypass.START;
		} else if (type == WorldRenderEvents.AfterSetup.class) {
			return (Event<T>) FabricWorldRenderEventBypass.AFTER_SETUP;
		} else if (type == WorldRenderEvents.BeforeEntities.class) {
			return (Event<T>) FabricWorldRenderEventBypass.BEFORE_ENTITIES;
		} else if (type == WorldRenderEvents.AfterEntities.class) {
			return (Event<T>) FabricWorldRenderEventBypass.AFTER_ENTITIES;
		} else if (type == WorldRenderEvents.BeforeBlockOutline.class) {
			return (Event<T>) FabricWorldRenderEventBypass.BEFORE_BLOCK_OUTLINE;
		} else if (type == WorldRenderEvents.BlockOutline.class) {
			return (Event<T>) FabricWorldRenderEventBypass.BLOCK_OUTLINE;
		} else if (type == WorldRenderEvents.DebugRender.class) {
			return (Event<T>) FabricWorldRenderEventBypass.BEFORE_DEBUG_RENDER;
		} else if (type == WorldRenderEvents.AfterTranslucent.class) {
			return (Event<T>) FabricWorldRenderEventBypass.AFTER_TRANSLUCENT;
		} else if (type == WorldRenderEvents.Last.class) {
			return (Event<T>) FabricWorldRenderEventBypass.LAST;
		} else if (type == WorldRenderEvents.End.class) {
			return (Event<T>) FabricWorldRenderEventBypass.END;
		}

		return EventFactory.createArrayBacked(type, emptyInvoker, invokerFactory);
	}
}
