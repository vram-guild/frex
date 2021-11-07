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

package grondag.frex;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import io.vram.frex.api.config.FrexConfig;
import io.vram.frex.impl.FrexLoadManager;
import io.vram.frex.impl.config.FlawlessFramesImpl;

public class Frex implements ClientModInitializer {
	public static Logger LOG = LogManager.getLogger("FREX");

	private static final boolean isAvailable;

	static {
		boolean result = false;

		for (final ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (mod.getMetadata().containsCustomValue("frex:contains_frex_renderer")) {
				result = true;
				break;
			}
		}

		isAvailable = result;
	}

	@Deprecated
	@ScheduledForRemoval
	public static boolean isAvailable() {
		return isAvailable;
	}

	/**
	 * All Fabric-specific hooks needed for core API should be here for now.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeClient() {
		setupRenderer();

		FrexLoadManager.firstTimeLoad();

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(modelTextureListener);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(lightListener);

		FabricLoader.getInstance().getEntrypoints("frex", FrexInitializer.class).forEach(api -> api.onInitalizeFrex());

		final Function<String, Consumer<Boolean>> provider = FlawlessFramesImpl.providerFactory();
		FabricLoader.getInstance().getEntrypoints("frex_flawless_frames", Consumer.class).forEach(api -> api.accept(provider));

		FrexConfig.logMaterialPredicateDuplicates = FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	private static void setupRenderer() {
		// Code is replaced via mixin depending on what other renderers are loaded.
	}

	private final SimpleSynchronousResourceReloadListener modelTextureListener = new SimpleSynchronousResourceReloadListener() {
		private final List<ResourceLocation> deps = ImmutableList.of(ResourceReloadListenerKeys.MODELS, ResourceReloadListenerKeys.TEXTURES);
		private final ResourceLocation id = new ResourceLocation("frex:models_and_textures");

		@Override
		public ResourceLocation getFabricId() {
			return id;
		}

		@Override
		public Collection<ResourceLocation> getFabricDependencies() {
			return deps;
		}

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
			FrexLoadManager.reloadTextureDependencies(resourceManager);
		}
	};

	private final SimpleSynchronousResourceReloadListener lightListener = new SimpleSynchronousResourceReloadListener() {
		private final List<ResourceLocation> deps = ImmutableList.of();
		private final ResourceLocation id = new ResourceLocation("frex:general");

		@Override
		public ResourceLocation getFabricId() {
			return id;
		}

		@Override
		public Collection<ResourceLocation> getFabricDependencies() {
			return deps;
		}

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
			FrexLoadManager.reloadGeneralDependencies(resourceManager);
		}
	};
}
