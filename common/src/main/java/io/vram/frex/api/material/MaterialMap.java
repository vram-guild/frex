/*
 * Copyright © Contributing Authors
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
 *
 * Additional copyright and licensing notices may apply for content that was
 * included from other projects. For more information, see ATTRIBUTION.md.
 */

package io.vram.frex.api.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import io.vram.frex.impl.material.MaterialMapLoader;

public interface MaterialMap {
	/**
	 * Used by renderer to avoid overhead of sprite reverse lookup when not needed.
	 * @return true if map is sprite-sensitive, false if always returns same material
	 */
	boolean needsSprite();

	/**
	 * Returns null if sprite is unmapped or if this is the default material map.
	 */
	@Nullable RenderMaterial getMapped(@Nullable TextureAtlasSprite sprite);

	static MaterialMap get(BlockState state) {
		return MaterialMapLoader.INSTANCE.get(state);
	}

	static MaterialMap get(FluidState fluidState) {
		return MaterialMapLoader.INSTANCE.get(fluidState);
	}

	static MaterialMap getForParticle(ParticleType<?> particleType) {
		return MaterialMapLoader.INSTANCE.get(particleType);
	}

	static MaterialMap defaultMaterialMap() {
		return MaterialMapLoader.DEFAULT_MAP;
	}

	static MaterialMap get(ItemStack itemStack) {
		return MaterialMapLoader.INSTANCE.get(itemStack);
	}
}
