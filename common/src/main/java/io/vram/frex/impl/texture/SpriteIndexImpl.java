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

package io.vram.frex.impl.texture;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.texture.SpriteIndex;

public class SpriteIndexImpl implements SpriteIndex {
	private static final Object2ObjectOpenHashMap<ResourceLocation, SpriteIndexImpl> MAP = new Object2ObjectOpenHashMap<>(64, Hash.VERY_FAST_LOAD_FACTOR);

	public static final SpriteIndexImpl getOrCreate(ResourceLocation id) {
		return MAP.computeIfAbsent(id, SpriteIndexImpl::new);
	}

	private ObjectArrayList<TextureAtlasSprite> spriteIndexList = null;
	private TextureAtlas atlas;
	private int atlasWidth;
	private int atlasHeight;
	public final ResourceLocation id;

	private SpriteIndexImpl(ResourceLocation id) {
		this.id = id;
	}

	public void reset(SpriteLoader.Preparations dataIn, ObjectArrayList<TextureAtlasSprite> spriteIndexIn, TextureAtlas atlasIn) {
		atlas = atlasIn;

		spriteIndexList = spriteIndexIn;
		atlasWidth = dataIn.width();
		atlasHeight = dataIn.height();
	}

	@Override
	public TextureAtlasSprite fromIndex(int spriteId) {
		return spriteIndexList.get(spriteId);
	}

	@Override
	public float mapU(int spriteId, float unmappedU) {
		final TextureAtlasSprite sprite = spriteIndexList.get(spriteId);
		final float u0 = sprite.getU0();
		return u0 + unmappedU * (sprite.getU1() - u0);
	}

	@Override
	public float mapV(int spriteId, float unmappedV) {
		final TextureAtlasSprite sprite = spriteIndexList.get(spriteId);
		final float v0 = sprite.getV0();
		return v0 + unmappedV * (sprite.getV1() - v0);
	}

	@Override
	public int atlasWidth() {
		return atlasWidth;
	}

	@Override
	public int atlasHeight() {
		return atlasHeight;
	}

	@Override
	public TextureAtlas atlas() {
		return atlas;
	}
}
