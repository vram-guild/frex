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

package io.vram.frex.impl.math;

import java.util.Deque;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;

import io.vram.frex.api.math.MatrixStack;

public class MatrixStackImpl implements MatrixStack {
	private final Deque<PoseStack.Pose> stack;
	private final PoseStack wrapped;
	private final ObjectArrayList<PoseStack.Pose> pool = new ObjectArrayList<>();
	private Matrix4f modelMatrix;
	private Matrix3f normalMatrix;

	public MatrixStackImpl(Deque<PoseStack.Pose> stack, PoseStack wrapped) {
		this.stack = stack;
		this.wrapped = wrapped;
		refresh();
	}

	protected void refresh() {
		final var pose = wrapped.last();
		modelMatrix = pose.pose();
		normalMatrix = pose.normal();
	}

	@Override
	public void push() {
		final PoseStack.Pose current = stack.getLast();
		PoseStack.Pose add;

		if (pool.isEmpty()) {
			add = MatrixStackEntryHelper.create(new Matrix4f(current.pose()), new Matrix3f(current.normal()));
		} else {
			add = pool.pop();
			add.pose().set(current.pose());
			add.normal().set(current.normal());
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
	public Matrix4f modelMatrix() {
		return modelMatrix;
	}

	@Override
	public Matrix3f normalMatrix() {
		return normalMatrix;
	}

	@Override
	public PoseStack toVanilla() {
		return wrapped;
	}
}
