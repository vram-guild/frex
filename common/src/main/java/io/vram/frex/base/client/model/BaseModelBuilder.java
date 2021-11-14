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

package io.vram.frex.base.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.model.util.BakedModelUtil;

/**
 * Can be used for multiple blocks - will return same baked model for each.
 */
@SuppressWarnings("unchecked")
public abstract class BaseModelBuilder<T extends BaseModelBuilder<T>> implements UnbakedModel {
	protected boolean useAmbientOcclusion = true;
	protected boolean isGui3d = true;
	protected boolean usesBlockLight = true;
	protected ResourceLocation defaultParticleSprite = MissingTextureAtlasSprite.getLocation();
	protected ItemTransforms itemTransforms = BakedModelUtil.MODEL_TRANSFORM_BLOCK;
	protected ObjectArrayList<ResourceLocation> modelDependencies;
	protected ObjectArrayList<Material> materials;
	protected BakedModel result = null;

	public T useAmbientOcclusion(boolean enable) {
		useAmbientOcclusion = enable;
		return (T) this;
	}

	public T isGui3d(boolean enable) {
		isGui3d = enable;
		return (T) this;
	}

	public T usesBlockLight(boolean enable) {
		usesBlockLight = enable;
		return (T) this;
	}

	public T defaultParticleSprite(ResourceLocation sprite) {
		defaultParticleSprite = sprite;
		return (T) this;
	}

	public T defaultParticleSprite(String sprite) {
		defaultParticleSprite = new ResourceLocation(sprite);
		return (T) this;
	}

	public T defaultParticleSprite(TextureAtlasSprite sprite) {
		defaultParticleSprite = sprite.getName();
		return (T) this;
	}

	public T withModelDependencies(ResourceLocation... dependencies) {
		var modelDependencies = this.modelDependencies;

		if (modelDependencies == null) {
			modelDependencies = new ObjectArrayList<>();
			this.modelDependencies = modelDependencies;
		}

		for (final var d : dependencies) {
			modelDependencies.add(d);
		}

		return (T) this;
	}

	public T withSprites(ResourceLocation... sprites) {
		var materials = this.materials;

		if (materials == null) {
			materials = new ObjectArrayList<>();
			this.materials = materials;
		}

		for (final var s : sprites) {
			materials.add(new Material(TextureAtlas.LOCATION_BLOCKS, s));
		}

		return (T) this;
	}

	public T withItemTransforms(ItemTransforms itemTransforms) {
		this.itemTransforms = itemTransforms;
		return (T) this;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return modelDependencies == null ? Collections.emptyList() : modelDependencies;
	}

	@Override
	public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelFunction, Set<Pair<String, String>> errors) {
		return materials == null ? Collections.emptyList() : materials;
	}

	protected abstract BakedModel bakeOnce(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation);

	@Override
	public final BakedModel bake(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation) {
		var result = this.result;

		if (result == null) {
			result = bakeOnce(bakery, spriteFunc, modelState, modelLocation);
			this.result = result;
		}

		return result;
	}
}
