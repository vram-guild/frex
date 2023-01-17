package io.vram.frex.impl.model;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public final class BiomeLookupHelper {
	private static HolderLookup.Provider LOOKUP = VanillaRegistries.createLookup();

	public static Biome getOrThrow(ResourceKey<Biome> biomeKey) {
		return LOOKUP.lookupOrThrow(Registries.BIOME).getOrThrow(biomeKey).value();
	}
}
