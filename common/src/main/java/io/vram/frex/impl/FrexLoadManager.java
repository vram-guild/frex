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

package io.vram.frex.impl;

import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.frex.api.renderloop.RenderReloadListener;
import io.vram.frex.base.renderer.util.ResourceCache;
import io.vram.frex.impl.light.ItemLightLoader;
import io.vram.frex.impl.material.MaterialMapLoader;
import io.vram.frex.impl.model.FluidModelImpl;
import io.vram.frex.impl.model.SimpleFluidSpriteProvider;
import io.vram.frex.pastel.PastelBlockRenderContext;
import io.vram.frex.pastel.PastelEntityBlockRenderContext;
import io.vram.frex.pastel.PastelItemRenderContext;

public class FrexLoadManager {
	public static void firstTimeLoad() {
		RenderReloadListener.register(FrexLoadManager::worldRenderReload);
	}

	public static void worldRenderReload() {
		PastelBlockRenderContext.reload();
		PastelEntityBlockRenderContext.reload();
		PastelItemRenderContext.reload();
	}

	public static void reloadTextureDependencies(ResourceManager resourceManager) {
		MaterialMapLoader.INSTANCE.reload(resourceManager);
		SimpleFluidSpriteProvider.reload();
		FluidModelImpl.reload();
	}

	public static void reloadGeneralDependencies(ResourceManager resourceManager) {
		ItemLightLoader.INSTANCE.reload(resourceManager);
		ResourceCache.invalidateAll();
	}
}
