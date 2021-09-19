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

package io.vram.frex.api.model;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import io.vram.frex.api.mesh.FrexVertexConsumerProvider;
import io.vram.frex.api.mesh.Mesh;
import io.vram.frex.api.mesh.QuadEditor;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

public interface ModelRenderContext {
	void accept(Mesh mesh);

	void accept(BakedModel model, @Nullable BlockState blockState);

	QuadEditor quadEmitter();

	FrexVertexConsumerProvider vertexConsumers();

	void pushTransform(QuadTransform transform);

	void popTransform();

	PoseStack matrixStack();

	Random random();
}
