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

package io.vram.frex.api.mesh;

import net.minecraft.client.renderer.MultiBufferSource;

import io.vram.frex.api.material.RenderMaterial;

/**
 * Extension of {link MultiBufferSource} that provides consumers
 * via {@link RenderMaterial} in addition to render layer.  Use this to
 * emit polygons in Entity and BlockEntity renderers that rely on a custom
 * {@link RenderMaterial}.
 *
 * <p>If the renderer implementation supports this feature, then {@link RenderMaterial}
 * parameters passed during world rendering can probably be safely cast to this interface.
 * However, some mods could make calls to block or entity renderer such that
 * this interface isn't available there.
 */
public interface FrexBufferSource extends MultiBufferSource {
	/**
	 * Obtain the appropriate vertex consumer for the given material.
	 * @param material  The material of one or more polygons to be rendered.
	 * @return VertexConsumer instance to accept polygons for the given material.  May not be a vanilla implementation.
	 */
	FrexVertexConsumer getConsumer(RenderMaterial material);
}
