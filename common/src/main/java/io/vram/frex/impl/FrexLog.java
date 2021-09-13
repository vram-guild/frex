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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FrexLog {
	private FrexLog() { }

	private static Logger LOG = LogManager.getLogger("FREX CORE API");

	public static void warn(String string) {
		LOG.warn(string);
	}

	public static void info(String string) {
		LOG.info(string);
	}

	public static void warn(String message, Exception e) {
		LOG.warn(message, e);
	}
}
