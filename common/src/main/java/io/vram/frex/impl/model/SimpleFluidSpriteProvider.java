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

package io.vram.frex.impl.model;

import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;

import io.vram.frex.api.model.fluid.FluidSpriteProvider;

public class SimpleFluidSpriteProvider implements FluidSpriteProvider {
	private final ResourceLocation stillSpriteName;
	private final ResourceLocation flowingSpriteName;
	private final ResourceLocation overlaySpriteName;
	private TextureAtlasSprite[] sprites = null;

	private SimpleFluidSpriteProvider(ResourceLocation stillSpriteName, ResourceLocation flowingSpriteName, @Nullable ResourceLocation overlaySpriteName) {
		this.stillSpriteName = stillSpriteName;
		this.flowingSpriteName = flowingSpriteName;
		this.overlaySpriteName = overlaySpriteName;
		RELOAD_LIST.add(this);
	}

	@Override
	public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
		var result = sprites;

		if (result == null) {
			final boolean overlay = overlaySpriteName != null;
			result = new TextureAtlasSprite[overlay ? 3 : 2];
			final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
			result[0] = atlas.apply(stillSpriteName);
			result[1] = atlas.apply(flowingSpriteName);

			if (overlay) {
				result[2] = atlas.apply(overlaySpriteName);
			}

			sprites = result;
		}

		return result;
	}

	private void onReload() {
		sprites = null;
	}

	public static FluidSpriteProvider of(ResourceLocation stillSpriteName, ResourceLocation flowingSpriteName, @Nullable ResourceLocation overlaySpriteName) {
		return new SimpleFluidSpriteProvider(stillSpriteName, flowingSpriteName, overlaySpriteName);
	}

	private static final ObjectArrayList<SimpleFluidSpriteProvider> RELOAD_LIST = new ObjectArrayList<>();

	public static void reload() {
		for (final var p : RELOAD_LIST) {
			p.onReload();
		}
	}
}
