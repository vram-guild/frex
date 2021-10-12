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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.texture.SpriteFinder;
import io.vram.frex.base.renderer.util.ResourceCache;

import grondag.frex.Frex;

public class MaterialTexture {
	public final int index;
	public final ResourceLocation id;
	private final ResourceCache<Info> info = new ResourceCache<>(this::retrieveInfo);

	private record Info(AbstractTexture texture, boolean isAtlas, SpriteFinder spriteFinder) { }

	private MaterialTexture(int index, ResourceLocation id) {
		this.index = index;
		this.id = id;
	}

	private Info retrieveInfo() {
		final TextureManager tm = Minecraft.getInstance().getTextureManager();
		// forces registration
		tm.bindForSetup(id);
		final var texture = tm.getTexture(id);
		final var isAtlas = texture != null && texture instanceof TextureAtlas;
		final SpriteFinder spriteFinder = isAtlas ? SpriteFinder.get((TextureAtlas) texture) : null;
		return new Info(texture, isAtlas, spriteFinder);
	}

	public SpriteFinder spriteFinder() {
		return info.getOrLoad().spriteFinder;
	}

	public AbstractTexture texture() {
		return info.getOrLoad().texture;
	}

	public TextureAtlas textureAsAtlas() {
		return (TextureAtlas) texture();
	}

	public boolean isAtlas() {
		return info.getOrLoad().isAtlas;
	}

	public static final int MAX_TEXTURE_STATES = 4096;
	private static int nextIndex = 1;
	private static final MaterialTexture[] STATES = new MaterialTexture[MAX_TEXTURE_STATES];
	private static final Object2ObjectOpenHashMap<ResourceLocation, MaterialTexture> MAP = new Object2ObjectOpenHashMap<>(256, Hash.VERY_FAST_LOAD_FACTOR);

	public static final MaterialTexture NO_TEXTURE = new MaterialTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);

	public static final MaterialTexture MISSING;

	static {
		STATES[0] = NO_TEXTURE;
		MAP.defaultReturnValue(NO_TEXTURE);
		MISSING = fromId(TextureManager.INTENTIONAL_MISSING_TEXTURE);
	}

	public static MaterialTexture fromIndex(int index) {
		return STATES[index];
	}

	private static boolean shouldWarn = true;

	// PERF: use cow or other method to avoid synch
	public static synchronized MaterialTexture fromId(ResourceLocation id) {
		MaterialTexture state = MAP.get(id);

		if (state == NO_TEXTURE) {
			if (nextIndex >= MAX_TEXTURE_STATES) {
				if (shouldWarn) {
					shouldWarn = false;
					Frex.LOG.warn(String.format("Maximum unique textures (%d) exceeded when attempting to add %s.  Missing texture will be used.",
							MAX_TEXTURE_STATES, id.toString()));
					Frex.LOG.warn("Previously encountered textures are listed below. Subsequent warnings are suppressed.");

					for (final MaterialTexture extant : STATES) {
						Frex.LOG.info(extant == null ? "Null (this is a bug)" : extant.id.toString());
					}
				}

				return MISSING;
			}

			final int index = nextIndex++;
			state = new MaterialTexture(index, id);
			MAP.put(id, state);
			STATES[index] = state;
		}

		return state;
	}
}
