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

package io.vram.frex.api.texture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.renderer.Renderer;

public interface MaterialTexture {
	ResourceLocation id();

	int index();

	AbstractTexture texture();

	boolean isAtlas();

	TextureAtlas textureAsAtlas();

	@Nullable SpriteFinder spriteFinder();

	@Nullable SpriteIndex spriteIndex();

	static MaterialTexture missing() {
		return Renderer.get().textures().missingTexture();
	}

	static MaterialTexture none() {
		return Renderer.get().textures().noTexture();
	}

	static MaterialTexture fromIndex(int index) {
		return Renderer.get().textures().textureFromIndex(index);
	}

	static MaterialTexture fromId(ResourceLocation id) {
		return Renderer.get().textures().textureFromId(id);
	}
}
