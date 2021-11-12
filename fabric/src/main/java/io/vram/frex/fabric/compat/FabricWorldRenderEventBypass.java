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

package io.vram.frex.fabric.compat;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;

import io.vram.frex.api.renderloop.BlockOutlineListener;
import io.vram.frex.api.renderloop.BlockOutlineListener.BlockOutlineContext;
import io.vram.frex.api.renderloop.BlockOutlinePreListener;
import io.vram.frex.api.renderloop.DebugRenderListener;
import io.vram.frex.api.renderloop.EntityRenderPostListener;
import io.vram.frex.api.renderloop.EntityRenderPreListener;
import io.vram.frex.api.renderloop.FrustumSetupListener;
import io.vram.frex.api.renderloop.TranslucentPostListener;
import io.vram.frex.api.renderloop.WorldRenderContext;
import io.vram.frex.api.renderloop.WorldRenderLastListener;
import io.vram.frex.api.renderloop.WorldRenderPostListener;
import io.vram.frex.api.renderloop.WorldRenderStartListener;

public class FabricWorldRenderEventBypass {
	public static final Event<WorldRenderEvents.Start> START = new Event<>() {
		{
			this.invoker = ctx -> WorldRenderStartListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.Start listener) {
			WorldRenderStartListener.register(ctx -> listener.onStart((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};

	public static final Event<WorldRenderEvents.AfterSetup> AFTER_SETUP = new Event<>() {
		{
			this.invoker = ctx -> FrustumSetupListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.AfterSetup listener) {
			FrustumSetupListener.register(ctx -> listener.afterSetup((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};

	public static final Event<WorldRenderEvents.BeforeEntities> BEFORE_ENTITIES = new Event<>() {
		{
			this.invoker = ctx -> EntityRenderPreListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.BeforeEntities listener) {
			EntityRenderPreListener.register(ctx -> listener.beforeEntities((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};

	public static final Event<WorldRenderEvents.AfterEntities> AFTER_ENTITIES = new Event<>() {
		{
			this.invoker = ctx -> EntityRenderPostListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.AfterEntities listener) {
			EntityRenderPostListener.register(ctx -> listener.afterEntities((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};

	public static final Event<WorldRenderEvents.BeforeBlockOutline> BEFORE_BLOCK_OUTLINE = new Event<>() {
		{
			this.invoker = (ctx, hit) -> BlockOutlinePreListener.invoke((WorldRenderContext) ctx, hit);
		}

		@Override
		public void register(WorldRenderEvents.BeforeBlockOutline listener) {
			BlockOutlinePreListener.register((ctx, hit) -> listener.beforeBlockOutline((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx, hit));
		}
	};

	public static final Event<WorldRenderEvents.BlockOutline> BLOCK_OUTLINE = new Event<>() {
		{
			this.invoker = (wCtx, bCtx) -> BlockOutlineListener.invoke((WorldRenderContext) wCtx, (BlockOutlineContext) bCtx);
		}

		@Override
		public void register(WorldRenderEvents.BlockOutline listener) {
			BlockOutlineListener.register((wCtx, bCtx) -> listener.onBlockOutline((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) wCtx, (net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext) bCtx));
		}
	};

	public static final Event<WorldRenderEvents.DebugRender> BEFORE_DEBUG_RENDER = new Event<>() {
		{
			this.invoker = ctx -> DebugRenderListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.DebugRender listener) {
			DebugRenderListener.register(ctx -> listener.beforeDebugRender((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};

	public static final Event<WorldRenderEvents.AfterTranslucent> AFTER_TRANSLUCENT = new Event<>() {
		{
			this.invoker = ctx -> TranslucentPostListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.AfterTranslucent listener) {
			TranslucentPostListener.register(ctx -> listener.afterTranslucent((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};

	public static final Event<WorldRenderEvents.Last> LAST = new Event<>() {
		{
			this.invoker = ctx -> WorldRenderLastListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.Last listener) {
			WorldRenderLastListener.register(ctx -> listener.onLast((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};

	public static final Event<WorldRenderEvents.End> END = new Event<>() {
		{
			this.invoker = ctx -> WorldRenderPostListener.invoke((WorldRenderContext) ctx);
		}

		@Override
		public void register(WorldRenderEvents.End listener) {
			WorldRenderPostListener.register(ctx -> listener.onEnd((net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext) ctx));
		}
	};
}
