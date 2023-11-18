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

public enum MaterialTriState {
	/**
	 * Default must have the 0th ordinal to make computing default material bits
	 * (as in {@link BaseMaterialManager#computeDefaultBits0()}) easier.
	 */
	DEFAULT (false, true),

	FALSE (false, false),

	TRUE (true, false);

	public boolean value() {
		return value;
	}

	/**
	 * The underlying boolean value.
	 */
	public final boolean value;

	/**
	 * Whether the value has not been set by a user.
	 */
	public final boolean isDefault;

	MaterialTriState(boolean value, boolean isDefault) {
		this.value = value;
		this.isDefault = isDefault;
	}

	public static MaterialTriState of (boolean value) {
		return value ? TRUE : FALSE;
	}

	public static MaterialTriState of (boolean value, boolean isDefault) {
		if (isDefault) {
			return DEFAULT;
		}

		return value ? TRUE : FALSE;
	}
}
