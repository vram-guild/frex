/*
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.vram.frex.impl.material;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard;
import net.minecraft.client.renderer.RenderStateShard.LayeringStateShard;
import net.minecraft.client.renderer.RenderStateShard.OutputStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard;

import io.vram.frex.api.material.MaterialConstants;

public class RenderTypeShardHelper {
	public static int toMaterialDecal(LayeringStateShard phase) {
		if (phase == RenderStateShard.VIEW_OFFSET_Z_LAYERING) {
			return MaterialConstants.DECAL_VIEW_OFFSET;
		} else if (phase == RenderStateShard.POLYGON_OFFSET_LAYERING) {
			return MaterialConstants.DECAL_POLYGON_OFFSET;
		} else {
			return MaterialConstants.DECAL_NONE;
		}
	}

	public static int toMaterialDepthTest(DepthTestStateShard phase) {
		if (phase == RenderStateShard.NO_DEPTH_TEST) {
			return MaterialConstants.DEPTH_TEST_ALWAYS;
		} else if (phase == RenderStateShard.EQUAL_DEPTH_TEST) {
			return MaterialConstants.DEPTH_TEST_EQUAL;
		} else if (phase == RenderStateShard.LEQUAL_DEPTH_TEST) {
			return MaterialConstants.DEPTH_TEST_LEQUAL;
		} else {
			return MaterialConstants.DEPTH_TEST_DISABLE;
		}
	}

	public static int toMaterialTarget(OutputStateShard phase) {
		if (phase == RenderStateShard.TRANSLUCENT_TARGET) {
			return MaterialConstants.TARGET_TRANSLUCENT;
		} else if (phase == RenderStateShard.OUTLINE_TARGET) {
			return MaterialConstants.TARGET_OUTLINE;
		} else if (phase == RenderStateShard.PARTICLES_TARGET) {
			return MaterialConstants.TARGET_PARTICLES;
		} else if (phase == RenderStateShard.WEATHER_TARGET) {
			return MaterialConstants.TARGET_WEATHER;
		} else if (phase == RenderStateShard.CLOUDS_TARGET) {
			return MaterialConstants.TARGET_CLOUDS;
		} else if (phase == RenderStateShard.ITEM_ENTITY_TARGET) {
			return MaterialConstants.TARGET_ENTITIES;
		} else {
			assert phase == RenderStateShard.MAIN_TARGET : "Unsupported render target";
			return MaterialConstants.TARGET_MAIN;
		}
	}

	public static int toMaterialWriteMask(WriteMaskStateShard phase) {
		if (phase == RenderStateShard.COLOR_WRITE) {
			return MaterialConstants.WRITE_MASK_COLOR;
		} else if (phase == RenderStateShard.DEPTH_WRITE) {
			return MaterialConstants.WRITE_MASK_DEPTH;
		} else {
			return MaterialConstants.WRITE_MASK_COLOR_DEPTH;
		}
	}

	public static int toMaterialTransparency(TransparencyStateShard phase) {
		if (phase == RenderStateShard.ADDITIVE_TRANSPARENCY) {
			return MaterialConstants.TRANSPARENCY_ADDITIVE;
		} else if (phase == RenderStateShard.LIGHTNING_TRANSPARENCY) {
			return MaterialConstants.TRANSPARENCY_LIGHTNING;
		} else if (phase == RenderStateShard.GLINT_TRANSPARENCY) {
			return MaterialConstants.TRANSPARENCY_GLINT;
		} else if (phase == RenderStateShard.CRUMBLING_TRANSPARENCY) {
			return MaterialConstants.TRANSPARENCY_CRUMBLING;
		} else if (phase == RenderStateShard.TRANSLUCENT_TRANSPARENCY) {
			return MaterialConstants.TRANSPARENCY_TRANSLUCENT;
		} else {
			return MaterialConstants.TRANSPARENCY_NONE;
		}
	}
}
