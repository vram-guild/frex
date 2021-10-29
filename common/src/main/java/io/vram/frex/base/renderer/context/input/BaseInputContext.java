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

package io.vram.frex.base.renderer.context.input;

import java.util.Random;

import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.api.model.InputContext;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;

public abstract class BaseInputContext implements InputContext {
	protected final Type type;
	protected final Random random = new Random();
	protected boolean needsRandomReseed = true;
	protected int overlay;
	protected MatrixStack matrixStack;

	public BaseInputContext(Type type) {
		this.type = type;
	}

	public void prepare(int overlay) {
		needsRandomReseed = true;
		this.overlay = overlay;
	}

	public void prepare(int overlay, MatrixStack matrixStack) {
		prepare(overlay);
		setMatrixStack(matrixStack);
	}

	public void setMatrixStack(MatrixStack matrixStack) {
		this.matrixStack = matrixStack;
	}

	protected abstract long randomSeed();

	@Override
	public Random random() {
		final Random result = random;

		if (needsRandomReseed) {
			result.setSeed(randomSeed());
		}

		return result;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public int overlay() {
		return overlay;
	}

	@Override
	public MatrixStack matrixStack() {
		return matrixStack;
	}

	public abstract int flatBrightness(BaseQuadEmitter quad);

	@Override
	public boolean isAbsent() {
		return false;
	}
}
