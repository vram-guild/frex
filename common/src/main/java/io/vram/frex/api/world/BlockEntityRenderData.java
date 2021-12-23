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

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import io.vram.frex.impl.world.BlockEntityRenderDataImpl;

/**
 * Mods should register a provider function for block entities that
 * provide data to be used in rendering so that the data can be
 * retrieved on the main render thread in advance of terrain baking
 * that can occur off-thread.
 *
 * <p>Even if BlockEntity data access is thread-safe, this ensure the
 * block entity instance is created on the main thread.
 *
 * <p>On the Fabric platform, if no provider function is registered and
 * the block entity implements the Fabric interface {@code RenderAttachmentBlockEntity}
 * then the Fabric interface will be used automatically as the fallback provider.
 *
 */
@NonExtendable
public interface BlockEntityRenderData {
	static void registerProvider(BlockEntityType<?> type, Function<BlockEntity, Object> provider) {
		BlockEntityRenderDataImpl.registerProvider(type, provider);
	}

	/** For use by renderers to capture data on main thread. */
	static Object get(BlockEntity entity) {
		return BlockEntityRenderDataImpl.get(entity);
	}
}
