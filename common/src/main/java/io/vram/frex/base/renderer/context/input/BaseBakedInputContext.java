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

package io.vram.frex.base.renderer.context.input;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;

import io.vram.frex.api.model.BakedInputContext;

public abstract class BaseBakedInputContext extends BaseInputContext implements BakedInputContext {
	protected BakedModel bakedModel;
	protected RenderType defaultRenderType;
	protected int defaultPreset;

	public BaseBakedInputContext(Type type) {
		super(type);
	}

	@Override
	public int indexedColor(int colorIndex) {
		return -1;
	}

	@Override
	public BakedModel bakedModel() {
		return bakedModel;
	}

	protected abstract void computeDefaultRenderType();

	@Override
	public RenderType defaultRenderType() {
		computeDefaultRenderType();
		return defaultRenderType;
	}

	@Override
	public int defaultPreset() {
		computeDefaultRenderType();
		return defaultPreset;
	}
}
