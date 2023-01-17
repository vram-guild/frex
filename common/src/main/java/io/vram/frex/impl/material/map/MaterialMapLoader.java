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

package io.vram.frex.impl.material.map;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.impl.FrexLog;
import io.vram.frex.impl.material.MaterialLoaderImpl;

@Internal
public class MaterialMapLoader {
	private MaterialMapLoader() { }

	public MaterialMap<BlockState> get(BlockState state) {
		return BLOCK_MAP.getOrDefault(state, MaterialMap.identity());
	}

	public MaterialMap<FluidState> get(FluidState fluidState) {
		return FLUID_MAP.getOrDefault(fluidState, MaterialMap.identity());
	}

	public MaterialMap<ItemStack> get(ItemStack itemStack) {
		return ITEM_MAP.getOrDefault(itemStack.getItem(), MaterialMap.identity());
	}

	public MaterialMap<Particle> get(ParticleType<?> particleType) {
		return PARTICLE_MAP.getOrDefault(particleType, MaterialMap.identity());
	}

	public MaterialMap<BlockState> get(BlockEntityType<?> blockEntityType) {
		return BLOCK_ENTITY_MAP.getOrDefault(blockEntityType, MaterialMap.identity());
	}

	public MaterialMap<Entity> get(EntityType<?> entityType) {
		return ENTITY_MAP.getOrDefault(entityType, MaterialMap.identity());
	}

	public void reload(ResourceManager manager) {
		MaterialLoaderImpl.reset();

		BLOCK_MAP.clear();

		for (Block block : BuiltInRegistries.BLOCK) {
			loadBlock(manager, block);
		}

		FLUID_MAP.clear();

		for (Fluid fluid : BuiltInRegistries.FLUID) {
			loadFluid(manager, fluid);
		}

		// NB: must come after block because uses block maps for block items
		ITEM_MAP.clear();

		for (Item item : BuiltInRegistries.ITEM) {
			loadItem(manager, item);
		}

		PARTICLE_MAP.clear();

		for (ParticleType<?> particleType : BuiltInRegistries.PARTICLE_TYPE) {
			loadParticle(manager, particleType);
		}

		BLOCK_ENTITY_MAP.clear();

		for (BlockEntityType<?> blockEntityType : BuiltInRegistries.BLOCK_ENTITY_TYPE) {
			loadBlockEntity(manager, blockEntityType);
		}

		ENTITY_MAP.clear();

		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			loadEntity(manager, entityType);
		}
	}

	private void loadBlock(ResourceManager manager, Block block) {
		final ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

		final ResourceLocation id = new ResourceLocation(blockId.getNamespace(), "materialmaps/block/" + blockId.getPath() + ".json");

		try {
			final var res = manager.getResource(id);

			if (res.isPresent()) {
				MaterialMapDeserializer.deserialize(block.getStateDefinition().getPossibleStates(), id, new InputStreamReader(res.get().open(), StandardCharsets.UTF_8), BLOCK_MAP);
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load block material map " + id.toString() + " due to exception " + e.toString());
		}
	}

	private void loadFluid(ResourceManager manager, Fluid fluid) {
		final ResourceLocation blockId = BuiltInRegistries.FLUID.getKey(fluid);

		final ResourceLocation id = new ResourceLocation(blockId.getNamespace(), "materialmaps/fluid/" + blockId.getPath() + ".json");

		try {
			final var res = manager.getResource(id);

			if (res.isPresent()) {
				MaterialMapDeserializer.deserialize(fluid.getStateDefinition().getPossibleStates(), id, new InputStreamReader(res.get().open(), StandardCharsets.UTF_8), FLUID_MAP);
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load fluid material map " + id.toString() + " due to exception " + e.toString());
		}
	}

	private void loadItem(ResourceManager manager, Item item) {
		final ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);

		final ResourceLocation id = new ResourceLocation(itemId.getNamespace(), "materialmaps/item/" + itemId.getPath() + ".json");

		try {
			final var res = manager.getResource(id);

			if (res.isPresent()) {
				ItemMaterialMapDeserializer.deserialize(item, id, new InputStreamReader(res.get().open(), StandardCharsets.UTF_8), ITEM_MAP);
			} else {
				// fall back to block map for block items
				// otherwise material maps are not required
				if (item instanceof BlockItem) {
					final var defaultBlockState = ((BlockItem) item).getBlock().defaultBlockState();
					final MaterialMap<BlockState> blockMap = BLOCK_MAP.get(defaultBlockState);

					if (blockMap != null) {
						ITEM_MAP.put(item, (f, i, s) -> blockMap.map(f, defaultBlockState, s));
					}
				}
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load block material map " + id.toString() + " due to exception " + e.toString());
		}
	}

	private void loadParticle(ResourceManager manager, ParticleType<?> particleType) {
		final ResourceLocation particleId = BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);

		if (particleId == null) return;

		final ResourceLocation id = new ResourceLocation(particleId.getNamespace(), "materialmaps/particle/" + particleId.getPath() + ".json");

		try {
			final var res = manager.getResource(id);

			if (res.isPresent()) {
				ParticleMaterialMapDeserializer.deserialize(particleType, id, new InputStreamReader(res.get().open(), StandardCharsets.UTF_8), PARTICLE_MAP);
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load particle material map " + id.toString() + " due to exception " + e.toString());
		}
	}

	private void loadBlockEntity(ResourceManager manager, BlockEntityType<?> blockEntityType) {
		final ResourceLocation blockEntityId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntityType);

		if (blockEntityId == null) return;

		final ResourceLocation id = new ResourceLocation(blockEntityId.getNamespace(), "materialmaps/block_entity/" + blockEntityId.getPath() + ".json");

		try {
			final List<Resource> resources = manager.getResourceStack(id);

			if (resources.size() > 0) {
				BlockEntityMaterialMapDeserializer.deserialize(blockEntityType, id, resources, BLOCK_ENTITY_MAP);
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load block entity material map " + id.toString() + " due to exception " + e.toString());
		}
	}

	private void loadEntity(ResourceManager manager, EntityType<?> entityType) {
		final ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
		final ResourceLocation id = new ResourceLocation(entityId.getNamespace(), "materialmaps/entity/" + entityId.getPath() + ".json");

		try {
			final List<Resource> resources = manager.getResourceStack(id);

			if (resources.size() > 0) {
				EntityMaterialMapDeserializer.deserialize(entityType, id, resources, ENTITY_MAP);
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load block entity material map " + id.toString() + " due to exception " + e.toString());
		}
	}

	private static final IdentityHashMap<BlockState, MaterialMap<BlockState>> BLOCK_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<FluidState, MaterialMap<FluidState>> FLUID_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<Item, MaterialMap<ItemStack>> ITEM_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<ParticleType<?>, MaterialMap<Particle>> PARTICLE_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<BlockEntityType<?>, MaterialMap<BlockState>> BLOCK_ENTITY_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<EntityType<?>, MaterialMap<Entity>> ENTITY_MAP = new IdentityHashMap<>();

	public static final MaterialMapLoader INSTANCE = new MaterialMapLoader();
}
