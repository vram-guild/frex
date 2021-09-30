package io.vram.frex.api.renderer;

@FunctionalInterface
public interface RendererProvider {
	Renderer getRenderer();

	default int priority() {
		return 0;
	}
}
