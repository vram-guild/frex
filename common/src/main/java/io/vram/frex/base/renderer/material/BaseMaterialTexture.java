/*
 * Copyright © Original Authors
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.texture.MaterialTexture;
import io.vram.frex.api.texture.SpriteFinder;
import io.vram.frex.api.texture.SpriteIndex;
import io.vram.frex.base.renderer.util.ResourceCache;

public class BaseMaterialTexture implements MaterialTexture {
	protected final int index;
	protected final ResourceLocation id;
	protected final String idAsString;
	protected final ResourceCache<Info> info = new ResourceCache<>(this::retrieveInfo);

	protected record Info(AbstractTexture texture, boolean isAtlas, SpriteFinder spriteFinder, SpriteIndex spriteIndex) { }

	protected BaseMaterialTexture(int index, ResourceLocation id) {
		this.index = index;
		this.id = id;
		idAsString = id.toString();
	}

	protected Info retrieveInfo() {
		final TextureManager tm = Minecraft.getInstance().getTextureManager();
		// forces registration
		tm.bindForSetup(id);
		final var texture = tm.getTexture(id);
		final var isAtlas = texture != null && texture instanceof TextureAtlas;
		final SpriteFinder spriteFinder = isAtlas ? SpriteFinder.get((TextureAtlas) texture) : null;
		final SpriteIndex spriteIndex = isAtlas ? SpriteIndex.getOrCreate(id) : null;
		return new Info(texture, isAtlas, spriteFinder, spriteIndex);
	}

	@Override
	public ResourceLocation id() {
		return id;
	}

	@Override
	public String idAsString() {
		return idAsString;
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public SpriteFinder spriteFinder() {
		return info.getOrLoad().spriteFinder;
	}

	@Override
	public SpriteIndex spriteIndex() {
		return info.getOrLoad().spriteIndex;
	}

	@Override
	public AbstractTexture texture() {
		return info.getOrLoad().texture;
	}

	@Override
	public TextureAtlas textureAsAtlas() {
		return (TextureAtlas) texture();
	}

	@Override
	public boolean isAtlas() {
		return info.getOrLoad().isAtlas;
	}
}
