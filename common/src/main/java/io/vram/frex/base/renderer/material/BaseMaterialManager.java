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

package io.vram.frex.base.renderer.material;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.renderer.MaterialManager;

public abstract class BaseMaterialManager<M extends BaseMaterialView & RenderMaterial> implements MaterialManager {
	protected final AtomicInteger nextIndex = new AtomicInteger(2);
	protected final Object[] values = new Object[MaterialConstants.MAX_MATERIAL_COUNT];
	protected final ConcurrentHashMap<BaseMaterialView, M> map = new ConcurrentHashMap<>(4096, 0.25f);
	protected final Object2ObjectOpenHashMap<ResourceLocation, M> registryMap = new Object2ObjectOpenHashMap<>();

	protected final M MISSING_MATERIAL = createMaterial((BaseMaterialView) new Finder().label(RenderMaterial.MISSING_MATERIAL_KEY.toString()), 0);
	@SuppressWarnings("unchecked")
	protected final M STANDARD_MATERIAL = (M) materialFinder().preset(MaterialConstants.PRESET_DEFAULT).label(RenderMaterial.STANDARD_MATERIAL_KEY.toString()).find();

	protected final Function<BaseMaterialView, M> mappingFunction = k -> {
		final M result = createMaterial(k, nextIndex.getAndIncrement());
		values[result.index()] = result;
		return result;
	};

	protected class Finder extends BaseMaterialFinder {
		@Override
		public RenderMaterial find() {
			return map.computeIfAbsent(this, mappingFunction);
		}
	}

	public BaseMaterialManager() {
		values[0] = MISSING_MATERIAL;
		registerMaterial(RenderMaterial.MISSING_MATERIAL_KEY, MISSING_MATERIAL);
		registerMaterial(RenderMaterial.STANDARD_MATERIAL_KEY, STANDARD_MATERIAL);
	}

	protected abstract M createMaterial(BaseMaterialView finder, int index);

	@Override
	public Finder materialFinder() {
		return new Finder();
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
}
