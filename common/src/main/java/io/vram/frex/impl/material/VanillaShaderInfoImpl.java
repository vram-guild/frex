/*
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
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
	public final boolean fog;
	public final int cutout;
	public final boolean diffuse;
	public final boolean foil;

	private VanillaShaderInfoImpl(boolean fog, boolean foil, int cutout, boolean diffuse) {
		this.fog = fog;
		this.foil = foil;
		this.cutout = cutout;
		this.diffuse = diffuse;
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

	private static final Object2ObjectOpenHashMap<String, VanillaShaderInfoImpl> MAP = new Object2ObjectOpenHashMap<>(64, Hash.VERY_FAST_LOAD_FACTOR);

	public static final VanillaShaderInfoImpl MISSING = of(false, false, CUTOUT_NONE);

	static {
		MAP.put("block", of(false, CUTOUT_NONE));
		MAP.put("new_entity", of(false, CUTOUT_NONE));
		MAP.put("particle", of(true, CUTOUT_TENTH));
		MAP.put("position_color_lightmap", of(false, CUTOUT_NONE));
		MAP.put("position", of(true, CUTOUT_NONE));
		MAP.put("position_color", of(false, CUTOUT_ZERO));
		MAP.put("position_color_tex", of(false, CUTOUT_TENTH));
		MAP.put("position_tex", of(false, CUTOUT_ZERO));
		MAP.put("position_tex_color", of(false, CUTOUT_TENTH));
		MAP.put("position_color_tex_lightmap", of(false, CUTOUT_TENTH));
		MAP.put("position_tex_color_normal", of(true, CUTOUT_TENTH));
		MAP.put("position_tex_lightmap_color", of(false, CUTOUT_NONE));
		MAP.put("rendertype_solid", of(true, CUTOUT_NONE, true));
		MAP.put("rendertype_cutout_mipped", of(true, CUTOUT_HALF, true));
		MAP.put("rendertype_cutout", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_translucent", of(true, CUTOUT_NONE, true));
		MAP.put("rendertype_translucent_moving_block", of(false, CUTOUT_NONE));
		MAP.put("rendertype_translucent_no_crumbling", of(false, CUTOUT_NONE));
		MAP.put("rendertype_armor_cutout_no_cull", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_entity_solid", of(true, CUTOUT_NONE, true));
		MAP.put("rendertype_entity_cutout", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_entity_cutout_no_cull", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_entity_cutout_no_cull_z_offset", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_item_entity_translucent_cull", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_entity_translucent_cull", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_entity_translucent", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_entity_smooth_cutout", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_beacon_beam", of(false, CUTOUT_NONE));
		MAP.put("rendertype_entity_decal", of(true, CUTOUT_TENTH, true));
		MAP.put("rendertype_entity_no_outline", of(true, CUTOUT_NONE, true));
		MAP.put("rendertype_entity_shadow", of(true, CUTOUT_NONE));
		MAP.put("rendertype_entity_alpha", of(false, CUTOUT_ALPHA));
		MAP.put("rendertype_eyes", of(false, CUTOUT_NONE));
		MAP.put("rendertype_energy_swirl", of(false, CUTOUT_TENTH));
		MAP.put("rendertype_leash", of(true, CUTOUT_NONE));
		MAP.put("rendertype_water_mask", of(false, CUTOUT_NONE));
		MAP.put("rendertype_outline", of(false, CUTOUT_ZERO));
		MAP.put("rendertype_armor_glint", of(false, false, CUTOUT_TENTH));
		MAP.put("rendertype_armor_entity_glint", of(false, false, CUTOUT_TENTH));
		MAP.put("rendertype_glint_translucent", of(false, false, CUTOUT_TENTH));
		MAP.put("rendertype_glint", of(false, false, CUTOUT_TENTH));
		MAP.put("rendertype_glint_direct", of(false, false, CUTOUT_TENTH));
		MAP.put("rendertype_entity_glint", of(false, false, CUTOUT_TENTH));
		MAP.put("rendertype_entity_glint_direct", of(false, false, CUTOUT_TENTH));
		MAP.put("rendertype_text", of(true, CUTOUT_TENTH));
		MAP.put("rendertype_text_see_through", of(false, CUTOUT_TENTH));
		MAP.put("rendertype_crumbling", of(false, CUTOUT_TENTH));
		MAP.put("rendertype_end_gateway", of(false, CUTOUT_NONE));
		MAP.put("rendertype_end_portal", of(true, CUTOUT_TENTH));
		MAP.put("rendertype_lightning", of(false, CUTOUT_NONE));
		MAP.put("rendertype_lines", of(true, CUTOUT_NONE));
		MAP.put("rendertype_tripwire", of(false, CUTOUT_TENTH));
	}

	public static VanillaShaderInfoImpl get(Object shaderStateShard) {
		final var shader = ((ShaderStateShard) shaderStateShard).shader;
		return shader.isPresent() ? get(shader.get().get().name) : MISSING;
	}

	public static VanillaShaderInfoImpl get(String shaderName) {
		return MAP.getOrDefault(shaderName, MISSING);
	}

	private static VanillaShaderInfoImpl of(boolean fog, int cutout) {
		return new VanillaShaderInfoImpl(fog, false, cutout, false);
	}

	private static VanillaShaderInfoImpl of(boolean fog, int cutout, boolean diffuse) {
		return new VanillaShaderInfoImpl(fog, false, cutout, diffuse);
	}

	private static VanillaShaderInfoImpl of(boolean fog, boolean glint, int cutout) {
		return new VanillaShaderInfoImpl(fog, glint, cutout, false);
	}
}
