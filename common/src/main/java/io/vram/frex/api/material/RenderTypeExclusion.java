package io.vram.frex.api.material;

import java.util.function.Predicate;

import io.vram.frex.impl.material.RenderTypeExclusionImpl;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

import net.minecraft.client.renderer.RenderType;

@NonExtendable
public interface RenderTypeExclusion {
	static void exclude(RenderType renderType) {
		RenderTypeExclusionImpl.exclude(renderType);
	}

	static void exclude(Predicate<RenderType> filter) {
		RenderTypeExclusionImpl.addFilter(filter);
	}

	static boolean isExcluded(RenderType renderType) {
		return RenderTypeExclusionImpl.isExcluded(renderType);
	}
}
