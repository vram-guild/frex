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

import io.vram.frex.api.model.InputContext;

public interface QuadSink {
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
}
