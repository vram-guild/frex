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
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import org.spongepowered.include.com.google.common.base.Preconditions;

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

import io.vram.frex.api.buffer.QuadSink;
import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.model.provider.ModelProvider;
import io.vram.frex.api.model.provider.ModelProviderRegistry;
import io.vram.frex.api.model.util.BakedModelUtil;
import io.vram.frex.impl.FrexLog;

// WIP: name can't stay
public class DynamicModel extends BaseModel {
	protected final Int2ObjectFunction<Mesh> meshFactory;
	protected final int keyCount;
	protected final Mesh[] meshes;
	protected final WeakReference<List<BakedQuad>[]>[] quadLists;
	protected final BlockKeyFunction blockKeyFunction;
	protected final ItemKeyFunction itemKeyFunction;
	protected final VanillaKeyFunction vanillaKeyFunction;
	protected boolean shouldWarn = true;
	protected String label;

	@SuppressWarnings("unchecked")
	protected DynamicModel(Builder builder, Function<Material, TextureAtlasSprite> spriteFunc) {
		super(builder, spriteFunc);
		meshFactory = builder.meshFactory;
		keyCount = builder.keyCount;
		meshes = new Mesh[keyCount];
		quadLists = new WeakReference[keyCount];
		blockKeyFunction = builder.blockKeyFunction;
		itemKeyFunction = builder.itemKeyFunction;
		vanillaKeyFunction = builder.vanillaKeyFunction;
	}

	protected Mesh getMesh(int key) {
		if (key < 0 || key >= keyCount) {
			if (shouldWarn) {
				shouldWarn = false;
				FrexLog.LOG.warn("Invalid key result in DynamicModel " + label +". Subsequent errors will be supressed", new Throwable());
				return Mesh.EMPTY;
			}
		}

		Mesh result = meshes[key];

		if (result == null) {
			result = meshFactory.apply(key);
			meshes[key] = result;
		}

		return result;
	}

	@Override
	public void renderAsBlock(BlockInputContext input, QuadSink output) {
		getMesh(blockKeyFunction.computeKey(input)).outputTo(output);
	}

	@Override
	public void renderAsItem(ItemInputContext input, QuadSink output) {
		getMesh(itemKeyFunction.computeKey(input)).outputTo(output);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction face, Random random) {
		final int key = vanillaKeyFunction.computeKey(blockState, random);

		if (key < 0 || key >= keyCount) {
			if (shouldWarn) {
				shouldWarn = false;
				FrexLog.LOG.warn("Invalid key result in DynamicModel " + label +". Subsequent errors will be supressed", new Throwable());
				return ImmutableList.of();
			}
		}

		final WeakReference<List<BakedQuad>[]> listReference = quadLists[key];
		List<BakedQuad>[] lists = listReference == null ? null : listReference.get();

		if (lists == null) {
			lists = BakedModelUtil.toQuadLists(getMesh(key));
			quadLists[key] = new WeakReference<>(lists);
		}

		final List<BakedQuad> result = lists[face == null ? 6 : face.get3DDataValue()];
		return result == null ? ImmutableList.of() : result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends BaseModelBuilder<Builder> {
		protected int keyCount = 1;
		protected Int2ObjectFunction<Mesh> meshFactory = i -> Mesh.EMPTY;
		protected BlockKeyFunction blockKeyFunction = c -> 0;
		protected ItemKeyFunction itemKeyFunction = c -> 0;
		protected VanillaKeyFunction vanillaKeyFunction = (b, r) -> 0;

		public Builder keyCount(int keyCount) {
			Preconditions.checkArgument(keyCount >= 0);
			Preconditions.checkArgument(keyCount < 4096);
			this.keyCount = keyCount;
			return this;
		}

		public Builder meshFactory(Int2ObjectFunction<Mesh> meshFactory) {
			Preconditions.checkNotNull(meshFactory);
			this.meshFactory = meshFactory;
			return this;
		}

		public Builder blockKeyFunction(BlockKeyFunction blockKeyFunction) {
			Preconditions.checkNotNull(blockKeyFunction);
			this.blockKeyFunction = blockKeyFunction;
			return this;
		}

		public Builder itemKeyFunction(ItemKeyFunction itemKeyFunction) {
			Preconditions.checkNotNull(itemKeyFunction);
			this.itemKeyFunction = itemKeyFunction;
			return this;
		}

		public Builder vanillaKeyFunction(VanillaKeyFunction vanillaKeyFunction) {
			Preconditions.checkNotNull(vanillaKeyFunction);
			this.vanillaKeyFunction = vanillaKeyFunction;
			return this;
		}

		@Override
		public BakedModel bakeOnce(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteFunc, ModelState modelState, ResourceLocation modelLocation) {
			return new DynamicModel(this, spriteFunc);
		}
	}

	public static Function<ResourceManager, ModelProvider<ModelResourceLocation>> createProvider(Consumer<Builder> setupFunc) {
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

	@FunctionalInterface
	public interface VanillaKeyFunction {
		int computeKey(BlockState blockState, Random random);
	}

	@FunctionalInterface
	public interface BlockKeyFunction {
		int computeKey(BlockInputContext input);
	}

	@FunctionalInterface
	public interface ItemKeyFunction {
		int computeKey(ItemInputContext input);
	}
}
