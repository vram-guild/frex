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

package io.vram.frex.api.texture;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.impl.texture.SpriteInjectorImpl;

/**
 * Use to add sprites to a sprite atlas that may not be automatically
 * included as part of a model.
 */
@FunctionalInterface
public interface SpriteInjector {
	/**
	 * Adds a sprite to the sprite atlas identified in {@link #injectOnAtlasStitch(ResourceLocation, Consumer)}.
	 *
	 * @param spriteId The sprite to add - will be ignored if already present
	 */
	void inject(ResourceLocation spriteId);

	/**
	 * Adds a sprite to the identified sprite atlas every time the atlas is stitched.
	 * This method is less trouble than {@link #injectOnAtlasStitch(ResourceLocation, SpriteInjector)}
	 * for sprites that should always be added.
	 *
	 * @param atlasId Target sprite atlas
	 * @param spriteId The sprite to add - will be ignored if already present
	 */
	static void injectAlways(ResourceLocation atlasId, ResourceLocation spriteId) {
		SpriteInjectorImpl.injectAlways(atlasId, spriteId);
	}

	static void injectAlways(ResourceLocation atlasId, String spriteId) {
		injectAlways(atlasId, new ResourceLocation(spriteId));
	}

	/**
	 * Registers a listener that receives a sprite injector when the target atlas is about to be stitched.
	 * Use this method when the sprites to be added may be dynamic.
	 *
	 * @param atlasId Target sprite atlas
	 * @param listener Listener that will accept an injector instance at stitch time
	 */
	static void injectOnAtlasStitch(ResourceLocation atlasId, Consumer<SpriteInjector> listener) {
		SpriteInjectorImpl.injectOnAtlasStitch(atlasId, listener);
	}
}
