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

package io.vram.frex.api.buffer;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import io.vram.frex.api.model.InputContext;
import io.vram.frex.api.rendertype.RenderTypeUtil;

/**
 * Universal interface for models to send vertex data to the back end.
 * Supports both polygon-at-a-time and vanilla-style vertex streaming
 * input patterns.
 *
 * <p>Implements MultiBufferSource to support use cases where we want
 * to intercept output from vanilla or mod code that isn't API aware.
 */
public interface QuadSink extends MultiBufferSource {
	QuadEmitter asQuadEmitter();

	VertexEmitter asVertexEmitter();

	PooledQuadEmitter withTransformQuad(InputContext context, QuadTransform transform);

	PooledVertexEmitter withTransformVertex(InputContext context, QuadTransform transform);

	/** Has no effect for non-pooled emitters. */
	default void close() {
		// NOOP
	}

	/**
	 * Will be true for instances returned from {@link #withTransformQuad(InputContext, QuadTransform)}
	 * or {@link #withTransformVertex(InputContext, QuadTransform)}.  Primary use case
	 * if for renderers to avoid early culling tests when a transform is present.
	 *
	 * @return {@code true} when this instance applies a {@link QuadTransform}.
	 */
	default boolean isTransformer() {
		return false;
	}

	/**
	 * This default implementation won't handle all use cases.
	 * Implementations should provide something suitable for each context.
	 * In terrain rendering, for example, this probably will not be used.
	 *
	 * <p>Does not return VertexEmitter to support implementations
	 * that need to fall back to vanilla classes for some render types.
	 * API-aware models should simply use {@link #asVertexEmitter()}.
	 */
	@Override
	default VertexConsumer getBuffer(RenderType renderTYpe) {
		return asVertexEmitter().defaultMaterial(RenderTypeUtil.toMaterial(renderTYpe));
	}
}
