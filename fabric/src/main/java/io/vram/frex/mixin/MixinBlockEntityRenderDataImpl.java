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

package io.vram.frex.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.level.block.entity.BlockEntity;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

import io.vram.frex.impl.world.BlockEntityRenderDataImpl;

@Mixin(BlockEntityRenderDataImpl.class)
public class MixinBlockEntityRenderDataImpl {
	private static final Function<BlockEntity, Object> FABRIC_PROVIDER = be -> ((RenderAttachmentBlockEntity) be).getRenderAttachmentData();

	/**
	 * @author Grondag
	 * @reason how we control interop on FAPI
	 */
	@Overwrite(remap = false)
	public static Function<BlockEntity, Object> defaultProvider() {
		return FABRIC_PROVIDER;
	}
}
