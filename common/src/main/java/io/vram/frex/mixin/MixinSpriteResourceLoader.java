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

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.frex.impl.texture.SpriteInjectorImpl;

@Mixin(SpriteResourceLoader.class)
public class MixinSpriteResourceLoader {
	@Inject(at = @At(value = "RETURN"), method = "load", locals = LocalCapture.CAPTURE_FAILHARD)
	private static void afterLoad(ResourceManager resourceManager, ResourceLocation resourceLocation, CallbackInfoReturnable<SpriteResourceLoader> cir, ResourceLocation resourceLocation2, List<SpriteSource> list) {
		SpriteInjectorImpl.forEachInjection(resourceLocation, e -> list.add(new SingleFile(resourceLocation, Optional.of(e))));
	}
}
