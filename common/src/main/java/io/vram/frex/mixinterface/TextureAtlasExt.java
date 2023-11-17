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

package io.vram.frex.mixinterface;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import io.vram.frex.impl.texture.IndexedSprite;
import io.vram.frex.impl.texture.SpriteIndexImpl;

public interface TextureAtlasExt {
	void frx_signalDidReset();

	static void resetSpriteIndex(TextureAtlas atlas, SpriteLoader.Preparations preparations) {
		final ObjectArrayList<TextureAtlasSprite> spriteIndexList = new ObjectArrayList<>();
		int index = 0;

		for (final TextureAtlasSprite sprite : preparations.regions().values()) {
			spriteIndexList.add(sprite);
			final var spriteExt = (IndexedSprite) sprite;
			spriteExt.frex_index(index++);
		}

		SpriteIndexImpl.getOrCreate(atlas.location()).reset(preparations, spriteIndexList, atlas);
	}
}
