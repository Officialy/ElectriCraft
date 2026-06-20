/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.entity.BlockEntity;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriCable;
import reika.electricraft.base.ElectriTERenderer;

public class RenderCable extends ElectriTERenderer<ElectriCable> {

    // 1.21.5: render -> submit; @Override dropped
    public void render(ElectriCable tile, float p_112308_, PoseStack stack, VertexConsumer p_112310_, int p_112311_, int p_112312_) {
        ElectriCable te = tile;
        if (tile.hasLevel()) {

        } else {
//			ReikaTextureHelper.bindTerrainTexture();
            this.renderBlock(te, stack/*par2, par4 - 0.3, par6, te.getEndIcon(), te.getCenterIcon()*/);
            this.renderBlock(te, stack/*par2, par4 + 0.1, par6, te.getCenterIcon(), te.getCenterIcon()*/);
            this.renderBlock(te, stack/*par2, par4 + 0.5, par6, te.getEndIcon(), te.getCenterIcon()*/);
        }
    }

    private void renderBlock(ElectriCable te, PoseStack stack) {
        // TODO: Port to 26.1 rendering API (Tesselator.getBuilder() + vertex().uv().endVertex() + end() all removed)
    }

    @Override
    protected String getModID() {
        return ElectriCraft.MODID;
    }
}
