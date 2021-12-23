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

package io.vram.frex.api.texture;

import java.nio.ByteBuffer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public interface PhysicalTextureSet extends AutoCloseable {
	ResourceLocation baseColorId();

	int imageWidth();

	int imageHeight();

	AnimationMetadataSection animationData();

	default int pixels() {
		return imageWidth() * imageHeight();
	}

	/*
	 * Controls if the fragment material is dialectric or metallic.
	 * For dialectric materials, this provides the F0 value.
	 *
	 * The range of values is not continuous to maximize floating point
	 * precision in useful ranges.  Dialectic materials
	 * will have values ranging from 0.0 to 0.1 and metals will
	 * have the value 1.0.
	 *
	 * When the material is dialectric, frx_fragColor models
	 * diffuse reflected color and frx_fragReflectance
	 * is the linear monochrome specular reflectance (F0).
	 *
	 * When the material is metallic, frx_fragColor
	 * models specular reflectance, which for metals is often not
	 * uniform across the light spectrum and responsible for the perceived
	 * color of metals.
	 *
	 * Initial value when frx_materialFragment() is called will
	 * be sampled from the metalness texture map.
	 *
	 * Not available in depth pass.
	 * Not available if PBR is inactive.
	 * Gate usage with #ifdef PBR_ENABLED.
	 */
	ByteBuffer reflectance();

	/*
	 * Fragment normal in tangent space.
	 *
	 * Initial value when frx_materialFragment() is called will
	 * be sampled from the normal map.
	 *
	 * The pipeline shader will consume this for lighting. If any
	 * transformation is needed because of smoothed vertex normals or
	 * because the pipeline computes lighting in world space, those
	 * transformations will be done by the pipeline shader
	 * after frx_materialFragment() runs.
	 *
	 * Not available in depth pass.
	 * Not available if PBR is inactive.
	 * Gate usage with #ifdef PBR_ENABLED.
	 */
	ByteBuffer normal();

	/*
	 * Base height map value to be consumed by pipeline for lighting.
	 * Values range from -0.25 to 0.25 and represent distance below or above
	 * the triangle plane in tangent space.
	 *
	 * Initial value when frx_materialFragment() is called will be sampled
	 * from the height texture map.
	 *
	 * The primary use for this by material shaders is to provide PBR support for
	 * animated, proecedural surfaces.
	 *
	 * Not available in depth pass.
	 * Not available if PBR is inactive.
	 * Gate usage with #ifdef PBR_ENABLED.
	 */
	ByteBuffer height();

	/*
	 * Models random light scattering caused by tiny surface irregularities.
	 * Values range from 0.0 (perfectly smooth) to 1.0 (rough as possible.)
	 *
	 * Initial value when frx_materialFragment() is called will be sampled
	 * from the roughness texture map.
	 *
	 * Not available in depth pass.
	 * Not available if PBR is inactive.
	 * Gate usage with #ifdef PBR_ENABLED.
	 */
	ByteBuffer roughness();

	/*
	 * Emissive value of this fragment.
	 *
	 * Initial value when frx_materialFragment() is called will
	 * be frx_sampleEmissive if material maps are available, or
	 * frx_matEmissiveFactor otherwise.
	 *
	 * The pipeline shader will consume this for lighting after
	 * frx_materialFragment() runs.
	 *
	 * Not available in depth pass.
	 * Gate usage with #ifndef DEPTH_PASS.
	 */
	ByteBuffer emissive();

	/*
	 * Models self-shading of small variations in irregular surfaces.
	 * It should not be used for shading by other objects in the world.
	 *
	 * Initial value when frx_materialFragment() is called will be sampled
	 * from the AO texture map.
	 *
	 * The primary use for this by material shaders is to provide PBR support for
	 * animated, proecedural surfaces.
	 *
	 * Macro-scale AO is controlled and applied by the pipeline.  See frx_fragDisableAo.
	 *
	 * Not available in depth pass.
	 * Not available if PBR is inactive.
	 * Gate usage with #ifdef PBR_ENABLED.
	 */
	ByteBuffer ao();

	/**
	 * @param id
	 * @return
	 */
	static @Nullable PhysicalTextureSet load(ResourceManager rm, ResourceLocation id) {
		return null;
	}
}
