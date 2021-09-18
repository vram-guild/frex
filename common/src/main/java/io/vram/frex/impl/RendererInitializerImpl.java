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

package io.vram.frex.impl;

import java.lang.reflect.Method;

public class RendererInitializerImpl {
	private static String bestCandidate = null;
	private static String methodName;
	private static boolean isFallback;
	private static ClassLoader loader;

	public static Object run() {
		if (bestCandidate != null) {
			try {
				final Class<?> c = Class.forName(bestCandidate, true, loader);
				final Method m = c.getMethod(methodName);
				return m.invoke(c);
			} catch (final Exception e) {
				throw new RuntimeException("Unable to obtain renderer instance.", e);
			}
		} else {
			return null;
		}
	}

	public static void register(String className, String methodNameIn, ClassLoader loaderIn, boolean isFallbackIn) {
		if (bestCandidate == null || isFallback) {
			bestCandidate = className;
			methodName = methodNameIn;
			loader = loaderIn;
			isFallback = isFallbackIn;
		}
	}

	// WIP: remove, use wrapping/Blu as default
	public static boolean hasCandidate() {
		return bestCandidate != null;
	}
}
