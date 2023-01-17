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
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import org.spongepowered.include.com.google.common.base.Preconditions;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.model.BlockModel;
import io.vram.frex.api.model.ItemModel;
import io.vram.frex.api.model.provider.ModelProvider;
import io.vram.frex.api.model.provider.ModelProviderRegistry;
import io.vram.frex.api.model.util.BakedModelUtil;

public class ProceduralModel extends BaseModel {
	protected final BlockModel blockModel;
	protected final ItemModel itemModel;
	protected final MeshFactory defaultMeshFactory;
	protected WeakReference<List<BakedQuad>[]> quadLists = null;
	protected boolean shouldWarn = true;
	protected String label;

	protected ProceduralModel(Builder builder, Function<Material, TextureAtlasSprite> spriteFunc) {
		super(builder, spriteFunc);
		blockModel = builder.blockModel;
		itemModel = builder.itemModel;
		defaultMeshFactory = builder.defaultMeshFactory;
	}

	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		blockModel.renderAsBlock(input, output);
	}

	@Override
	public void renderAsItem(ItemInputContext input, QuadSink output) {
		itemModel.renderAsItem(input, output);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction face, RandomSource random) {
		final WeakReference<List<BakedQuad>[]> listReference = quadLists;
		List<BakedQuad>[] lists = listReference == null ? null : listReference.get();

		if (lists == null) {
			lists = BakedModelUtil.toQuadLists(defaultMeshFactory.createMesh(SpriteProvider.forBlocksAndItems()));
			quadLists = new WeakReference<>(lists);
		}

		final List<BakedQuad> result = lists[face == null ? 6 : face.get3DDataValue()];
		return result == null ? ImmutableList.of() : result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends BaseModelBuilder<Builder> {
		protected BlockModel blockModel = BlockModel.EMPTY;
		protected ItemModel itemModel = ItemModel.EMPTY;
		protected MeshFactory defaultMeshFactory = MeshFactory.EMPTY;

		public Builder blockModel(BlockModel blockModel) {
			Preconditions.checkNotNull(blockModel);
			this.blockModel = blockModel;
			return this;
		}

		public Builder itemModel(ItemModel itemModel) {
			Preconditions.checkNotNull(itemModel);
			this.itemModel = itemModel;
			return this;
		}

		public Builder defaultMeshFactory(MeshFactory defaultMeshFactory) {
			Preconditions.checkNotNull(defaultMeshFactory);
			this.defaultMeshFactory = defaultMeshFactory;
			return this;
		}

		@Override
		public BakedModel bakeOnce(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation) {
			return new ProceduralModel(this, spriteFunc);
		}
	}

	public static Function<Map<ResourceLocation, net.minecraft.client.renderer.block.model.BlockModel>, ModelProvider<ModelResourceLocation>> createProvider(Consumer<Builder> setupFunc) {
		return (rm) -> {
			final var builder = new Builder();
			setupFunc.accept(builder);
			return (path, subModelLoader) -> builder;
		};
	}

	public static void createAndRegisterProvider(Consumer<Builder> setupFunc, ResourceLocation... paths) {
		ModelProviderRegistry.registerBlockItemProvider(createProvider(setupFunc), paths);
	}

	public static void createAndRegisterProvider(Consumer<Builder> setupFunc, String... paths) {
		ModelProviderRegistry.registerBlockItemProvider(createProvider(setupFunc), paths);
	}
}
