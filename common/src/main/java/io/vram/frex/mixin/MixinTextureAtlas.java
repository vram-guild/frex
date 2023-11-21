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

package io.vram.frex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;

import io.vram.frex.impl.texture.SpriteFinderImpl;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas implements SpriteFinderImpl.SpriteFinderAccess {
	@Unique
	private SpriteFinderImpl frex_spriteFinder = null;

	@Override
	public SpriteFinderImpl frex_spriteFinder() {
		return frex_spriteFinder;
	}

	@Override
	public void frex_createSpriteFinder(SpriteLoader.Preparations preparations) {
		frex_spriteFinder = new SpriteFinderImpl(preparations.regions(), (TextureAtlas) (Object) this);
	}
}
