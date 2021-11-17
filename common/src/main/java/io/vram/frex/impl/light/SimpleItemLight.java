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

package io.vram.frex.impl.light;

import io.vram.frex.api.light.ItemLight;

public class SimpleItemLight implements ItemLight {
	private final float intensity;
	private final float red;
	private final float green;
	private final float blue;
	private final boolean worksInFluid;
	private final int innerConeAngleDegrees;
	private final int outerConeAngleDegrees;

	public SimpleItemLight(
		float intensity,
		float red,
		float green,
		float blue,
		boolean worksInFluid,
		int innerConeAngleDegrees,
		int outerConeAngleDegrees
	) {
		this.intensity = intensity;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.worksInFluid = worksInFluid;
		this.innerConeAngleDegrees = innerConeAngleDegrees;
		this.outerConeAngleDegrees = outerConeAngleDegrees;
	}

	@Override
	public float intensity() {
		return intensity;
	}

	@Override
	public float red() {
		return red;
	}

	@Override
	public float green() {
		return green;
	}

	@Override
	public float blue() {
		return blue;
	}

	@Override
	public boolean worksInFluid() {
		return worksInFluid;
	}

	@Override
	public int innerConeAngleDegrees() {
		return innerConeAngleDegrees;
	}

	@Override
	public int outerConeAngleDegrees() {
		return outerConeAngleDegrees;
	}
}
