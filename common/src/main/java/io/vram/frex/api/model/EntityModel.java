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

package io.vram.frex.api.model;

import net.minecraft.world.entity.Entity;

import io.vram.frex.api.model.InputContext.Type;

@FunctionalInterface
public interface EntityModel<T extends Entity> {
	void renderAsEntity(EntityInputContext<T> input, ModelOuputContext output);

	@SuppressWarnings("unchecked")
	default void render(InputContext input, ModelOuputContext output) {
		if (input.type() == Type.ENTITY) {
			renderAsEntity((EntityInputContext<T>) input, output);
		}
	}

	public interface EntityInputContext<E extends Entity> extends InputContext {
		@Override
		default Type type() {
			return Type.ENTITY;
		}

		E entity();

		float yawDelta();

		float tickDelta();

		int packedLight();
	}
}
