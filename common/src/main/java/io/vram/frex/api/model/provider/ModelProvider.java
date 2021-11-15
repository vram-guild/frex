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

package io.vram.frex.api.model.provider;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Interface for functions that intercept model load requests. These functions
 * act as predicates in addition to loading models - a {@code null} result
 * means the model is not handled by this provider.
 *
 * <p>It is safe and recommended to cache resources inside a given instance
 * because it will be created once per model loading cycle and then reused
 * for each loading request within that cycle.
 *
 * <p>All registered providers will be tried before vanilla model loading
 * is attempted, but order of registered providers is undefined and only the
 * first provider to respond a given model request will be used.
 */
@FunctionalInterface
public interface ModelProvider<T extends ResourceLocation> {
	/**
	 * @param path identifies the resource being requested.
	 * @param subModelLoader  Use to retrieve sub-models if needed.  It is safe to retain this reference
	 * within the provider, but as noted above the provider should not be retained.
	 * @return The loaded UnbakedModel if this provider handles the given resource path and the provider ran without error, otherwise null.
	 */
	@Nullable
	UnbakedModel loadModel(T path, SubModelLoader subModelLoader);
}
