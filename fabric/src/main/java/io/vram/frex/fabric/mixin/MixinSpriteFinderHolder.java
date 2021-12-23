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

package io.vram.frex.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.renderer.texture.TextureAtlas;

import net.fabricmc.fabric.impl.renderer.SpriteFinderImpl;

import io.vram.frex.api.texture.SpriteFinder;
import io.vram.frex.impl.texture.SpriteFinderHolder;

@Mixin(SpriteFinderHolder.class)
public abstract class MixinSpriteFinderHolder {
	/**
	 * We use the Fabric implementation when it is available.
	 * It's the same code either way - I wrote it. (Grondag)
	 *
	 * @author grondag
	 * @reason Fabric API compatibility
	 */
	@Overwrite(remap = false)
	public static SpriteFinder get(TextureAtlas atlas) {
		return (SpriteFinder) SpriteFinderImpl.get(atlas);
	}
}
