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

package io.vram.frex.impl.material;

import java.util.IdentityHashMap;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.material.MaterialTransform;
import io.vram.frex.api.material.RenderMaterial;

@Internal
class DefaultedMultiMaterialMap<T> implements MaterialMap<T> {
	protected final IdentityHashMap<TextureAtlasSprite, RenderMaterial> spriteMap;
	protected final RenderMaterial defaultMaterial;

	DefaultedMultiMaterialMap(RenderMaterial defaultMaterial, IdentityHashMap<TextureAtlasSprite, RenderMaterial> spriteMap) {
		this.defaultMaterial = defaultMaterial;
		this.spriteMap = spriteMap;
	}

	@Override
	public boolean needsSprite() {
		return true;
	}

	@Override
	public void map(MaterialFinder finder, T gameObject, @Nullable TextureAtlasSprite sprite) {
		final MaterialTransform result = spriteMap.getOrDefault(sprite, defaultMaterial);

		if (result != null) {
			result.apply(finder);
		}
	}
}
