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

package grondag.frex.api;

import java.util.function.BooleanSupplier;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

import io.vram.frex.api.material.MaterialCondition;

import grondag.frex.Frex;
import grondag.frex.api.material.MaterialFinder;

/**
 * Interface for rendering plug-ins that provide enhanced capabilities
 * for model lighting, buffering and rendering. Such plug-ins implement the
 * enhanced model rendering interfaces specified by the Fabric API.
 */
@Deprecated
public interface Renderer extends net.fabricmc.fabric.api.renderer.v1.Renderer {
	/** Will throw exception if not implemented. Check {@link Frex#isAvailable()} before calling. */
	static Renderer get() {
		if (Frex.isAvailable()) {
			return (Renderer) RendererAccess.INSTANCE.getRenderer();
		} else {
			throw new IllegalStateException("A mod tried to obtain a FREX renderer but no FREX implementation is active.");
		}
	}

	/**
	 * Obtain a new {@link MaterialFinder} instance used to retrieve
	 * standard {@link RenderMaterial} instances.
	 *
	 * <p>Renderer does not retain a reference to returned instances and they should be re-used for
	 * multiple materials when possible to avoid memory allocation overhead.
	 */
	@Override
	MaterialFinder materialFinder();

	@ScheduledForRemoval
	@Deprecated
	int maxSpriteDepth();

	@Experimental
	MaterialCondition createCondition(BooleanSupplier supplier, boolean affectBlocks, boolean affectItems);

	@Experimental
	MaterialCondition conditionById(ResourceLocation id);

	@Experimental
	boolean registerCondition(ResourceLocation id, MaterialCondition pipeline);

	/**
	 * Identical to {@link #registerMaterial(ResourceLocation, RenderMaterial)} except registrations
	 * are replaced if they already exist.  Meant to be used for materials that are loaded
	 * from resources and need to be updated during resource reload.
	 *
	 * <p>Note that mods retaining references to materials obtained from the registry will not
	 * use the new material definition unless they re-query.  Material maps will handle this
	 * automatically but mods must be designed to do so.
	 *
	 * <p>If this feature is not supported by the renderer, behaves like {@link #registerMaterial(ResourceLocation, RenderMaterial)}.
	 *
	 * <p>Returns false if a material with the given identifier was already present.
	 */
	default boolean registerOrUpdateMaterial(ResourceLocation id, RenderMaterial material) {
		return registerMaterial(id, material);
	}
}
