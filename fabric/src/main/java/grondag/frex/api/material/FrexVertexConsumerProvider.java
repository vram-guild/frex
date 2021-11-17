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

package grondag.frex.api.material;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Extension of {@code VertexConsumerProvider} that provides consumers
 * via {@code RenderMaterial} in addition to render layer.  Use this to
 * emit polygons in Entity and BlockEntity renderers that rely on a custom
 * {@code RenderMaterial}.
 *
 * <p>If the renderer implementation supports this feature, then {@code VertexConsumerProvider}
 * parameters passed during world rendering can probably be safely cast to this interface.
 * However, some mods could make calls to block or entity renderer such that
 * this interface isn't available there.
 */
@Deprecated
public interface FrexVertexConsumerProvider {
	/**
	 * Obtain the appropriate vertex consumer for the given material.
	 * @param material  The material of one or more polygons to be rendered.
	 * @return VertexConsumer instance to accept polygons for the given material.  May not be a vanilla implementation.
	 */
	@Deprecated
	VertexConsumer getConsumer(RenderMaterial material);
}
