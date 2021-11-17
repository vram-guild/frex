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

package io.vram.frex.base.renderer.material;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.renderer.ConditionManager;
import io.vram.frex.api.renderer.MaterialManager;
import io.vram.frex.api.renderer.MaterialShaderManager;
import io.vram.frex.api.renderer.MaterialTextureManager;

public class BaseMaterialManager<M extends BaseMaterialView & RenderMaterial> implements MaterialManager {
	protected final AtomicInteger nextIndex = new AtomicInteger(2);
	protected final Object[] values = new Object[MaterialConstants.MAX_MATERIAL_COUNT];
	protected final ConcurrentHashMap<BaseMaterialView, M> map = new ConcurrentHashMap<>(4096, 0.25f);
	protected final Object2ObjectOpenHashMap<ResourceLocation, M> registryMap = new Object2ObjectOpenHashMap<>();
	protected final long defaultBits0;
	protected final long defaultBits1;
	protected final M MISSING_MATERIAL;
	protected final M STANDARD_MATERIAL;
	protected final MaterialFactory<M> factory;
	public final ConditionManager conditions;
	public final MaterialTextureManager textures;
	public final MaterialShaderManager shaders;

	protected final Function<BaseMaterialView, M> mappingFunction = this::createFromKey;

	protected class Finder extends BaseMaterialFinder {
		public Finder(long defaultBits0, long defaultBits1) {
			super(defaultBits0, defaultBits1);
		}

		@Override
		public RenderMaterial find() {
			return map.computeIfAbsent(this, mappingFunction);
		}
	}

	@SuppressWarnings("unchecked")
	public BaseMaterialManager(ConditionManager conditions, MaterialTextureManager textures, MaterialShaderManager shaders, MaterialFactory<M> factory) {
		this.conditions = conditions;
		this.textures = textures;
		this.shaders = shaders;
		this.factory = factory;
		defaultBits0 = computeDefaultBits0();
		defaultBits1 = computeDefaultBits1();
		MISSING_MATERIAL = factory.createMaterial(this, 0, (BaseMaterialView) materialFinder().label(RenderMaterial.MISSING_MATERIAL_KEY.toString()));
		values[0] = MISSING_MATERIAL;
		STANDARD_MATERIAL = (M) materialFinder().preset(MaterialConstants.PRESET_DEFAULT).label(RenderMaterial.STANDARD_MATERIAL_KEY.toString()).find();
		registerMaterial(RenderMaterial.MISSING_MATERIAL_KEY, MISSING_MATERIAL);
		registerMaterial(RenderMaterial.STANDARD_MATERIAL_KEY, STANDARD_MATERIAL);
	}

	protected M createFromKey(BaseMaterialView key) {
		final M result = factory.createMaterial(this, nextIndex.getAndIncrement(), key);
		values[result.index()] = result;
		return result;
	}

	@Override
	public Finder materialFinder() {
		return new Finder(defaultBits0, defaultBits1);
	}

	@Override
	public @Nullable M materialFromId(ResourceLocation id) {
		return registryMap.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public M materialFromIndex(int index) {
		return (M) values[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean registerMaterial(ResourceLocation id, RenderMaterial material) {
		if (registryMap.containsKey(id)) {
			return false;
		}

		// cast to prevent acceptance of impostor implementations
		registryMap.put(id, (M) material);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean registerOrUpdateMaterial(ResourceLocation id, RenderMaterial material) {
		// cast to prevent acceptance of impostor implementations
		return registryMap.put(id, (M) material) == null;
	}

	@Override
	public M defaultMaterial() {
		return STANDARD_MATERIAL;
	}

	@Override
	public M missingMaterial() {
		return MISSING_MATERIAL;
	}

	protected long computeDefaultBits0() {
		long defaultBits0 = 0;

		defaultBits0 = BaseMaterialView.TARGET.setValue(MaterialConstants.TARGET_MAIN, defaultBits0);
		defaultBits0 = BaseMaterialView.TRANSPARENCY.setValue(MaterialConstants.TRANSPARENCY_NONE, defaultBits0);
		defaultBits0 = BaseMaterialView.DEPTH_TEST.setValue(MaterialConstants.DEPTH_TEST_LEQUAL, defaultBits0);
		defaultBits0 = BaseMaterialView.WRITE_MASK.setValue(MaterialConstants.WRITE_MASK_COLOR_DEPTH, defaultBits0);
		defaultBits0 = BaseMaterialView.DECAL.setValue(MaterialConstants.DECAL_NONE, defaultBits0);
		defaultBits0 = BaseMaterialView.PRESET.setValue(MaterialConstants.PRESET_DEFAULT, defaultBits0);
		defaultBits0 = BaseMaterialView.CUTOUT.setValue(MaterialConstants.CUTOUT_NONE, defaultBits0);
		defaultBits0 = BaseMaterialView.CULL.setValue(true, defaultBits0);
		defaultBits0 = BaseMaterialView.FOG.setValue(true, defaultBits0);
		return defaultBits0;
	}

	protected long computeDefaultBits1() {
		long defaultBits1 = 0;
		defaultBits1 = BaseMaterialView.CONDITION.setValue(conditions.alwaysTrue().index(), defaultBits1);
		defaultBits1 = BaseMaterialView.TEXTURE.setValue(textures.textureFromId(TextureAtlas.LOCATION_BLOCKS).index(), defaultBits1);
		defaultBits1 = BaseMaterialView.SHADER.setValue(shaders.defaultShader().index(), defaultBits1);

		return defaultBits1;
	}

	public interface MaterialFactory<T extends BaseMaterialView & RenderMaterial> {
		T createMaterial(BaseMaterialManager<T> manager, int index, BaseMaterialView finder);
	}
}
