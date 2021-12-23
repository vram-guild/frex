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

package io.vram.frex.impl.material;

import static io.vram.frex.api.material.MaterialConstants.CUTOUT_ALPHA;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_HALF;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_NONE;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_TENTH;
import static io.vram.frex.api.material.MaterialConstants.CUTOUT_ZERO;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;

import io.vram.frex.api.rendertype.VanillaShaderInfo;

@Internal
public final class VanillaShaderInfoImpl implements VanillaShaderInfo {
	private static final int FOG = 1;
	private static final int DIFFUSE = 2;
	private static final int FOIL = 4;
	private static final int UNLIT = 8;

	public final boolean fog;
	public final int cutout;
	public final boolean diffuse;
	public final boolean foil;
	public final boolean unlit;

	private VanillaShaderInfoImpl(int cutout, int flags) {
		this.cutout = cutout;
		this.fog = (flags & FOG) == FOG;
		this.foil = (flags & FOIL) == FOIL;
		this.diffuse = (flags & DIFFUSE) == DIFFUSE;
		this.unlit = (flags & UNLIT) == UNLIT;
	}

	@Override
	public boolean fog() {
		return fog;
	}

	@Override
	public int cutout() {
		return cutout;
	}

	@Override
	public boolean diffuse() {
		return diffuse;
	}

	@Override
	public boolean foil() {
		return foil;
	}

	@Override
	public boolean unlit() {
		return unlit;
	}

	private static final Object2ObjectOpenHashMap<String, VanillaShaderInfoImpl> MAP = new Object2ObjectOpenHashMap<>(64, Hash.VERY_FAST_LOAD_FACTOR);

	public static final VanillaShaderInfoImpl MISSING = of(CUTOUT_NONE, 0);

	static {
		MAP.put("block", of(CUTOUT_NONE, UNLIT));
		MAP.put("new_entity", of(CUTOUT_NONE, 0));
		MAP.put("particle", of(CUTOUT_TENTH, FOG));
		MAP.put("position_color_lightmap", of(CUTOUT_NONE, 0));
		MAP.put("position", of(CUTOUT_NONE, FOG | UNLIT));
		MAP.put("position_color", of(CUTOUT_ZERO, UNLIT));
		MAP.put("position_color_tex", of(CUTOUT_TENTH, UNLIT));
		MAP.put("position_tex", of(CUTOUT_ZERO, UNLIT));
		MAP.put("position_tex_color", of(CUTOUT_TENTH, UNLIT));
		MAP.put("position_color_tex_lightmap", of(CUTOUT_TENTH, 0));
		MAP.put("position_tex_color_normal", of(CUTOUT_TENTH, FOG | UNLIT));
		MAP.put("position_tex_lightmap_color", of(CUTOUT_NONE, 0));
		MAP.put("rendertype_solid", of(CUTOUT_NONE, FOG | DIFFUSE));
		MAP.put("rendertype_cutout_mipped", of(CUTOUT_HALF, FOG | DIFFUSE));
		MAP.put("rendertype_cutout", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_translucent", of(CUTOUT_NONE, FOG | DIFFUSE));
		MAP.put("rendertype_translucent_moving_block", of(CUTOUT_NONE, 0));
		MAP.put("rendertype_translucent_no_crumbling", of(CUTOUT_NONE, 0));
		MAP.put("rendertype_armor_cutout_no_cull", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_entity_solid", of(CUTOUT_NONE, FOG | DIFFUSE));
		MAP.put("rendertype_entity_cutout", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_entity_cutout_no_cull", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_entity_cutout_no_cull_z_offset", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_item_entity_translucent_cull", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_entity_translucent_cull", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_entity_translucent", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_entity_smooth_cutout", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_beacon_beam", of(CUTOUT_NONE, 0));
		MAP.put("rendertype_entity_decal", of(CUTOUT_TENTH, FOG | DIFFUSE));
		MAP.put("rendertype_entity_no_outline", of(CUTOUT_NONE, FOG | DIFFUSE));
		MAP.put("rendertype_entity_shadow", of(CUTOUT_NONE, FOG | UNLIT));
		MAP.put("rendertype_entity_alpha", of(CUTOUT_ALPHA, UNLIT));
		MAP.put("rendertype_eyes", of(CUTOUT_NONE, UNLIT));
		MAP.put("rendertype_energy_swirl", of(CUTOUT_TENTH, UNLIT));
		MAP.put("rendertype_leash", of(CUTOUT_NONE, FOG));
		MAP.put("rendertype_water_mask", of(CUTOUT_NONE, 0));
		MAP.put("rendertype_outline", of(CUTOUT_ZERO, UNLIT));
		MAP.put("rendertype_armor_glint", of(CUTOUT_TENTH, UNLIT | FOIL));
		MAP.put("rendertype_armor_entity_glint", of(CUTOUT_TENTH, UNLIT | FOIL));
		MAP.put("rendertype_glint_translucent", of(CUTOUT_TENTH, UNLIT | FOIL));
		MAP.put("rendertype_glint", of(CUTOUT_TENTH, UNLIT | FOIL));
		MAP.put("rendertype_glint_direct", of(CUTOUT_TENTH, UNLIT | FOIL));
		MAP.put("rendertype_entity_glint", of(CUTOUT_TENTH, UNLIT | FOIL));
		MAP.put("rendertype_entity_glint_direct", of(CUTOUT_TENTH, UNLIT | FOIL));
		MAP.put("rendertype_text", of(CUTOUT_TENTH, FOG));
		MAP.put("rendertype_text_see_through", of(CUTOUT_TENTH, 0));
		MAP.put("rendertype_crumbling", of(CUTOUT_TENTH, UNLIT));
		MAP.put("rendertype_end_gateway", of(CUTOUT_NONE, UNLIT));
		MAP.put("rendertype_end_portal", of(CUTOUT_TENTH, UNLIT));
		MAP.put("rendertype_lightning", of(CUTOUT_NONE, UNLIT));
		MAP.put("rendertype_lines", of(CUTOUT_NONE, FOG | UNLIT));
		MAP.put("rendertype_tripwire", of(CUTOUT_TENTH, FOG));
	}

	public static VanillaShaderInfoImpl get(Object shaderStateShard) {
		final var shader = ((ShaderStateShard) shaderStateShard).shader;
		return shader.isPresent() ? get(shader.get().get().name) : MISSING;
	}

	public static VanillaShaderInfoImpl get(String shaderName) {
		return MAP.getOrDefault(shaderName, MISSING);
	}

	private static VanillaShaderInfoImpl of(int cutout, int flags) {
		return new VanillaShaderInfoImpl(cutout, flags);
	}
}
