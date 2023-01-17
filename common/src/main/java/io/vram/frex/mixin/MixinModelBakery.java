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

/*
 * This particular set of hooks was adapted from Fabric, and was originally
 * written by Asie Kierka if memory serves. The Fabric copyright and
 * license notice is reproduced below:
 *
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vram.frex.mixin;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.impl.model.ModelProviderRegistryImpl;
import io.vram.frex.mixinterface.ModelBakeryExt;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery implements ModelBakeryExt {
	@Shadow @Final private Map<ResourceLocation, BlockModel> modelResources;
	@Shadow @Final private Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources;
	@Shadow @Final private Set<ResourceLocation> loadingStack;
	@Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
	@Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;

	@Unique private ModelProviderRegistryImpl.LoaderInstance frexHandler;

	@Shadow private void loadTopLevel(ModelResourceLocation id) { }
	@Shadow private void cacheAndQueueDependencies(ResourceLocation id, UnbakedModel unbakedModel) { }
	@Shadow private void loadModel(ResourceLocation id) { }
	@Shadow public abstract UnbakedModel getModel(ResourceLocation id);

	private ModelProviderRegistryImpl.LoaderInstance frexHandler() {
		var result = frexHandler;

		if (result == null) {
			result = ModelProviderRegistryImpl.begin((ModelBakery) (Object) this, modelResources, blockStateResources);
			frexHandler = result;
			ModelProviderRegistryImpl.onModelPopulation(modelResources, blockStateResources, this::frx_addModel);
		}

		return result;
	}

	@Inject(at = @At("HEAD"), method = "loadModel", cancellable = true)
	private void loadModelHook(ResourceLocation id, CallbackInfo ci) {
		final UnbakedModel customModel = frexHandler().loadModelFromVariant(id);

		if (customModel != null) {
			cacheAndQueueDependencies(id, customModel);
			ci.cancel();
		}
	}

	@Inject(at = @At("RETURN"), method = "<init>")
	private void initFinishedHook(CallbackInfo info) {
		frexHandler().finish();
	}

	@Override @Unique
	public void frx_addModel(ResourceLocation id) {
		if (id instanceof ModelResourceLocation) {
			loadTopLevel((ModelResourceLocation) id);
		} else {
			// The vanilla addModel method is arbitrarily limited to ModelIdentifiers,
			// but it's useful to tell the game to just load and bake a direct model path as well.
			// Replicate the vanilla logic of addModel here.
			final UnbakedModel unbakedModel = getModel(id);
			this.unbakedCache.put(id, unbakedModel);
			this.topLevelModels.put(id, unbakedModel);
		}
	}

	@Override @Unique
	public UnbakedModel frx_loadModel(ResourceLocation id) {
		if (!loadingStack.add(id)) {
			throw new IllegalStateException("Circular reference while loading model " + id);
		}

		loadModel(id);
		loadingStack.remove(id);
		return unbakedCache.get(id);
	}
}
