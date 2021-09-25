package io.vram.frex.impl.material;

import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.renderer.Renderer;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class MaterialFinderPool {
	private MaterialFinderPool() { }

	private static ThreadLocal<MaterialFinder> POOL = ThreadLocal.withInitial(() -> Renderer.get().materialFinder());

	public static MaterialFinder threadLocal() {
		final MaterialFinder result = POOL.get();
		result.clear();
		return result;
	}
}
