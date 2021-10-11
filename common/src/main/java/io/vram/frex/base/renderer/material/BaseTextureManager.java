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

package io.vram.frex.base.renderer.material;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.renderer.MaterialTextureManager;
import io.vram.frex.api.texture.MaterialTexture;

import grondag.frex.Frex;

public class BaseTextureManager implements MaterialTextureManager {
	protected int nextIndex = 1;
	protected boolean shouldWarn = true;

	protected final BaseMaterialTexture[] STATES = new BaseMaterialTexture[MaterialConstants.MAX_TEXTURE_STATES];
	protected final Object2ObjectOpenHashMap<ResourceLocation, BaseMaterialTexture> MAP = new Object2ObjectOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);
	protected final BaseMaterialTexture MISSING = new BaseMaterialTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);

	public BaseTextureManager() {
		STATES[0] = MISSING;
		MAP.defaultReturnValue(MISSING);
	}

	@Override
	public BaseMaterialTexture textureByIndex(int index) {
		return STATES[index];
	}

	// PERF: use cow or other method to avoid synch
	@Override
	public synchronized BaseMaterialTexture textureById(ResourceLocation id) {
		BaseMaterialTexture state = MAP.get(id);

		if (state == MISSING && !id.equals(TextureManager.INTENTIONAL_MISSING_TEXTURE)) {
			if (nextIndex >= MaterialConstants.MAX_TEXTURE_STATES) {
				if (shouldWarn) {
					shouldWarn = false;
					Frex.LOG.warn(String.format("Maximum unique textures (%d) exceeded when attempting to add %s.  Missing texture will be used.", MaterialConstants.MAX_TEXTURE_STATES, id.toString()));
				}

				return MISSING;
			}

			final int index = nextIndex++;
			state = new BaseMaterialTexture(index, id);
			MAP.put(id, state);
			STATES[index] = state;
		}

		return state;
	}

	@Override
	public MaterialTexture missingTexture() {
		return MISSING;
	}
}
