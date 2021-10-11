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
		return Renderer.get().textures().textureByIndex(index);
	}

	static MaterialTexture fromId(ResourceLocation id) {
		return Renderer.get().textures().textureById(id);
	}
}
