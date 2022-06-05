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

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import io.vram.frex.api.material.BlockEntityMaterialMap;
import io.vram.frex.api.material.EntityMaterialMap;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.impl.FrexLog;

@Internal
public class MaterialMapLoader {
	private MaterialMapLoader() { }

	public MaterialMap get(BlockState state) {
		return BLOCK_MAP.getOrDefault(state, DEFAULT_MAP);
	}

	public MaterialMap get(FluidState fluidState) {
		return FLUID_MAP.getOrDefault(fluidState, DEFAULT_MAP);
	}

	public MaterialMap get(ItemStack itemStack) {
		return ITEM_MAP.getOrDefault(itemStack.getItem(), DEFAULT_MAP);
	}

	public MaterialMap get(ParticleType<?> particleType) {
		return PARTICLE_MAP.getOrDefault(particleType, DEFAULT_MAP);
	}

	public BlockEntityMaterialMap get(BlockEntityType<?> blockEntityType) {
		return BLOCK_ENTITY_MAP.getOrDefault(blockEntityType, BlockEntityMaterialMap.IDENTITY);
	}

	public EntityMaterialMap get(EntityType<?> entityType) {
		return ENTITY_MAP.getOrDefault(entityType, EntityMaterialMap.IDENTITY);
	}

	public void reload(ResourceManager manager) {
		MaterialLoaderImpl.reset();

		BLOCK_MAP.clear();
		final Iterator<Block> blocks = Registry.BLOCK.iterator();

		while (blocks.hasNext()) {
			loadBlock(manager, blocks.next());
		}

		FLUID_MAP.clear();
		final Iterator<Fluid> fluids = Registry.FLUID.iterator();

		while (fluids.hasNext()) {
			loadFluid(manager, fluids.next());
		}

		// NB: must come after block because uses block maps for block items
		ITEM_MAP.clear();
		final Iterator<Item> items = Registry.ITEM.iterator();

		while (items.hasNext()) {
			loadItem(manager, items.next());
		}

		PARTICLE_MAP.clear();
		final Iterator<ParticleType<?>> particles = Registry.PARTICLE_TYPE.iterator();

		while (particles.hasNext()) {
			loadParticle(manager, particles.next());
		}

		BLOCK_ENTITY_MAP.clear();
		final Iterator<BlockEntityType<?>> blockEntities = Registry.BLOCK_ENTITY_TYPE.iterator();

		while (blockEntities.hasNext()) {
			loadBlockEntity(manager, blockEntities.next());
		}

		ENTITY_MAP.clear();
		final Iterator<EntityType<?>> entities = Registry.ENTITY_TYPE.iterator();

		while (entities.hasNext()) {
			loadEntity(manager, entities.next());
		}
	}

	private void loadBlock(ResourceManager manager, Block block) {
		final ResourceLocation blockId = Registry.BLOCK.getKey(block);

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
		final ResourceLocation blockId = Registry.FLUID.getKey(fluid);

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
		final ResourceLocation itemId = Registry.ITEM.getKey(item);

		final ResourceLocation id = new ResourceLocation(itemId.getNamespace(), "materialmaps/item/" + itemId.getPath() + ".json");

		try {
			final var res = manager.getResource(id);

			if (res.isPresent()) {
				ItemMaterialMapDeserializer.deserialize(item, id, new InputStreamReader(res.get().open(), StandardCharsets.UTF_8), ITEM_MAP);
			} else {
				// fall back to block map for block items
				// otherwise material maps are not required
				if (item instanceof BlockItem) {
					final MaterialMap map = BLOCK_MAP.get(((BlockItem) item).getBlock().defaultBlockState());

					if (map != null) {
						ITEM_MAP.put(item, map);
					}
				}
			}
		} catch (final Exception e) {
			FrexLog.info("Unable to load block material map " + id.toString() + " due to exception " + e.toString());
		}
	}

	private void loadParticle(ResourceManager manager, ParticleType<?> particleType) {
		final ResourceLocation particleId = Registry.PARTICLE_TYPE.getKey(particleType);

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
		final ResourceLocation blockEntityId = Registry.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
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
		final ResourceLocation entityId = Registry.ENTITY_TYPE.getKey(entityType);
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

	public static final MaterialMap DEFAULT_MAP = new SingleMaterialMap(null);

	private static final IdentityHashMap<BlockState, MaterialMap> BLOCK_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<FluidState, MaterialMap> FLUID_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<Item, MaterialMap> ITEM_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<ParticleType<?>, MaterialMap> PARTICLE_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<BlockEntityType<?>, BlockEntityMaterialMap> BLOCK_ENTITY_MAP = new IdentityHashMap<>();
	private static final IdentityHashMap<EntityType<?>, EntityMaterialMap> ENTITY_MAP = new IdentityHashMap<>();

	public static final MaterialMapLoader INSTANCE = new MaterialMapLoader();
}
