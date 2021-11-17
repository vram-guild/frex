/*
 * Copyright © Original Authors
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

package io.vram.frex.impl.material.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.scores.Team;

import io.vram.frex.api.material.RenderMaterial;

/**
 * Stripped-down adaptation of vanilla class used for entity loot predicates.
 */
public class EntityOnly extends EntityBiPredicate {
	public static final EntityOnly ANY;

	private final MobEffectsPredicate effects;
	private final NbtPredicate nbt;
	private final EntityFlagsPredicate flags;
	private final EntityEquipmentPredicate equipment;
	private final PlayerPredicate player;
	private final FishingHookPredicate fishingHook;
	@Nullable private final String team;
	@Nullable private final ResourceLocation catType;

	private EntityOnly(MobEffectsPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, PlayerPredicate player, FishingHookPredicate fishingHook, @Nullable String team, @Nullable ResourceLocation catType) {
		this.effects = effects;
		this.nbt = nbt;
		this.flags = flags;
		this.equipment = equipment;
		this.player = player;
		this.fishingHook = fishingHook;
		this.team = team;
		this.catType = catType;
	}

	public boolean test(Entity entity) {
		return test(entity, null);
	}

	@Override
	public boolean test(Entity entity, RenderMaterial ignored) {
		if (this == ANY) {
			return true;
		} else if (entity == null) {
			return false;
		} else {
			if (!effects.matches(entity)) {
				return false;
			} else if (!nbt.matches(entity)) {
				return false;
			} else if (!flags.matches(entity)) {
				return false;
			} else if (!equipment.matches(entity)) {
				return false;
			} else if (!player.matches(entity)) {
				return false;
			} else if (!fishingHook.matches(entity)) {
				return false;
			} else {
				if (team != null) {
					final Team abstractTeam = entity.getTeam();

					if (abstractTeam == null || !team.equals(abstractTeam.getName())) {
						return false;
					}
				}

				return catType == null || entity instanceof Cat && ((Cat) entity).getResourceLocation().equals(catType);
			}
		}
	}

	public static EntityOnly fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			final JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "entity");
			final MobEffectsPredicate effects = MobEffectsPredicate.fromJson(jsonObject.get("effects"));
			final NbtPredicate nbt = NbtPredicate.fromJson(jsonObject.get("nbt"));
			final EntityFlagsPredicate flags = EntityFlagsPredicate.fromJson(jsonObject.get("flags"));
			final EntityEquipmentPredicate equipment = EntityEquipmentPredicate.fromJson(jsonObject.get("equipment"));
			final PlayerPredicate player = PlayerPredicate.fromJson(jsonObject.get("player"));
			final FishingHookPredicate fishHook = FishingHookPredicate.fromJson(jsonObject.get("fishing_hook"));
			final String team = GsonHelper.getAsString(jsonObject, "team", (String) null);
			final ResourceLocation catType = jsonObject.has("catType") ? new ResourceLocation(GsonHelper.getAsString(jsonObject, "catType")) : null;

			return new EntityOnly(effects, nbt, flags, equipment, player, fishHook, team, catType);
		} else {
			return ANY;
		}
	}

	static {
		ANY = new EntityOnly(MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, FishingHookPredicate.ANY, (String) null, (ResourceLocation) null);
	}

	@Override
	public boolean equals(Object obj) {
		// not worth elaborating
		return this == obj;
	}
}
