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

package io.vram.frex.mixin;

import java.util.Deque;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.impl.math.MatrixStackImpl;
import io.vram.frex.mixinterface.PoseStackExt;

@Mixin(PoseStack.class)
public class MixinPoseStack implements PoseStackExt {
	@Shadow @Final private Deque<PoseStack.Pose> poseStack;
	private MatrixStackImpl frxStack;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onNew(CallbackInfo ci) {
		frxStack = new MatrixStackImpl(poseStack, (PoseStack) (Object) this);
	}

	/**
	 * @author grondag
	 * @reason performance
	 */
	@Overwrite
	public void popPose() {
		frxStack.pop();
	}

	/**
	 * @author grondag
	 * @reason performance
	 */
	@Overwrite
	public void pushPose() {
		frxStack.push();
	}

	@Override @Unique
	public MatrixStack frx_asMatrixStack() {
		return frxStack;
	}
}
