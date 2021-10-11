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

import io.vram.bitkit.BitPacker64;
import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.RenderMaterial;

abstract class BaseMaterialView implements RenderMaterial {
	protected long bits0;
	protected long bits1;

	protected BaseMaterialView(long bits0, long bits1) {
		this.bits0 = bits0;
		this.bits1 = bits1;
	}

	@Override
	public boolean sorted() {
		return SORTED.getValue(bits0);
	}

	@Override
	public int conditionIndex() {
		return CONDITION.getValue(bits0);
	}

	@Override
	public boolean emissive() {
		return EMISSIVE.getValue(bits0);
	}

	@Override
	public boolean disableDiffuse() {
		return DISABLE_DIFFUSE.getValue(bits0);
	}

	@Override
	public boolean disableAo() {
		return DISABLE_AO.getValue(bits0);
	}

	@Override
	public boolean blur() {
		return BLUR.getValue(bits0);
	}

	@Override
	public int transparency() {
		return TRANSPARENCY.getValue(bits0);
	}

	@Override
	public int depthTest() {
		return DEPTH_TEST.getValue(bits0);
	}

	@Override
	public boolean cull() {
		return CULL.getValue(bits0);
	}

	@Override
	public int writeMask() {
		return WRITE_MASK.getValue(bits0);
	}

	@Override
	public boolean enableGlint() {
		return ENABLE_GLINT.getValue(bits0);
	}

	@Override
	public boolean discardsTexture() {
		return DISCARDS_TEXTURE.getValue(bits0);
	}

	@Override
	public int decal() {
		return DECAL.getValue(bits0);
	}

	@Override
	public int target() {
		return TARGET.getValue(bits0);
	}

	@Override
	public boolean lines() {
		return LINES.getValue(bits0);
	}

	@Override
	public boolean fog() {
		return FOG.getValue(bits0);
	}

	@Override
	public boolean castShadows() {
		return !DISABLE_SHADOWS.getValue(bits0);
	}

	@Override
	public int preset() {
		return PRESET.getValue(bits0);
	}

	@Override
	public boolean disableColorIndex() {
		return DISABLE_COLOR_INDEX.getValue(bits0);
	}

	@Override
	public int cutout() {
		return CUTOUT.getValue(bits0);
	}

	@Override
	public boolean unmipped() {
		return UNMIPPED.getValue(bits0);
	}

	@Override
	public boolean hurtOverlay() {
		return HURT_OVERLAY.getValue(bits0);
	}

	@Override
	public boolean flashOverlay() {
		return FLASH_OVERLAY.getValue(bits0);
	}

	static final BitPacker64<Void> PACKER_0 = new BitPacker64<> (null, null);

	static final BitPacker64<Void>.IntElement TARGET = PACKER_0.createIntElement(MaterialConstants.TARGET_COUNT);
	static final BitPacker64<Void>.IntElement TRANSPARENCY = PACKER_0.createIntElement(MaterialConstants.TRANSPARENCY_COUNT);
	static final BitPacker64<Void>.IntElement DEPTH_TEST = PACKER_0.createIntElement(MaterialConstants.DEPTH_TEST_COUNT);
	static final BitPacker64<Void>.IntElement WRITE_MASK = PACKER_0.createIntElement(MaterialConstants.WRITE_MASK_COUNT);
	static final BitPacker64<Void>.IntElement DECAL = PACKER_0.createIntElement(MaterialConstants.DECAL_COUNT);
	static final BitPacker64<Void>.IntElement PRESET = PACKER_0.createIntElement(MaterialConstants.PRESET_COUNT);
	static final BitPacker64<Void>.IntElement CUTOUT = PACKER_0.createIntElement(MaterialConstants.CUTOUT_COUNT);

	static final BitPacker64<Void>.BooleanElement BLUR = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement CULL = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement LINES = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement SORTED = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement DISABLE_COLOR_INDEX = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement DISCARDS_TEXTURE = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement EMISSIVE = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement DISABLE_DIFFUSE = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement DISABLE_AO = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement UNMIPPED = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement HURT_OVERLAY = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement FLASH_OVERLAY = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement FOG = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement DISABLE_SHADOWS = PACKER_0.createBooleanElement();
	static final BitPacker64<Void>.BooleanElement ENABLE_GLINT = PACKER_0.createBooleanElement();

	static final BitPacker64<Void> PACKER_1 = new BitPacker64<> (null, null);

	static final BitPacker64<Void>.IntElement TEXTURE = PACKER_1.createIntElement(MaterialConstants.MAX_TEXTURE_STATES);
	static final BitPacker64<Void>.IntElement SHADER_ID = PACKER_1.createIntElement(MaterialConstants.MAX_SHADERS);
	static final BitPacker64<Void>.IntElement CONDITION = PACKER_1.createIntElement(MaterialConstants.MAX_CONDITIONS);
}
