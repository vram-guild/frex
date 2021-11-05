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

package io.vram.frex.pastel.util;

import net.minecraft.client.renderer.RenderType;

public interface CompiledChunkExt {
	/**
	 * Mark buffer initialized.
	 *
	 * @param renderType Identifies buffer to be initialized.
	 * @return {@code true} if buffer was not already initialized.
	 */
	boolean frx_markInitialized(RenderType renderType);

	/**
	 * Mark that buffer has content.
	 *
	 * @param renderType Identifies buffer with content.
	 */
	void frx_markPopulated(RenderType renderType);
}
