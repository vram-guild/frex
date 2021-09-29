package io.vram.frex.impl.world;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.world.level.block.entity.BlockEntity;

@Internal
public interface BlockEntityRenderDataProviderAccess {
	Function<BlockEntity, Object> frxGetDataProvider();

	void frxSetDataProvider(Function<BlockEntity, Object> provider);
}
