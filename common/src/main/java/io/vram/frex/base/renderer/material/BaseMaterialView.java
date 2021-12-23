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

package io.vram.frex.base.renderer.material;

import it.unimi.dsi.fastutil.HashCommon;

import io.vram.bitkit.BitPacker64;
import io.vram.frex.api.material.MaterialConstants;
import io.vram.frex.api.material.MaterialView;

public abstract class BaseMaterialView implements MaterialView {
	protected long bits0;
	protected long bits1;
	protected String label;

	public BaseMaterialView(long bits0, long bits1, String label) {
		this.bits0 = bits0;
		this.bits1 = bits1;
		this.label = label;
	}

	public BaseMaterialView(BaseMaterialView template) {
		bits0 = template.bits0;
		bits1 = template.bits1;
		label = template.label;
	}

	public long bits0() {
		return bits0;
	}

	public long bits1() {
		return bits1;
	}

	@Override
	public int hashCode() {
		final int h = it.unimi.dsi.fastutil.HashCommon.long2int(HashCommon.mix(bits0));
		return 31 * h + it.unimi.dsi.fastutil.HashCommon.long2int(HashCommon.mix(bits1));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof final BaseMaterialView other) {
			return other.bits0 == bits0 && other.bits1 == bits1;
		} else {
			return false;
		}
	}

	@Override
	public boolean sorted() {
		return SORTED.getValue(bits0);
	}

	@Override
	public int conditionIndex() {
		return CONDITION.getValue(bits1);
	}

	@Override
	public int textureIndex() {
		return TEXTURE.getValue(bits1);
	}

	@Override
	public int shaderIndex() {
		return SHADER.getValue(bits1);
	}

	@Override
	public boolean emissive() {
		return EMISSIVE.getValue(bits0);
	}

	@Override
	public boolean unlit() {
		return UNLIT.getValue(bits0);
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
	public boolean foilOverlay() {
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

	@Override
	public String label() {
		return label;
	}

	protected static final BitPacker64<Void> PACKER_0 = new BitPacker64<> (null, null);

	public static final BitPacker64<Void>.IntElement TARGET = PACKER_0.createIntElement(MaterialConstants.TARGET_COUNT);
	public static final BitPacker64<Void>.IntElement TRANSPARENCY = PACKER_0.createIntElement(MaterialConstants.TRANSPARENCY_COUNT);
	public static final BitPacker64<Void>.IntElement DEPTH_TEST = PACKER_0.createIntElement(MaterialConstants.DEPTH_TEST_COUNT);
	public static final BitPacker64<Void>.IntElement WRITE_MASK = PACKER_0.createIntElement(MaterialConstants.WRITE_MASK_COUNT);
	public static final BitPacker64<Void>.IntElement DECAL = PACKER_0.createIntElement(MaterialConstants.DECAL_COUNT);
	public static final BitPacker64<Void>.IntElement PRESET = PACKER_0.createIntElement(MaterialConstants.PRESET_COUNT);
	public static final BitPacker64<Void>.IntElement CUTOUT = PACKER_0.createIntElement(MaterialConstants.CUTOUT_COUNT);

	public static final BitPacker64<Void>.BooleanElement BLUR = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement CULL = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement LINES = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement SORTED = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement DISABLE_COLOR_INDEX = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement DISCARDS_TEXTURE = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement EMISSIVE = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement UNLIT = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement DISABLE_DIFFUSE = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement DISABLE_AO = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement UNMIPPED = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement HURT_OVERLAY = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement FLASH_OVERLAY = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement FOG = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement DISABLE_SHADOWS = PACKER_0.createBooleanElement();
	public static final BitPacker64<Void>.BooleanElement ENABLE_GLINT = PACKER_0.createBooleanElement();

	protected static final BitPacker64<Void> PACKER_1 = new BitPacker64<> (null, null);

	public static final BitPacker64<Void>.IntElement TEXTURE = PACKER_1.createIntElement(MaterialConstants.MAX_TEXTURE_STATES);
	public static final BitPacker64<Void>.IntElement SHADER = PACKER_1.createIntElement(MaterialConstants.MAX_SHADERS);
	public static final BitPacker64<Void>.IntElement CONDITION = PACKER_1.createIntElement(MaterialConstants.MAX_CONDITIONS);
}
