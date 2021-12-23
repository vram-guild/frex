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

package io.vram.frex.api.model;

import net.minecraft.world.entity.Entity;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.InputContext.Type;

@FunctionalInterface
public interface EntityModel<T extends Entity> extends DynamicModel {
	void renderAsEntity(EntityInputContext<T> input, QuadSink output);

	@Override
	@SuppressWarnings("unchecked")
	default void renderDynamic(InputContext input, QuadSink output) {
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
