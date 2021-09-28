/*
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.vram.frex.mixin;

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
