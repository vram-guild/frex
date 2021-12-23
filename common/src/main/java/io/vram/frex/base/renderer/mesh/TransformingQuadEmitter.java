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

package io.vram.frex.base.renderer.mesh;

import io.vram.frex.api.buffer.PooledQuadEmitter;
import io.vram.frex.api.buffer.PooledVertexEmitter;
import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.buffer.QuadTransform;
import io.vram.frex.api.model.InputContext;

public class TransformingQuadEmitter extends BaseQuadEmitter implements PooledQuadEmitter, PooledVertexEmitter {
	protected final TransformStack transformStack;

	protected InputContext context;
	protected QuadEmitter output;
	protected QuadTransform transform;

	public TransformingQuadEmitter(TransformStack transformStack) {
		this.transformStack = transformStack;
		data = new int[MeshEncodingHelper.TOTAL_MESH_QUAD_STRIDE];
	}

	public TransformingQuadEmitter prepare(InputContext context, QuadTransform transform, QuadEmitter output) {
		this.context = context;
		this.transform = transform;
		this.output = output;
		clear();
		return this;
	}

	@Override
	public TransformingQuadEmitter withTransformQuad(InputContext context, QuadTransform transform) {
		return transformStack.createTransform(context, transform, this);
	}

	@Override
	public TransformingQuadEmitter withTransformVertex(InputContext context, QuadTransform transform) {
		return transformStack.createTransform(context, transform, this);
	}

	@Override
	public QuadEmitter emit() {
		transform.transform(context, this, output);
		return this;
	}

	@Override
	public void close() {
		transformStack.reclaim(this);
	}

	@Override
	public boolean isTransformer() {
		return true;
	}
}
