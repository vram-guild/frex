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

package io.vram.frex.api.model;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Contract;

import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;

import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.texture.SpriteFinder;

/**
 * Collection of utilities for model implementations.
 */
// WIP: split up
public abstract class ModelHelper {
	public static final int NORTH_INDEX = toFaceIndex(Direction.NORTH);
	public static final int SOUTH_INDEX = toFaceIndex(Direction.SOUTH);
	public static final int EAST_INDEX = toFaceIndex(Direction.EAST);
	public static final int WEST_INDEX = toFaceIndex(Direction.WEST);
	public static final int UP_INDEX = toFaceIndex(Direction.UP);
	public static final int DOWN_INDEX = toFaceIndex(Direction.DOWN);
	/** Result from {@link #toFaceIndex(Direction)} for null values. */
	public static final int UNASSIGNED_INDEX = 6;

	public static final int NORTH_FLAG = 1 << NORTH_INDEX;
	public static final int SOUTH_FLAG = 1 << SOUTH_INDEX;
	public static final int EAST_FLAG = 1 << EAST_INDEX;
	public static final int WEST_FLAG = 1 << WEST_INDEX;
	public static final int UP_FLAG = 1 << UP_INDEX;
	public static final int DOWN_FLAG = 1 << DOWN_INDEX;
	public static final int UNASSIGNED_FLAG = 1 << UNASSIGNED_INDEX;

	public static final int ALL_REAL_FACE_FLAGS = NORTH_FLAG | SOUTH_FLAG | EAST_FLAG | WEST_FLAG | UP_FLAG | DOWN_FLAG;

	private ModelHelper() { }

	/**
	 * Convenient way to encode faces that may be null.
	 * Null is returned as {@link #UNASSIGNED_INDEX}.
	 * Use {@link #faceFromIndex(int)} to retrieve encoded face.
	 */
	public static int toFaceIndex(Direction face) {
		return face == null ? UNASSIGNED_INDEX : face.get3DDataValue();
	}

	/**
	 * Use to decode a result from {@link #toFaceIndex(Direction)}.
	 * Return value will be null if encoded value was null.
	 * Can also be used for no-allocation iteration of {@link Direction#values()},
	 * optionally including the null face. (Use &lt; or  &lt;= {@link #UNASSIGNED_INDEX}
	 * to exclude or include the null value, respectively.)
	 */
	@Contract("null -> null")
	public static Direction faceFromIndex(int faceIndex) {
		return FACES[faceIndex];
	}

	/** @see #faceFromIndex(int) */
	private static final Direction[] FACES = Arrays.copyOf(Direction.values(), 7);

	/**
	 * Converts a mesh into an array of lists of vanilla baked quads.
	 * Useful for creating vanilla baked models when required for compatibility.
	 * The array indexes correspond to {@link Direction#get3DDataValue()} with the
	 * addition of {@link #UNASSIGNED_INDEX}.
	 *
	 * <p>Retrieves sprites from the block texture atlas via {@link SpriteFinder}.
	 */
	public static List<BakedQuad>[] toQuadLists(Mesh mesh) {
		final SpriteFinder finder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));

		@SuppressWarnings("unchecked")
		final ImmutableList.Builder<BakedQuad>[] builders = new ImmutableList.Builder[7];

		for (int i = 0; i < 7; i++) {
			builders[i] = ImmutableList.builder();
		}

		if (mesh != null) {
			mesh.forEach(q -> {
				final Direction face = q.cullFace();
				builders[face == null ? 6 : face.get3DDataValue()].add(q.toBakedQuad(finder.find(q)));
			});
		}

		@SuppressWarnings("unchecked")
		final
		List<BakedQuad>[] result = new List[7];

		for (int i = 0; i < 7; i++) {
			result[i] = builders[i].build();
		}

		return result;
	}

	/**
	 * The vanilla model transformation logic is closely coupled with model deserialization.
	 * That does little good for modded model loaders and procedurally generated models.
	 * This convenient construction method applies the same scaling factors used for vanilla models.
	 * This means you can use values from a vanilla JSON file as inputs to this method.
	 */
	private static ItemTransform makeTransform(float rotationX, float rotationY, float rotationZ, float translationX, float translationY, float translationZ, float scaleX, float scaleY, float scaleZ) {
		final Vector3f translation = new Vector3f(translationX, translationY, translationZ);
		translation.mul(0.0625f);
		translation.clamp(-5.0F, 5.0F);
		return new ItemTransform(new Vector3f(rotationX, rotationY, rotationZ), translation, new Vector3f(scaleX, scaleY, scaleZ));
	}

	public static final ItemTransform TRANSFORM_BLOCK_GUI = makeTransform(30, 225, 0, 0, 0, 0, 0.625f, 0.625f, 0.625f);
	public static final ItemTransform TRANSFORM_BLOCK_GROUND = makeTransform(0, 0, 0, 0, 3, 0, 0.25f, 0.25f, 0.25f);
	public static final ItemTransform TRANSFORM_BLOCK_FIXED = makeTransform(0, 0, 0, 0, 0, 0, 0.5f, 0.5f, 0.5f);
	public static final ItemTransform TRANSFORM_BLOCK_3RD_PERSON_RIGHT = makeTransform(75, 45, 0, 0, 2.5f, 0, 0.375f, 0.375f, 0.375f);
	public static final ItemTransform TRANSFORM_BLOCK_1ST_PERSON_RIGHT = makeTransform(0, 45, 0, 0, 0, 0, 0.4f, 0.4f, 0.4f);
	public static final ItemTransform TRANSFORM_BLOCK_1ST_PERSON_LEFT = makeTransform(0, 225, 0, 0, 0, 0, 0.4f, 0.4f, 0.4f);

	/**
	 * Mimics the vanilla model transformation used for most vanilla blocks,
	 * and should be suitable for most custom block-like models.
	 */
	public static final ItemTransforms MODEL_TRANSFORM_BLOCK = new ItemTransforms(TRANSFORM_BLOCK_3RD_PERSON_RIGHT, TRANSFORM_BLOCK_3RD_PERSON_RIGHT, TRANSFORM_BLOCK_1ST_PERSON_LEFT, TRANSFORM_BLOCK_1ST_PERSON_RIGHT, ItemTransform.NO_TRANSFORM, TRANSFORM_BLOCK_GUI, TRANSFORM_BLOCK_GROUND, TRANSFORM_BLOCK_FIXED);
}
