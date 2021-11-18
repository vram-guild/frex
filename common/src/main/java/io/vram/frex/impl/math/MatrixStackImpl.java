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

package io.vram.frex.impl.math;

import java.util.Deque;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import com.mojang.blaze3d.vertex.PoseStack;

import io.vram.frex.api.math.FastMatrix3f;
import io.vram.frex.api.math.FastMatrix4f;
import io.vram.frex.api.math.MatrixStack;

public class MatrixStackImpl implements MatrixStack {
	private final Deque<PoseStack.Pose> stack;
	private final PoseStack wrapped;
	private final ObjectArrayList<PoseStack.Pose> pool = new ObjectArrayList<>();
	private FastMatrix4f modelMatrix;
	private FastMatrix3f normalMatrix;

	public MatrixStackImpl(Deque<PoseStack.Pose> stack, PoseStack wrapped) {
		this.stack = stack;
		this.wrapped = wrapped;
		refresh();
	}

	protected void refresh() {
		final var pose = wrapped.last();
		modelMatrix = (FastMatrix4f) (Object) pose.pose();
		normalMatrix = (FastMatrix3f) (Object) pose.normal();
	}

	@Override
	public void push() {
		final PoseStack.Pose current = stack.getLast();
		PoseStack.Pose add;

		if (pool.isEmpty()) {
			add = MatrixStackEntryHelper.create(current.pose().copy(), current.normal().copy());
		} else {
			add = pool.pop();
			((FastMatrix4f) (Object) add.pose()).f_set(current.pose());
			((FastMatrix3f) (Object) add.normal()).f_set(current.normal());
		}

		stack.addLast(add);
		refresh();
	}

	@Override
	public void pop() {
		pool.add(stack.removeLast());
		refresh();
	}

	@Override
	public FastMatrix4f modelMatrix() {
		return modelMatrix;
	}

	@Override
	public FastMatrix3f normalMatrix() {
		return normalMatrix;
	}

	@Override
	public PoseStack toVanilla() {
		return wrapped;
	}
}
