/*
 * Copyright © Contributing Authors
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

package io.vram.frex.base.client.model;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.model.provider.ModelProvider;
import io.vram.frex.api.model.provider.ModelProviderRegistry;
import io.vram.frex.api.renderer.Renderer;

public class StaticModel extends BaseModel {
	protected final Mesh mesh;

	protected StaticModel(Builder builder, Function<Material, TextureAtlasSprite> spriteFunc) {
		super(builder, spriteFunc);
		mesh = builder.meshFactory.createMesh(Renderer.get().meshBuilder(), Renderer.get().materials().materialFinder(), n -> spriteFunc.apply(new Material(TextureAtlas.LOCATION_BLOCKS, n)));
	}

	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		mesh.outputTo(output.asQuadEmitter());
	}

	@Override
	public void renderAsItem(ItemInputContext input, QuadSink output) {
		mesh.outputTo(output.asQuadEmitter());
	}

	@Override
	protected Mesh fallbackMesh(BlockState blockState, Direction face, Random random) {
		return mesh;
	}

	public static Builder builder(MeshFactory meshFactory) {
		return new Builder(meshFactory);
	}

	public static class Builder extends BaseModelBuilder<Builder> {
		protected final MeshFactory meshFactory;
		protected Mesh mesh;

		protected Builder(MeshFactory meshFactory) {
			this.meshFactory = meshFactory;
		}

		@Override
		public BakedModel bakeOnce(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation) {
			return new StaticModel(this, spriteFunc);
		}

		public void setMesh(Mesh mesh) {
			this.mesh = mesh;
		}
	}

	public static Function<ResourceManager, ModelProvider<ModelResourceLocation>> createProviderFunction(Consumer<Builder> setupFunc, MeshFactory meshFactory) {
		return (rm) -> {
			final var builder = new Builder(meshFactory);
			setupFunc.accept(builder);
			return (path, subModelLoader) -> builder;
		};
	}

	public static void registerSimpleCubeModel(ResourceLocation blockPath, ResourceLocation spritePath, int color, Function<MaterialFinder, RenderMaterial> materialFunc) {
		final MeshFactory meshFactory = (meshBuilder, materialFinder, spriteFunc) ->
			meshBuilder.box(materialFunc.apply(materialFinder), color, spriteFunc.getSprite(spritePath), 0, 0, 0, 1, 1, 1).build();

		ModelProviderRegistry.registerBlockItemProvider(StaticModel.createProviderFunction(b -> b.defaultParticleSprite(spritePath), meshFactory), blockPath);
	}

	public static void registerSimpleCubeModel(String blockPath, String spritePath, int color, Function<MaterialFinder, RenderMaterial> materialFunc) {
		registerSimpleCubeModel(new ResourceLocation(blockPath), new ResourceLocation(spritePath), color, materialFunc);
	}
}
