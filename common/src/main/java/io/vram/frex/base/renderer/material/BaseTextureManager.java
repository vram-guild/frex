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

package io.vram.frex.base.renderer.material;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.renderer.MaterialTextureManager;
import io.vram.frex.api.texture.MaterialTexture;
import io.vram.frex.impl.FrexLog;

public class BaseTextureManager implements MaterialTextureManager {
	protected int nextIndex = 2;
	protected boolean shouldWarn = true;

	protected final BaseMaterialTexture[] STATES = new BaseMaterialTexture[MaterialConstants.MAX_TEXTURE_STATES];
	protected final Object2ObjectOpenHashMap<ResourceLocation, BaseMaterialTexture> MAP = new Object2ObjectOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);
	protected final BaseMaterialTexture MISSING = new BaseMaterialTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);
	protected final BaseMaterialTexture NONE = new BaseMaterialTexture(1, TextureManager.INTENTIONAL_MISSING_TEXTURE);

	public BaseTextureManager() {
		STATES[0] = MISSING;
		STATES[1] = NONE;
		MAP.defaultReturnValue(MISSING);
	}

	@Override
	public BaseMaterialTexture textureFromIndex(int index) {
		return STATES[index];
	}

	@Override
	public synchronized BaseMaterialTexture textureFromId(ResourceLocation id) {
		BaseMaterialTexture state = MAP.get(id);

		if (state == MISSING && !id.equals(TextureManager.INTENTIONAL_MISSING_TEXTURE)) {
			if (nextIndex >= MaterialConstants.MAX_TEXTURE_STATES) {
				if (shouldWarn) {
					shouldWarn = false;
					FrexLog.warn(String.format("Maximum unique textures (%d) exceeded when attempting to add %s.  Missing texture will be used.", MaterialConstants.MAX_TEXTURE_STATES, id.toString()));
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

	@Override
	public MaterialTexture noTexture() {
		return NONE;
	}
}
