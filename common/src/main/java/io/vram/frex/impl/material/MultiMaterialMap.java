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

package io.vram.frex.impl.material;

import java.util.IdentityHashMap;

import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.material.RenderMaterial;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@Internal
class MultiMaterialMap implements MaterialMap {
	private final IdentityHashMap<TextureAtlasSprite, RenderMaterial> spriteMap;

	MultiMaterialMap(IdentityHashMap<TextureAtlasSprite, RenderMaterial> spriteMap) {
		this.spriteMap = spriteMap;
	}

	@Override
	public boolean needsSprite() {
		return true;
	}

	@Override
	public RenderMaterial getMapped(TextureAtlasSprite sprite) {
		return spriteMap.get(sprite);
	}
}
