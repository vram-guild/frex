package io.vram.frex.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import io.vram.frex.impl.texture.SpriteInjectorImpl;

@Mixin(SpriteResourceLoader.class)
public class MixinSpriteResourceLoader {
	@Inject(at = @At(value = "RETURN"), method = "load", locals = LocalCapture.CAPTURE_FAILHARD)
	private static void afterLoad(ResourceManager resourceManager, ResourceLocation resourceLocation, CallbackInfoReturnable<SpriteResourceLoader> cir, ResourceLocation resourceLocation2, List<SpriteSource> list) {
		SpriteInjectorImpl.forEachInjection(resourceLocation, e -> list.add(new SingleFile(resourceLocation, Optional.of(e))));
	}
}
