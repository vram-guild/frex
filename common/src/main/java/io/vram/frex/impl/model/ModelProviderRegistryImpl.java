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

package io.vram.frex.impl.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.frex.api.config.FrexConfig;
import io.vram.frex.api.model.provider.ModelLocationProvider;
import io.vram.frex.api.model.provider.ModelProvider;
import io.vram.frex.api.model.provider.SubModelLoader;
import io.vram.frex.impl.FrexLog;
import io.vram.frex.mixinterface.ModelBakeryExt;

public class ModelProviderRegistryImpl {
	public static class LoaderInstance implements SubModelLoader {
		private final List<ModelProvider<ModelResourceLocation>> modelVariantProviders;
		private final List<ModelProvider<ResourceLocation>> modelResourceProviders;
		private final Object2ObjectOpenHashMap<ResourceLocation, ModelProvider<ModelResourceLocation>> blockItemProviders = new Object2ObjectOpenHashMap<>();

		private ModelBakery loader;

		private LoaderInstance(ModelBakery loader, ResourceManager manager) {
			this.loader = loader;
			this.modelVariantProviders = variantProviderFunctions.stream().map((s) -> s.apply(manager)).collect(Collectors.toList());
			this.modelResourceProviders = resourceProviderFunctions.stream().map((s) -> s.apply(manager)).collect(Collectors.toList());

			for (final var pair : blockItemProviderFunctions) {
				final var func = pair.getLeft().apply(manager);

				for (final var path : pair.getRight()) {
					blockItemProviders.putIfAbsent(path, func);
				}
			}
		}

		@Override
		public UnbakedModel loadSubModel(ResourceLocation id) {
			if (loader == null) {
				throw new RuntimeException("A model provider attempted to access ModelBakery after model baking was complete.");
			}

			return ((ModelBakeryExt) loader).frx_loadModel(id);
		}

		@Nullable
		public UnbakedModel loadModelFromResource(ResourceLocation resourceId) {
			return loadCustomModel((r) -> r.loadModel(resourceId, this), modelResourceProviders, "resource provider");
		}

		@Nullable
		public UnbakedModel loadModelFromVariant(ResourceLocation path) {
			if (!(path instanceof final ModelResourceLocation modelId)) {
				return loadModelFromResource(path);
			} else {
				final var variantId = (ModelResourceLocation) path;
				final ModelProvider<ModelResourceLocation> pathProvider = blockItemProviders.get(new ResourceLocation(path.getNamespace(), path.getPath()));
				UnbakedModel model = null;

				if (pathProvider != null) {
					try {
						model = pathProvider.loadModel(variantId, this);
					} catch (final Exception e) {
						FrexLog.error(e);
					}
				}

				if (model != null) {
					return model;
				}

				model = loadCustomModel((r) -> r.loadModel(variantId, this), modelVariantProviders, "resource provider");

				if (model != null) {
					return model;
				}

				// Replicating the special-case from ModelBakery as loadModelFromJson is insufficiently patchable
				if (Objects.equals(modelId.getVariant(), "inventory")) {
					final ResourceLocation resourceId = new ResourceLocation(modelId.getNamespace(), "item/" + modelId.getPath());
					model = loadModelFromResource(resourceId);

					if (model != null) {
						return model;
					}
				}

				return null;
			}
		}

		public void finish() {
			loader = null;
		}
	}

	private static <T> UnbakedModel loadCustomModel(Function<T, UnbakedModel> function, Collection<T> loaders, String debugName) {
		if (!FrexConfig.debugModelLoading) {
			for (final T provider : loaders) {
				try {
					final UnbakedModel model = function.apply(provider);

					if (model != null) {
						return model;
					}
				} catch (final Exception e) {
					FrexLog.error(e);
					return null;
				}
			}

			return null;
		}

		UnbakedModel modelLoaded = null;
		T providerUsed = null;
		List<T> providersApplied = null;

		for (final T provider : loaders) {
			try {
				final UnbakedModel model = function.apply(provider);

				if (model != null) {
					if (providersApplied != null) {
						providersApplied.add(provider);
					} else if (providerUsed != null) {
						providersApplied = Lists.newArrayList(providerUsed, provider);
					} else {
						modelLoaded = model;
						providerUsed = provider;
					}
				}
			} catch (final Exception e) {
				FrexLog.error(e);
				return null;
			}
		}

		if (providersApplied != null) {
			final StringBuilder builder = new StringBuilder("Conflict - multiple " + debugName + "s claimed the same unbaked model:");

			for (final T loader : providersApplied) {
				builder.append("\n\t - ").append(loader.getClass().getName());
			}

			FrexLog.error(builder.toString());
			return null;
		} else {
			return modelLoaded;
		}
	}

	private static final ObjectArrayList<Function<ResourceManager, ModelProvider<ModelResourceLocation>>> variantProviderFunctions = new ObjectArrayList<>();
	private static final ObjectArrayList<Function<ResourceManager, ModelProvider<ResourceLocation>>> resourceProviderFunctions = new ObjectArrayList<>();
	private static final ObjectArrayList<Pair<Function<ResourceManager, ModelProvider<ModelResourceLocation>>, ResourceLocation[]>> blockItemProviderFunctions = new ObjectArrayList<>();
	private static final ObjectArrayList<ModelLocationProvider> locationProviders = new ObjectArrayList<>();

	public static void registerLocationProvider(ModelLocationProvider provider) {
		locationProviders.add(provider);
	}

	public static void registerResourceProvider(Function<ResourceManager, ModelProvider<ResourceLocation>> providerFunction) {
		resourceProviderFunctions.add(providerFunction);
	}

	public static void registerVariantProvider(Function<ResourceManager, ModelProvider<ModelResourceLocation>> providerFunction) {
		variantProviderFunctions.add(providerFunction);
	}

	public static LoaderInstance begin(ModelBakery loader, ResourceManager manager) {
		return new LoaderInstance(loader, manager);
	}

	public static void onModelPopulation(ResourceManager resourceManager, Consumer<ResourceLocation> target) {
		for (final ModelLocationProvider appender : locationProviders) {
			appender.provideLocations(resourceManager, target);
		}
	}

	public static void registerBlockItemProvider(Function<ResourceManager, ModelProvider<ModelResourceLocation>> providerFunction, ResourceLocation... paths) {
		blockItemProviderFunctions.add(Pair.of(providerFunction, paths));
	}
}
