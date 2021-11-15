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

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.impl.texture.SpriteFinderImpl;

@Mixin(TextureAtlas.class)
public class MixinTextureAltasNoFabric implements SpriteFinderImpl.SpriteFinderAccess {
	@Shadow @Final private Map<ResourceLocation, TextureAtlasSprite> texturesByName;

	private SpriteFinderImpl frex_spriteFinder = null;

	@Override
	public SpriteFinderImpl frex_spriteFinder() {
		SpriteFinderImpl result = frex_spriteFinder;

		if (result == null) {
			result = new SpriteFinderImpl(texturesByName, (TextureAtlas) (Object) this);
			frex_spriteFinder = result;
		}

		return result;
	}

	@Inject(at = @At("RETURN"), method = "reload")
	private void uploadHook(TextureAtlas.Preparations input, CallbackInfo info) {
		frex_spriteFinder = null;
	}
}
