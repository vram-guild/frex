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

package io.vram.frex.base.renderer.material;

public abstract class BaseRenderMaterial extends BaseMaterialView {
	protected final boolean blur;
	protected final boolean castShadows;
	protected final int conditionIndex;
	protected final boolean cull;
	protected final int cutout;
	protected final int decal;
	protected final int depthTest;
	protected final boolean disableAo;
	protected final boolean disableColorIndex;
	protected final boolean disableDiffuse;
	protected final boolean discardsTexture;
	protected final boolean emissive;
	protected final boolean flashOverlay;
	protected final boolean fog;
	protected final boolean foilOverlay;
	protected final boolean hurtOverlay;
	protected final int index;
	protected final boolean lines;
	protected final int preset;
	protected final int shaderIndex;
	protected final boolean sorted;
	protected final int target;
	protected final int textureIndex;
	protected final int transparency;
	protected final boolean unmipped;
	protected final int writeMask;

	public BaseRenderMaterial(int index, BaseMaterialView template) {
		this(index, template.bits0, template.bits1, template.label);
	}

	public BaseRenderMaterial(int index, long bits0, long bits1, String label) {
		super(bits0, bits1, label);
		this.index = index;
		blur = super.blur();
		castShadows = super.castShadows();
		conditionIndex = super.conditionIndex();
		cull = super.cull();
		cutout = super.cutout();
		decal = super.decal();
		depthTest = super.depthTest();
		disableAo = super.disableAo();
		disableColorIndex = super.disableColorIndex();
		disableDiffuse = super.disableDiffuse();
		discardsTexture = super.discardsTexture();
		emissive = super.emissive();
		flashOverlay = super.flashOverlay();
		fog = super.fog();
		foilOverlay = super.foilOverlay();
		hurtOverlay = super.hurtOverlay();
		lines = super.lines();
		preset = super.preset();
		shaderIndex = super.shaderIndex();
		sorted = super.sorted();
		target = super.target();
		textureIndex = super.textureIndex();
		transparency = super.transparency();
		unmipped = super.unmipped();
		writeMask = super.writeMask();
	}

	@Override
	public boolean blur() {
		return blur;
	}

	@Override
	public boolean castShadows() {
		return castShadows;
	}

	@Override
	public int conditionIndex() {
		return conditionIndex;
	}

	@Override
	public boolean cull() {
		return cull;
	}

	@Override
	public int cutout() {
		return cutout;
	}

	@Override
	public int decal() {
		return decal;
	}

	@Override
	public int depthTest() {
		return depthTest;
	}

	@Override
	public boolean disableAo() {
		return disableAo;
	}

	@Override
	public boolean disableColorIndex() {
		return disableColorIndex;
	}

	@Override
	public boolean disableDiffuse() {
		return disableDiffuse;
	}

	@Override
	public boolean discardsTexture() {
		return discardsTexture;
	}

	@Override
	public boolean emissive() {
		return emissive;
	}

	@Override
	public boolean flashOverlay() {
		return flashOverlay;
	}

	@Override
	public boolean fog() {
		return fog;
	}

	@Override
	public boolean foilOverlay() {
		return foilOverlay;
	}

	@Override
	public boolean hurtOverlay() {
		return hurtOverlay;
	}

	public int index() {
		return index;
	}

	@Override
	public boolean lines() {
		return lines;
	}

	@Override
	public int preset() {
		return preset;
	}

	@Override
	public int shaderIndex() {
		return shaderIndex;
	}

	@Override
	public boolean sorted() {
		return sorted;
	}

	@Override
	public int target() {
		return target;
	}

	@Override
	public int textureIndex() {
		return textureIndex;
	}

	@Override
	public int transparency() {
		return transparency;
	}

	@Override
	public boolean unmipped() {
		return unmipped;
	}

	@Override
	public int writeMask() {
		return writeMask;
	}
}
