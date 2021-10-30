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

package io.vram.frex.base.renderer;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import io.vram.frex.api.material.MaterialShader;
import io.vram.frex.api.renderer.MaterialShaderManager;

public class BaseMaterialShaderManager implements MaterialShaderManager {
	protected final Long2ObjectOpenHashMap<BaseMaterialShader> MAP = new Long2ObjectOpenHashMap<>();
	protected final ObjectArrayList<BaseMaterialShader> LIST = new ObjectArrayList<>();
	protected final MaterialShader defaultShader;
	protected final Indexer fragmentIndexer = new Indexer();
	protected final Indexer vertexIndexer = new Indexer();

	public BaseMaterialShaderManager() {
		defaultShader = getOrCreate(null, null);
	}

	public ResourceLocation fragmentIdFromIndex(int index) {
		return fragmentIndexer.fromIndex(index);
	}

	public ResourceLocation vertexIdFromIndex(int index) {
		return vertexIndexer.fromIndex(index);
	}

	public synchronized BaseMaterialShader find(ResourceLocation vertexShaderId, ResourceLocation fragmentShaderId, ResourceLocation depthVertexShaderId, ResourceLocation depthFragmentShaderId) {
		return find(
			vertexIndexer.toIndex(vertexShaderId),
			fragmentIndexer.toIndex(fragmentShaderId),
			vertexIndexer.toIndex(depthVertexShaderId),
			fragmentIndexer.toIndex(depthFragmentShaderId)
		);
	}

	public synchronized BaseMaterialShader find(int vertexShaderIndex, int fragmentShaderIndex, int depthVertexIndex, int depthFragmentIndex) {
		final long key = vertexShaderIndex | (fragmentShaderIndex << 16) | (((long) depthVertexIndex) << 32) | (((long) depthFragmentIndex) << 48);
		BaseMaterialShader result = MAP.get(key);

		if (result == null) {
			result = new BaseMaterialShader(this, LIST.size(), vertexShaderIndex, fragmentShaderIndex, depthVertexIndex, depthFragmentIndex);
			LIST.add(result);
			MAP.put(key, result);
		}

		return result;
	}

	public BaseMaterialShader get(int index) {
		return LIST.get(index);
	}

	@Override
	public MaterialShader shaderFromIndex(int index) {
		return get(index);
	}

	@Override
	public MaterialShader getOrCreate(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId) {
		return getOrCreate(vertexSourceId, fragmentSourceId, MaterialShader.DEFAULT_VERTEX_SOURCE, MaterialShader.DEFAULT_FRAGMENT_SOURCE);
	}

	@Override
	public MaterialShader getOrCreate(@Nullable ResourceLocation vertexSourceId, @Nullable ResourceLocation fragmentSourceId, @Nullable ResourceLocation depthVertexSourceId, @Nullable ResourceLocation depthFragmentSourceId) {
		if (vertexSourceId == null) {
			vertexSourceId = MaterialShader.DEFAULT_VERTEX_SOURCE;
		}

		if (fragmentSourceId == null) {
			fragmentSourceId = MaterialShader.DEFAULT_FRAGMENT_SOURCE;
		}

		if (depthVertexSourceId == null) {
			depthVertexSourceId = MaterialShader.DEFAULT_VERTEX_SOURCE;
		}

		if (depthFragmentSourceId == null) {
			depthFragmentSourceId = MaterialShader.DEFAULT_FRAGMENT_SOURCE;
		}

		return find(vertexSourceId, fragmentSourceId, depthVertexSourceId, depthFragmentSourceId);
	}

	@Override
	public MaterialShader defaultShader() {
		return defaultShader;
	}

	protected class Indexer {
		protected final Object2IntOpenHashMap<ResourceLocation> map = new Object2IntOpenHashMap<>();
		protected final ObjectArrayList<ResourceLocation> list = new ObjectArrayList<>();

		protected int toIndex(ResourceLocation id) {
			return map.computeIfAbsent(id, o -> {
				final int index;

				synchronized (list) {
					index = list.size();
					list.add(id);
				}

				return index;
			});
		}

		protected ResourceLocation fromIndex(int index) {
			return list.get(index);
		}
	}
}
