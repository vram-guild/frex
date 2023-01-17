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

package io.vram.frex.base.client.model;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.model.provider.ModelProvider;
import io.vram.frex.api.model.provider.ModelProviderRegistry;
import io.vram.frex.api.model.util.BakedModelUtil;

public class StaticMeshModel extends BaseModel {
	protected WeakReference<List<BakedQuad>[]> quadLists = null;
	protected final Mesh mesh;

	protected StaticMeshModel(Builder builder, Function<Material, TextureAtlasSprite> spriteFunc) {
		super(builder, spriteFunc);
		mesh = builder.meshFactory.createMesh(n -> spriteFunc.apply(new Material(InventoryMenu.BLOCK_ATLAS, n)));
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
	public List<BakedQuad> getQuads(BlockState blockState, Direction face, RandomSource random) {
		List<BakedQuad>[] lists = quadLists == null ? null : quadLists.get();

		if (lists == null) {
			lists = BakedModelUtil.toQuadLists(mesh);
			quadLists = new WeakReference<>(lists);
		}

		final List<BakedQuad> result = lists[face == null ? 6 : face.get3DDataValue()];
		return result == null ? ImmutableList.of() : result;
	}

	public static Builder builder(MeshFactory meshFactory) {
		return new Builder(meshFactory);
	}

	public static class Builder extends BaseModelBuilder<Builder> {
		protected final MeshFactory meshFactory;

		protected Builder(MeshFactory meshFactory) {
			this.meshFactory = meshFactory;
		}

		@Override
		public BakedModel bakeOnce(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation) {
			return new StaticMeshModel(this, spriteFunc);
		}
	}

	public static BiFunction<Map<ResourceLocation, BlockModel>, Map<ResourceLocation, List<ModelBakery.LoadedJson>>, ModelProvider<ModelResourceLocation>> createProvider(Consumer<Builder> setupFunc, MeshFactory meshFactory) {
		return (models, blockStates) -> {
			final var builder = new Builder(meshFactory);
			setupFunc.accept(builder);
			return (path, subModelLoader) -> builder;
		};
	}

	public static void createAndRegisterProvider(Consumer<Builder> setupFunc, MeshFactory meshFactory, ResourceLocation... paths) {
		ModelProviderRegistry.registerBlockItemProvider(createProvider(setupFunc, meshFactory), paths);
	}

	public static void createAndRegisterProvider(Consumer<Builder> setupFunc, MeshFactory meshFactory, String... paths) {
		ModelProviderRegistry.registerBlockItemProvider(createProvider(setupFunc, meshFactory), paths);
	}

	public static void createAndRegisterCube(ResourceLocation blockPath, ResourceLocation spritePath, int color, Function<MaterialFinder, RenderMaterial> materialFunc) {
		final MeshFactory meshFactory = MeshFactory.shared((meshBuilder, materialFinder, spriteFunc) ->
			meshBuilder.box(materialFunc.apply(materialFinder), color, spriteFunc.getSprite(spritePath), 0, 0, 0, 1, 1, 1).build());

		ModelProviderRegistry.registerBlockItemProvider(StaticMeshModel.createProvider(b -> b.defaultParticleSprite(spritePath), meshFactory), blockPath);
	}

	public static void createAndRegisterCube(String blockPath, String spritePath, int color, Function<MaterialFinder, RenderMaterial> materialFunc) {
		createAndRegisterCube(new ResourceLocation(blockPath), new ResourceLocation(spritePath), color, materialFunc);
	}
}
