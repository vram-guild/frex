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

package io.vram.frex.base.client.model;

import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;

import io.vram.frex.api.model.BlockItemModel;

public abstract class BaseModel implements BlockItemModel, BakedModel {
	protected final boolean useAmbientOcclusion;
	protected final boolean isGui3d;
	protected final boolean usesBlockLight;
	protected final TextureAtlasSprite defaultParticleSprite;
	protected final ItemOverrideProxy itemProxy = new ItemOverrideProxy(this);
	protected final ItemTransforms itemTransforms;

	protected BaseModel(BaseModelBuilder<?> builder, Function<Material, TextureAtlasSprite> spriteFunc) {
		this.useAmbientOcclusion = builder.useAmbientOcclusion;
		this.isGui3d = builder.isGui3d;
		this.usesBlockLight = builder.usesBlockLight;
		this.defaultParticleSprite = spriteFunc.apply(new Material(TextureAtlas.LOCATION_BLOCKS, builder.defaultParticleSprite));
		this.itemTransforms = builder.itemTransforms;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return useAmbientOcclusion;
	}

	@Override
	public boolean isGui3d() {
		return isGui3d;
	}

	@Override
	public boolean usesBlockLight() {
		return usesBlockLight;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return defaultParticleSprite;
	}

	@Override
	public ItemTransforms getTransforms() {
		return itemTransforms;
	}

	@Override
	public ItemOverrides getOverrides() {
		return itemProxy;
	}
}
