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
