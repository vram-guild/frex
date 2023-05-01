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

package io.vram.frex.impl;

import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.frex.api.renderloop.RenderReloadListener;
import io.vram.frex.base.renderer.util.ResourceCache;
import io.vram.frex.impl.light.ItemLightLoader;
import io.vram.frex.impl.material.map.MaterialMapLoader;
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
