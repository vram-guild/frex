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

package io.vram.frex.base.renderer.context.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import io.vram.frex.api.buffer.QuadEmitter;
import io.vram.frex.api.material.MaterialFinder;
import io.vram.frex.api.material.MaterialMap;
import io.vram.frex.api.material.RenderMaterial;
import io.vram.frex.api.model.DynamicModel;
import io.vram.frex.base.renderer.context.input.BaseInputContext;
import io.vram.frex.base.renderer.mesh.BaseQuadEmitter;
import io.vram.frex.base.renderer.mesh.MeshEncodingHelper;
import io.vram.frex.base.renderer.mesh.RootQuadEmitter;
import io.vram.frex.pastel.PastelTerrainRenderContext;

/**
 * Base class for all render contexts. ender contexts handle state management
 * and access required by models. Holds shared fields and utility methods
 * common to sub-types but the behavior and inputs for each type of
 * context vary too much to define abstract methods at this level.
 *
 * <p>The same type of model can have different contexts. A block rendering as
 * terrain vs a falling blocks rendering as an entity is one example.
 *
 * <p>The functionality of sub-types will fall into up to
 * five phases of activity:
 * <ul>
 *   <li><em>Batch Setup Phase: </em>
 *   Contexts with state shared across multiple game objects will need a method
 *   to initialize it.  The method will be called before a batch of one or more
 *   game objects (and their models) are rendered. {@link PastelTerrainRenderContext#prepareForRegion} is one example.
 *   Some contexts do not need this phase.
 *   </li>
 *   <li><em>Model Setup Phase: </em>
 *   Models assume some state is established before they output. Render contexts
 *   will have methods for callers to initialize model-specific state as game
 *   objects are iterated. The context will also use this for internal housekeeping
 *   like per-model cache invalidation.
 *   </li>
 *   <li><em>Model Output Phase: </em>
 *   Control of execution is inverted while models render. Most implement one or
 *   more methods to be called from injections in code that handles vanilla model
 *   rendering for a given model type and scenario.  The methods are highly specific
 *   to individual use cases so no attempt is made to define them in the base class.
 *
 *   <p>Once invoked, usually with a model or game object as inputs, the render
 *   context provides some flavor of InputContext plus a QaudSink, calls some
 *   variant of {@link DynamicModel#renderDynamic} and the model reacts by emitting
 *   zero or more polygons until it completes.
 *
 *   <p>During this phase the context satisfies many API requirements that aren't
 *   directly visible to or controlled by the model: material mapping,
 *   quad transformation, routing of vertices to the correct buffer, etc.
 *
 *   <p>This phase varies significantly by context but some elements can be
 *   reliably shared across sub-types and most of the members of this class
 *   relate to Model Output. (See individual method and field descriptions.)
 *   </li>
 *   <li><em>Model Cleanup Phase: </em>
 *   Some contexts have methods to release resources or manage shared state
 *   after model output is complete.
 *   </li>
 *   <li><em>Batch Cleanup Phase: </em>
 *   Some contexts have methods to release resources or manage shared state
 *   after a batch of models is complete.  The context will have no way to
 *   know that a batch is done, so these methods must always be called by the
 *   code that is iterating the batch.
 *   </li>
 * </ul>
 */
public abstract class BaseRenderContext<C extends BaseInputContext> {
	/**
	 * Convenient and fast access to the default (empty) material map.
	 * During the model setup phase, {@link #materialMap} should be set to
	 * this value if no object-specific material map can be obtained.
	 *
	 * <p>Because this is a final instance value, fast `==` comparison can be
	 * used to test if a non-default material map is present and material mapping
	 * skipped if not.
	 */
	protected static final MaterialMap defaultMap = MaterialMap.defaultMaterialMap();

	protected final MaterialFinder finder = MaterialFinder.newInstance();

	/**
	 * Received output from models. Can be shared across sub types
	 * because all model interfaces depend on QuadSink.  Note that this
	 * is a child class because the {@link QuadEmitter#emit()}
	 */
	protected final RootQuadEmitter emitter = new Emitter();

	public final C inputContext;

	/**
	 * Handles material mapping for whatever game object/model is being rendered.
	 * Should be initialized during the model setup phase so that it is ready
	 * for model output because material maps are always specific to game object.
	 *
	 * <p>Material maps can also select based on sprite, which cannot be known
	 * until each polygon is emitted and all model transformations are complete.
	 *
	 * <p>This base class includes {@link #mapMaterials(BaseQuadEmitter)} for the
	 * per-quad handling but the setup for each game object must live in sub-types.
	 */
	protected MaterialMap materialMap = defaultMap;

	protected BaseRenderContext() {
		inputContext = createInputContext();
	}

	/** Type-specific factory used in constructor. Not accepted
	 * as a lambda function for easier inheritance and because
	 * some implementations will be non-trivial and/or require access to
	 * state of this instance.
	 */
	protected abstract C createInputContext();

	/**
	 * Called from the emitter during model output after each
	 * polygon is output by the model. The emitter will have
	 * already applied transformations if active.
	 *
	 * <p>Handles tint, material mapping and face culling, all of which
	 * must happen after transformation. Also handles lighting and
	 * shading tasks that depend on the specific model and context
	 * and on the lighting model and configuration of the current renderer.
	 */
	public abstract void renderQuad();

	/**
	 * Applies matrix transforms to vertex data, including block offsets,
	 * and transfers vertex data from the emitter to the appropriate buffer.
	 * This is also where material properties are mapped to a RenderType or
	 * otherwise encoded so that the back end knows how to render the content.
	 *
	 * <p>Encoding is highly specific to implementation.  FREX provides a
	 * reasonable default encoder for vanilla-style pipelines but advanced
	 * implementations will almost certainly have their own solution.
	 *
	 * <p>It is important that block offsets and other spatial transformations
	 * residing in the matrix stack happen here and not earlier, so that model
	 * transformations can work with model geometry in a consistent coordinate space.
	 */
	protected abstract void encodeQuad();

	/**
	 * To be called from {@link #renderQuad()} to handle material mapping.
	 * This code should work for most implementations and context types.
	 *
	 * <p>Note that the interface to material maps could be simpler (something
	 * like `MaterialMap.apply()` if we taught MaterialMap how to read sprites
	 * but the design goal was to avoid coupling MaterialMap to QuadEmitter and
	 * sprite mechanics in the API.
	 */
	protected void mapMaterials(BaseQuadEmitter quad) {
		if (materialMap == defaultMap) {
			return;
		}

		final TextureAtlasSprite sprite = materialMap.needsSprite() ? quad.material().texture().spriteIndex().fromIndex(quad.spriteId()) : null;
		final RenderMaterial mapped = materialMap.getMapped(sprite);

		if (mapped != null) {
			quad.material(mapped);
		}
	}

	/**
	 * Provides access to the quad sink that accepts model output,
	 * ensuring it is cleared before every use.  Call this to obtain
	 * the second parameter for calls to {@code renderDynamic()},
	 * {@code renderAsBlock()}, etc.
	 */
	public QuadEmitter emitter() {
		emitter.clear();
		return emitter;
	}

	/**
	 * Where we handle all pre-buffer coloring, lighting, transformation, etc.
	 * Reused for all mesh quads. Fixed baking array sized to hold largest possible mesh quad.
	 */
	private class Emitter extends RootQuadEmitter {
		{
			data = new int[MeshEncodingHelper.TOTAL_MESH_QUAD_STRIDE];
			material(RenderMaterial.defaultMaterial());
		}

		@Override
		public Emitter emit() {
			complete();
			renderQuad();
			clear();
			return this;
		}
	}
}
