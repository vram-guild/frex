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

package io.vram.frex.impl.texture;

import java.util.function.Function;

import io.vram.frex.api.texture.SpriteFinder;

import net.minecraft.client.texture.SpriteAtlasTexture;

public class SpriteFinderImpl {
	private static Function<SpriteAtlasTexture, SpriteFinder> accessFunction;

	public static void init(Function<SpriteAtlasTexture, SpriteFinder> accessFunction) {
		SpriteFinderImpl.accessFunction = accessFunction;
	}

	public static SpriteFinder get(SpriteAtlasTexture atlas) {
		return accessFunction.apply(atlas);
	}
}
