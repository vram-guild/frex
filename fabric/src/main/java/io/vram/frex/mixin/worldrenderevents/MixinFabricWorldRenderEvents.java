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

package io.vram.frex.mixin.worldrenderevents;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import io.vram.frex.compat.fabric.FabricWorldRenderEventBypass;

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
