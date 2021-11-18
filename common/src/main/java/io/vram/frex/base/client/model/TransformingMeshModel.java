/*
 * Copyright © Original Authors
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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import org.spongepowered.include.com.google.common.base.Preconditions;

import net.minecraft.client.renderer.block.model.BakedQuad;
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

import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.buffer.QuadTransform;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.model.InputContext;
import io.vram.frex.api.model.provider.ModelProvider;
import io.vram.frex.api.model.provider.ModelProviderRegistry;
import io.vram.frex.api.model.util.BakedModelUtil;
import io.vram.frex.impl.model.ModelProviderRegistryImpl;

public class TransformingMeshModel extends BaseModel {
	protected WeakReference<List<BakedQuad>[]> quadLists = null;
	protected final Mesh mesh;
	protected final QuadTransform transform;

	protected TransformingMeshModel(Builder builder, Function<Material, TextureAtlasSprite> spriteFunc) {
		super(builder, spriteFunc);
		mesh = builder.meshFactory.createMesh(n -> spriteFunc.apply(new Material(TextureAtlas.LOCATION_BLOCKS, n)));
		this.transform = builder.transform;
	}

	protected void render(InputContext input, QuadSink output) {
		final QuadEmitter emitter = output.withTransformQuad(input, transform);
		mesh.outputTo(emitter);
		emitter.close();
	}

	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		render(input, output);
	}

	@Override
	public void renderAsItem(ItemInputContext input, QuadSink output) {
		render(input, output);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction face, Random random) {
		List<BakedQuad>[] lists = quadLists == null ? null : quadLists.get();

		if (lists == null) {
			lists = BakedModelUtil.toQuadLists(mesh);
			quadLists = new WeakReference<>(lists);
		}

		final List<BakedQuad> result = lists[face == null ? 6 : face.get3DDataValue()];
		return result == null ? ImmutableList.of() : result;
	}

	public static Builder builder(MeshFactory meshFactory, QuadTransform transform) {
		return new Builder(meshFactory, transform);
	}

	public static class Builder extends BaseModelBuilder<Builder> {
		protected final MeshFactory meshFactory;
		protected final QuadTransform transform;

		protected Builder(MeshFactory meshFactory, QuadTransform transform) {
			this.meshFactory = meshFactory;
			this.transform = transform;
		}

		@Override
		public BakedModel bakeOnce(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation) {
			return new TransformingMeshModel(this, spriteFunc);
		}
	}

	public static Function<ResourceManager, ModelProvider<ModelResourceLocation>> createProvider(Consumer<Builder> setupFunc, MeshFactory meshFactory, QuadTransform transform) {
		return (rm) -> {
			final var builder = new Builder(meshFactory, transform);
			setupFunc.accept(builder);
			return (path, subModelLoader) -> builder;
		};
	}

	public static void createAndRegisterProvider(Consumer<Builder> setupFunc, MeshFactory meshFactory, QuadTransform transform, ResourceLocation... paths) {
		ModelProviderRegistry.registerBlockItemProvider(createProvider(setupFunc, meshFactory, transform), paths);
	}

	public static void createAndRegisterProvider(Consumer<Builder> setupFunc, MeshFactory meshFactory, QuadTransform transform, String... paths) {
		ModelProviderRegistry.registerBlockItemProvider(createProvider(setupFunc, meshFactory, transform), paths);
	}

	public static void createAndRegisterCube(ResourceLocation spritePath, int color, Function<MaterialFinder, RenderMaterial> materialFunc, QuadTransform transform, ResourceLocation... paths) {
		final MeshFactory meshFactory = MeshFactory.shared((meshBuilder, materialFinder, spriteFunc) ->
			meshBuilder.box(materialFunc.apply(materialFinder), color, spriteFunc.getSprite(spritePath), 0, 0, 0, 1, 1, 1).build());

		ModelProviderRegistry.registerBlockItemProvider(createProvider(b -> b.defaultParticleSprite(spritePath), meshFactory, transform), paths);
	}

	public static void createAndRegisterCube(String spritePath, int color, Function<MaterialFinder, RenderMaterial> materialFunc, QuadTransform transform, String... paths) {
		Preconditions.checkNotNull(paths);
		createAndRegisterCube(new ResourceLocation(spritePath), color, materialFunc, transform, ModelProviderRegistryImpl.stringsToLocations(paths));
	}
}
