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

package grondag.frex;

/**
 * Use to make FREX an optional dependency. To do so, implement this interface
 * in a stand-alone class and declare a "frex" end point in the mod's
 * fabric.mod.json that points to the implementation.  For example:
 *
 * <p><pre>
 * "entrypoints": {
 *    "frex": [ "yourorg.yourmod.yourpackage.XmFrexInitializer" ]
 * }</pre>
 *
 * <p>Every mod that implements this interface and declares and end point will receive
 * exactly one call to {@link #onInitalizeFrex()}.
 *
 * <p>To maintain an optional dependency, all calls to FREX methods must be isolated to
 * the FrexInitializer instance or to classes that are only loaded if {@link #onInitalizeFrex()}
 * is called.
 *
 * <p>Note that it is NOT necessary to implement this interface and register a
 * "frex" end point for mods that nest the FREX library or have a hard dependency on FREX.
 * Such mods can safely handle FREX registration in their client initialize instance.
 */
@Deprecated
public interface FrexInitializer {
	/**
	 * Signals mods that maintain an optional dependency on FREX that FREX is
	 * loaded. Such mods should handle initialization activities that reference
	 * FREX classes during this call.
	 *
	 * <p>Will be called during client mod initialization, possibly before the requesting
	 * mod initialization is complete. It will be called exactly once per game start.
	 */
	void onInitalizeFrex();
}
