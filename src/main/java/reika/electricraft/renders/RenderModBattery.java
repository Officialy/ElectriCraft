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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityBattery;

public class RenderModBattery extends ElectriTERenderer<BlockEntityBattery> {

    public RenderModBattery(BlockEntityRendererProvider.Context context) {

    }

    // 1.21.5: render -> submit; @Override dropped
    public void render(BlockEntityBattery tile, float p_112308_, PoseStack stack, VertexConsumer bufferSource, int light, int p_112312_) {
        // TODO: Port to 26.1 rendering API (Tesselator.getBuilder() + begin() + vertex().endVertex() + end() all removed)
    }

}
