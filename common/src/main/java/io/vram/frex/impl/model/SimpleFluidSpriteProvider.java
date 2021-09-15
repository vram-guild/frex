package io.vram.frex.impl.model;

import java.util.function.Function;

import io.vram.frex.api.model.FluidSpriteProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class SimpleFluidSpriteProvider implements FluidSpriteProvider {
	private final Identifier stillSpriteName;
	private final Identifier flowingSpriteName;
	private final Identifier overlaySpriteName;
	private Sprite[] sprites = null;

	private SimpleFluidSpriteProvider(Identifier stillSpriteName, Identifier flowingSpriteName, @Nullable Identifier overlaySpriteName) {
		this.stillSpriteName = stillSpriteName;
		this.flowingSpriteName = flowingSpriteName;
		this.overlaySpriteName = overlaySpriteName;
		RELOAD_LIST.add(this);
	}

	@Override
	public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
		var result = sprites;

		if (result == null) {
			final boolean overlay = overlaySpriteName != null;
			result = new Sprite[overlay ? 3 : 2];
			final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
			result[0] = atlas.apply(stillSpriteName);
			result[1] = atlas.apply(flowingSpriteName);

			if (overlay) {
				result[2] = atlas.apply(overlaySpriteName);
			}

			sprites = result;
		}

		return result;
	}

	private void onReload() {
		sprites = null;
	}

	public static FluidSpriteProvider of(Identifier stillSpriteName, Identifier flowingSpriteName, @Nullable Identifier overlaySpriteName) {
		return new SimpleFluidSpriteProvider(stillSpriteName, flowingSpriteName, overlaySpriteName);
	}

	private static final ObjectArrayList<SimpleFluidSpriteProvider> RELOAD_LIST = new ObjectArrayList<>();

	public static void reload() {
		for (final var p : RELOAD_LIST) {
			p.onReload();
		}
	}
}
