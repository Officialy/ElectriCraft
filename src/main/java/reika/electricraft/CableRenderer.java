/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

// 1.21.5: BlockEntityRenderer is now BlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState>
// with a submit(...) entry point; this legacy class predates BlockEntityRenderer's typed signature so it's
// reduced to a plain helper until the renderer is rebuilt against the SubmitNodeCollector pipeline.
public class CableRenderer {

    public CableRenderer(int ID) {
    }

    public boolean renderWorldBlock(BlockGetter world, BlockPos pos, Block block, int modelId) {
        return true;
    }

    protected void renderFace(PoseStack stack, BlockEntity te, int x, int y, int z, Direction dir, double size) {
        // TODO 1.21.5: rewrite against MeshData / BufferBuilder.
    }
}
