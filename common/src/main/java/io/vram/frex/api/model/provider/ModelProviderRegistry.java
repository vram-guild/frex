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

package io.vram.frex.api.model.provider;

import java.util.function.Function;

import org.spongepowered.include.com.google.common.base.Preconditions;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.frex.impl.model.ModelProviderRegistryImpl;

/**
 * Provides hooks to inject new models for loading and to intercept
 * model loading requests.
 *
 * <p>Injection of new models is accomplished via {@link #registerLocationProvider(ModelLocationProvider)}.
 *
 * <p>To intercept model <em>variants</em> - the different configurations of block models for each block
 * state property combination - use {@link #registerVariantProvider(Function)}.  This can also be used to
 * accept the {@code #inventory} variant used for item models.
 *
 * <p>To intercept model <em>files</em> use {@link #registerResourceProvider(Function)}. This is mainly useful
 * for loading models in other file formats, while retaining the vanilla mechanisms for randomized and multipart
 * models.
 *
 * <p>When intercepting model load requests, note that both provider types act as a predicate - they return {@code null}
 * to indicate the request was not intercepted.  If two provides try to handle the same path, then only one will
 * actually be used and the order is indeterminate.
 *
 * <p>While it is possible to provide a different provider for each individual path, the preferred approach is to
 * register a provider that intercepts many or all variations of a block or item (or even a group of them) and return
 * a single, shared dynamic model instance that alters output according to input context. This will generally
 * be more powerful (and is a main feature of the FREX model API) and will generally also be more memory efficient.
 */
public interface ModelProviderRegistry {
	/**
	 * Register a provider factory to intercept loading of model *variants*.
	 *
	 * @param providerFunction Called each time model loading runs to instantiate the provider,
	 * which is then reused for the entire model loading cycle.
	 */
	static void registerVariantProvider(Function<ResourceManager, ModelProvider<ModelResourceLocation>> providerFunction) {
		ModelProviderRegistryImpl.registerVariantProvider(providerFunction);
	}

	/**
	 * Register a provider factory to intercept loading of all model variants associated with one or more blocks or items,
	 * including the inventory variant. This is simpler than use
	 *
	 * @param providerFunction Called each time model loading runs to instantiate the provider,
	 * which is then reused for the entire model loading cycle.
	 * @param paths Identifies one or more blocks or items that share the same provider.
	 */
	static void registerBlockItemProvider(Function<ResourceManager, ModelProvider<ModelResourceLocation>> providerFunction, ResourceLocation... paths) {
		Preconditions.checkNotNull(paths);
		ModelProviderRegistryImpl.registerBlockItemProvider(providerFunction, paths);
	}

	static void registerBlockItemProvider(Function<ResourceManager, ModelProvider<ModelResourceLocation>> providerFunction, String... paths) {
		Preconditions.checkNotNull(paths);
		ModelProviderRegistryImpl.registerBlockItemProvider(providerFunction, ModelProviderRegistryImpl.stringsToLocations(paths));
	}

	/**
	 * Register a provider factory to intercept loading of model *files* from the resource tree.
	 * For example, the vanilla equivalent logic would respond to a path "minecraft:block/stone"
	 * by loading a JSON model file located at "assets/minecraft/models/block/stone.json".
	 *
	 * @param providerFunction Called each time model loading runs to instantiate the provider,
	 * which is then reused for the entire model loading cycle.
	 */
	static void registerResourceProvider(Function<ResourceManager, ModelProvider<ResourceLocation>> providerFunction) {
		ModelProviderRegistryImpl.registerResourceProvider(providerFunction);
	}

	/**
	 * Use this to request loading of baked models that wouldn't be requested by
	 * conventional JSON model loading. If the locations provided don't point to
	 * JSON model files, then it will also be necessary to register a {@link VariantModelProvider}
	 * or {@link ResourceModelProvider}.
	 *
	 * @see ModelLocationProvider
	 */
	static void registerLocationProvider(ModelLocationProvider locationProvider) {
		ModelProviderRegistryImpl.registerLocationProvider(locationProvider);
	}
}
