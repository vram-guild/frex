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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.AtlasSet;

import io.vram.frex.mixinterface.TextureAtlasExt;

@Mixin(AtlasSet.StitchResult.class)
public class MixinStitchResult {

	/**
	 * Perform sprite indexing before model baking.
	 */
	@Inject(at = @At("RETURN"), method = "<init>")
	void onInit(TextureAtlas textureAtlas, SpriteLoader.Preparations preparations, CallbackInfo ci) {
		TextureAtlasExt.resetSpriteIndex(textureAtlas, preparations);
		// Prevent another reset during upload
		((TextureAtlasExt) textureAtlas).frx_signalDidReset();
	}
}