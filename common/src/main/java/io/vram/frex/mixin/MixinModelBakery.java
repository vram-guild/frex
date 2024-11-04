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
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.vram.frex.impl.model.ModelProviderRegistryImpl;
import io.vram.frex.impl.model.ModelProviderRegistryImpl.LoaderInstance;
import io.vram.frex.mixinterface.ModelBakeryExt;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery implements ModelBakeryExt {
	@Shadow @Final private Set<ResourceLocation> loadingStack;
	@Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
	@Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;

	@Unique private ModelProviderRegistryImpl.LoaderInstance frexHandler;

	@Shadow public abstract UnbakedModel getModel(ResourceLocation id);

	@Inject(method = "<init>(Lnet/minecraft/client/color/block/BlockColors;Lnet/minecraft/util/profiling/ProfilerFiller;Ljava/util/Map;Ljava/util/Map;)V",
				at = @At(value = "RETURN"))
	private void initFrexHandler(BlockColors arg1, ProfilerFiller arg2, Map<ResourceLocation, BlockModel> arg3, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> arg4, CallbackInfo info) {
		if (frexHandler == null) {
			frexHandler = ModelProviderRegistryImpl.begin((ModelBakery) (Object) this, arg3, arg4);
			ModelProviderRegistryImpl.onModelPopulation(arg3, arg4, this::frx_addModel);
		}
	}

	// TODO: ???
	@Redirect(method = "getModel(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/UnbakedModel;",
				at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;loadBlockModel(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/resources/model/UnbakedModel;"))
	private UnbakedModel loadModelHook(ModelBakery this_, ResourceLocation id) {
		final UnbakedModel customModel = frexHandler.loadModelFromVariant(id);

		if (customModel != null) {
			return customModel;
		}
		return this_.loadBlockModel(id);
	}

	@Inject(at = @At("RETURN"), method = "<init>")
	private void initFinishedHook(CallbackInfo info) {
		frexHandler.finish();
	}

	@Override @Unique
	public void frx_addModel(ResourceLocation id) {
		final UnbakedModel unbakedModel = getModel(id);
		this.unbakedCache.put(id, unbakedModel);
		this.topLevelModels.put(id, unbakedModel);
	}

	@Override @Unique
	public UnbakedModel frx_loadModel(ResourceLocation id) {
		if (!loadingStack.add(id)) {
			throw new IllegalStateException("Circular reference while loading model " + id);
		}

		// TODO: ???
		return loadModelHook(this, id);
		/*
		loadModel(id);
		loadingStack.remove(id);
		return unbakedCache.get(id);*/
	}
}
