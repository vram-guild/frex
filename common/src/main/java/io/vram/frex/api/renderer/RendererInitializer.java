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

package io.vram.frex.api.renderer;

import io.vram.frex.impl.RendererInitializerImpl;

/**
 * How this gets called is platform-specific.
 */
public interface RendererInitializer {
	static void register(String className, String methodName, ClassLoader loader, boolean isFallback) {
		RendererInitializerImpl.register(className, methodName, loader, isFallback);
	}
}
