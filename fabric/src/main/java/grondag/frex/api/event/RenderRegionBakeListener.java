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

package grondag.frex.api.event;

import java.util.List;
import java.util.function.Predicate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import io.vram.frex.impl.world.RenderRegionBakeListenerImpl;

@Environment(EnvType.CLIENT)
@Deprecated
@FunctionalInterface
public interface RenderRegionBakeListener extends io.vram.frex.api.world.RenderRegionBakeListener {
	static void register(Predicate<? super io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext> predicate, io.vram.frex.api.world.RenderRegionBakeListener listener) {
		RenderRegionBakeListenerImpl.register(predicate, listener);
	}

	/**
	 * For use by renderer implementations.  Implementations are responsible for providing a thread-safe list
	 * instance and if populated, invoking all listeners in the list at the appropriate time. Renderer must
	 * also clear the list instance if needed before calling.
	 */
	static void prepareInvocations(io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext context, List<io.vram.frex.api.world.RenderRegionBakeListener> listeners) {
		RenderRegionBakeListenerImpl.prepareInvocations(context, listeners);
	}

	@Environment(EnvType.CLIENT)
	public interface RenderRegionContext extends io.vram.frex.api.world.RenderRegionBakeListener.RenderRegionContext { }

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BlockStateRenderer extends io.vram.frex.api.world.RenderRegionBakeListener.BlockStateRenderer { }
}
