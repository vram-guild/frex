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

package io.vram.frex.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import io.vram.frex.impl.texture.IndexedSprite;

@Mixin(TextureAtlasSprite.class)
public class MixinTextureAtlasSprite implements IndexedSprite {
	private int frexIndex;

	@Override
	public int frex_index() {
		return frexIndex;
	}

	@Override
	public void frex_index(int index) {
		frexIndex = index;
	}
}
