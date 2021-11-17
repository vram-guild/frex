/*
 * Copyright © Original Authors
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

package io.vram.frex.impl.world;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Internal
public class BlockEntityRenderDataImpl {
	public static void registerProvider(BlockEntityType<?> type, Function<BlockEntity, Object> provider) {
		((BlockEntityRenderDataProviderAccess) type).frxSetDataProvider(provider);
	}

	public static Object get(BlockEntity entity) {
		return ((BlockEntityRenderDataProviderAccess) entity.getType()).frxGetDataProvider().apply(entity);
	}

	private static final Function<BlockEntity, Object> DEFAULT_PROVIDER = be -> null;

	/** Overwritten via mixin on Fabric to usse Fabric interface. */
	public static Function<BlockEntity, Object> defaultProvider() {
		return DEFAULT_PROVIDER;
	}
}
