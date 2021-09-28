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

package io.vram.frex.impl.config;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import io.vram.frex.impl.FrexLog;

public class FlawlessFramesImpl {
	private static class Controller implements Consumer<Boolean> {
		final String owner;
		boolean isActive = false;

		private Controller(String owner) {
			this.owner = owner;
		}

		@Override
		public String toString() {
			return owner;
		}

		@Override
		public void accept(Boolean isActive) {
			if (this.isActive != isActive) {
				synchronized (ACTIVE) {
					if (this.isActive) {
						ACTIVE.remove(this);
						if (enableTrace) FrexLog.info("Deactivating Flawless Frames at request of " + owner);
					} else {
						ACTIVE.add(this);
						if (enableTrace) FrexLog.info("Activating Flawless Frames at request of " + owner);
					}

					this.isActive = isActive;

					if (enableTrace) {
						FrexLog.info("Flawless Frames current status is " + isActive());
						if (isActive()) FrexLog.info("Current active controllers are: " + ACTIVE.toString());
					}
				}
			}
		}
	}

	public static Function<String, Consumer<Boolean>> providerFactory() {
		return Controller::new;
	}

	private static final Set<Controller> ACTIVE = Collections.newSetFromMap(new IdentityHashMap<Controller, Boolean>());
	private static boolean enableTrace = false;

	public static boolean isActive() {
		return !ACTIVE.isEmpty();
	}

	public static void enableTrace(boolean enable) {
		enableTrace = enable;
	}
}
