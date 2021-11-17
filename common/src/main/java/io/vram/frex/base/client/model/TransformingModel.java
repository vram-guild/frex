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

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.renderer.block.model.BakedQuad;
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
import io.vram.frex.api.model.BlockItemModel;
import io.vram.frex.api.model.provider.ModelProvider;
import io.vram.frex.api.model.provider.ModelProviderRegistry;

public class TransformingModel extends BaseModel {
	protected final Supplier<BlockItemModel> modelFunction;
	protected final QuadTransform transform;
	protected BlockItemModel wrapped = null;

	protected TransformingModel(Builder builder, Function<Material, TextureAtlasSprite> spriteFunc) {
		super(builder, spriteFunc);
		this.modelFunction = builder.modelFunction;
		this.transform = builder.transform;
	}

	protected BlockItemModel wrapped() {
		var result = wrapped;

		if (result == null) {
			result = modelFunction.get();
			wrapped = result;
		}

		return result;
	}

	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		final QuadEmitter emitter = output.withTransformQuad(input, transform);
		wrapped().renderAsBlock(input, emitter);
		emitter.close();
	}

	@Override
	public void renderAsItem(ItemInputContext input, QuadSink output) {
		final QuadEmitter emitter = output.withTransformQuad(input, transform);
		wrapped().renderAsItem(input, emitter);
		emitter.close();
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction face, Random random) {
		// Should not be called so we don't apply the transform here
		return ((BakedModel) wrapped()).getQuads(blockState, face, random);
	}

	public static Builder builder(Supplier<BlockItemModel> modelFunction, QuadTransform transform) {
		return new Builder(modelFunction, transform);
	}

	public static class Builder extends BaseModelBuilder<Builder> {
		protected final Supplier<BlockItemModel> modelFunction;
		protected final QuadTransform transform;

		protected Builder(Supplier<BlockItemModel> modelFunction, QuadTransform transform) {
			this.modelFunction = modelFunction;
			this.transform = transform;
		}

		@Override
		public BakedModel bakeOnce(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation) {
			return new TransformingModel(this, spriteFunc);
		}
	}

	public static Function<ResourceManager, ModelProvider<ModelResourceLocation>> createProviderFunction(Consumer<Builder> setupFunc, Supplier<BlockItemModel> modelFunction, QuadTransform transform) {
		return (rm) -> {
			final var builder = new Builder(modelFunction, transform);
			setupFunc.accept(builder);
			return (path, subModelLoader) -> builder;
		};
	}

	public static void registerTransformingModel(ResourceLocation blockPath, Consumer<Builder> setupFunc, Supplier<BlockItemModel> modelFunction, QuadTransform transform) {
		ModelProviderRegistry.registerBlockItemProvider(TransformingModel.createProviderFunction(setupFunc, modelFunction, transform), blockPath);
	}

	public static void registerTransformingModel(String blockPath, Consumer<Builder> setupFunc, Supplier<BlockItemModel> modelFunction, QuadTransform transform) {
		registerTransformingModel(new ResourceLocation(blockPath), setupFunc, modelFunction, transform);
	}
}
