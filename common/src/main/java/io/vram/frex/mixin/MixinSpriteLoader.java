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
import java.util.concurrent.Executor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import io.vram.frex.impl.texture.IndexedSprite;
import io.vram.frex.impl.texture.SpriteIndexImpl;
import io.vram.frex.mixinterface.SpriteLoaderExt;

@Mixin(SpriteLoader.class)
public class MixinSpriteLoader implements SpriteLoaderExt {
	@Unique
	private TextureAtlas frx_textureAtlas;

	@Inject(at = @At("RETURN"), method = "create")
	private static void onCreate(TextureAtlas textureAtlas, CallbackInfoReturnable<SpriteLoader> cir) {
		((SpriteLoaderExt) cir.getReturnValue()).frx_acceptAtlas(textureAtlas);
	}

	@Inject(at = @At("RETURN"), method = "stitch")
	private void afterStitch(List<SpriteContents> list, int i, Executor executor, CallbackInfoReturnable<SpriteLoader.Preparations> cir) {
		final var preparations = cir.getReturnValue();

		final ObjectArrayList<TextureAtlasSprite> spriteIndexList = new ObjectArrayList<>();
		int index = 0;

		for (final TextureAtlasSprite sprite : preparations.regions().values()) {
			spriteIndexList.add(sprite);
			final var spriteExt = (IndexedSprite) sprite;
			spriteExt.frex_index(index++);
		}

		SpriteIndexImpl.getOrCreate(frx_textureAtlas.location()).reset(preparations, spriteIndexList, frx_textureAtlas);

		this.frx_textureAtlas = null;
	}

	@Override
	public void frx_acceptAtlas(TextureAtlas atlas) {
		frx_textureAtlas = atlas;
	}
}
