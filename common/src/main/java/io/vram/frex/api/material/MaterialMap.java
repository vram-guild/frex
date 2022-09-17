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

package io.vram.frex.api.material;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import io.vram.frex.impl.material.MaterialMapLoader;

public interface MaterialMap<T> {
	void map(MaterialFinder finder, T gameObject, @Nullable TextureAtlasSprite sprite);

	default void map(MaterialFinder finder, T gameObject) {
		map(finder, gameObject, null);
	}

	/**
	 * Used by renderer to avoid overhead of sprite reverse lookup when not needed.
	 * @return true if map is sprite-sensitive, false if always returns same material
	 */
	default boolean needsSprite() {
		return false;
	}

	/**
	 * Returns null if sprite is unmapped or if this is the default material map.
	 */
	@Deprecated
	default @Nullable RenderMaterial getMapped(@Nullable TextureAtlasSprite sprite) {
		return null;
	}

	@Deprecated
	default RenderMaterial getMapped(RenderMaterial material, T gameObject, MaterialFinder finder) {
		finder.copyFrom(material);
		map(finder, gameObject);
		return finder.find();
	}

	default boolean isIdentity() {
		return this == IDENTITY;
	}

	static MaterialMap<BlockState> get(BlockState state) {
		return MaterialMapLoader.INSTANCE.get(state);
	}

	static MaterialMap<FluidState> get(FluidState fluidState) {
		return MaterialMapLoader.INSTANCE.get(fluidState);
	}

	static MaterialMap<Particle> getForParticle(ParticleType<?> particleType) {
		return MaterialMapLoader.INSTANCE.get(particleType);
	}

	@Deprecated
	static <T> MaterialMap<T> defaultMaterialMap() {
		return identity();
	}

	static MaterialMap<ItemStack> get(ItemStack itemStack) {
		return MaterialMapLoader.INSTANCE.get(itemStack);
	}

	MaterialMap<?> IDENTITY = (f, o, s) -> { };

	@SuppressWarnings("unchecked")
	static <T> MaterialMap<T> identity() {
		return (MaterialMap<T>) IDENTITY;
	}
}
