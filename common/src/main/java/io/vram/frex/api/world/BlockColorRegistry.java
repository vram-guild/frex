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

package io.vram.frex.api.world;

import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;

import io.vram.frex.impl.world.ColorRegistryImpl;

/**
 * Use this to safely register block colors during initialization
 * when the vanilla instances may not yet exist. Guarantees the registrations
 * will take place before rendering starts, irrespective of initialization order.
 */
@NonExtendable
public interface BlockColorRegistry {
	static void register(BlockColor blockColor, Block... blocks) {
		ColorRegistryImpl.register(blockColor, blocks);
	}

	/**
	 * Convenient access for the default block colors instance.
	 * Will be null until the game client initialization creates it.
	 */
	static @Nullable BlockColors get() {
		return ColorRegistryImpl.getBlockColors();
	}
}
