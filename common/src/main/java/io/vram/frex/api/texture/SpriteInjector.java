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

package io.vram.frex.api.texture;

import java.util.function.Consumer;

import net.minecraft.util.Identifier;

@FunctionalInterface
public interface SpriteInjector {
	void inject(Identifier spriteId, boolean includeColor);

	default void inject (Identifier spriteId) {
		inject (spriteId, true);
	}

	static void register(Identifier atlasId, Consumer<SpriteInjector> listener) {
		// TODO
	}

	static void forEachColorSprite(Identifier atlasId, Consumer<Identifier> consumer) {
		// TODO
	}

	static void forEachPhysicalSprite(Identifier atlasId, Consumer<Identifier> consumer) {
		// TODO
	}
}
