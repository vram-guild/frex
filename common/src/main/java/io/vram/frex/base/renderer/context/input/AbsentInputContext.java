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

package io.vram.frex.base.renderer.context.input;

import net.minecraft.client.renderer.texture.OverlayTexture;

import io.vram.frex.api.math.MatrixStack;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;

public class AbsentInputContext extends BaseInputContext {
	public AbsentInputContext() {
		super(Type.ABSENT);
		this.setMatrixStack(MatrixStack.create());
		this.prepare(OverlayTexture.NO_OVERLAY);
	}

	@Override
	protected long randomSeed() {
		return 42;
	}

	@Override
	public int flatBrightness(BaseQuadEmitter quad) {
		return 0;
	}

	@Override
	public boolean isAbsent() {
		return true;
	}

	public static final AbsentInputContext INSTANCE = new AbsentInputContext();
}
