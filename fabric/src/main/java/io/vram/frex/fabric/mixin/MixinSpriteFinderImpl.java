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

package io.vram.frex.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;

import io.vram.frex.impl.texture.SpriteFinderImpl;

@SuppressWarnings("NonExtendableApiUsage")
@Mixin(SpriteFinderImpl.class)
public abstract class MixinSpriteFinderImpl implements SpriteFinder {
	@Override
	public TextureAtlasSprite find(QuadView quad) {
		final float u = quad.u(0) + quad.u(1) + quad.u(2) + quad.u(3);
		final float v = quad.v(0) + quad.v(1) + quad.v(2) + quad.v(3);
		return find(u * 0.25f, v * 0.25f);
	}
}
