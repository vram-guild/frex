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

package io.vram.frex.mixin;

import java.util.Deque;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import io.vram.frex.api.math.FastMatrix3f;
import io.vram.frex.api.math.FastMatrix4f;
import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.impl.math.MatrixStackEntryHelper;

@Mixin(PoseStack.class)
public abstract class MixinPoseStack implements MatrixStack {
	@Shadow @Final private Deque<PoseStack.Pose> poseStack;

	private final ObjectArrayList<PoseStack.Pose> pool = new ObjectArrayList<>();
	private FastMatrix4f modelMatrix;
	private FastMatrix3f normalMatrix;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onNew(CallbackInfo ci) {
		refresh();
	}

	/**
	 * @author grondag
	 * @reason performance
	 */
	@Overwrite
	public void popPose() {
		pool.add(poseStack.removeLast());
		refresh();
	}

	/**
	 * @author grondag
	 * @reason performance
	 */
	@Overwrite
	public void pushPose() {
		final PoseStack.Pose current = poseStack.getLast();
		PoseStack.Pose add;

		if (pool.isEmpty()) {
			add = MatrixStackEntryHelper.create(current.pose().copy(), current.normal().copy());
		} else {
			add = pool.pop();
			((FastMatrix4f) (Object) add.pose()).f_set(current.pose());
			((FastMatrix3f) (Object) add.normal()).f_set(current.normal());
		}

		poseStack.addLast(add);
		refresh();
	}

	private void refresh() {
		final PoseStack me = (PoseStack) (Object) this;
		final var pose = me.last();
		modelMatrix = (FastMatrix4f) (Object) pose.pose();
		normalMatrix = (FastMatrix3f) (Object) pose.normal();
	}

	@Override
	public void push() {
		((PoseStack) (Object) this).pushPose();
	}

	@Override
	public void pop() {
		((PoseStack) (Object) this).popPose();
	}

	@Override
	public FastMatrix4f modelMatrix() {
		return modelMatrix;
	}

	@Override
	public FastMatrix3f normalMatrix() {
		return normalMatrix;
	}
}
