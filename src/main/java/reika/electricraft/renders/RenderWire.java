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
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityWire;

public class RenderWire extends ElectriTERenderer<BlockEntityWire> {

	public RenderWire(BlockEntityRendererProvider.Context context) {
	}
	// 1.21.5: render -> submit; @Override dropped
	public void render(BlockEntityWire te, float p_112308_, PoseStack stack, VertexConsumer bufferSource, int light, int p_112312_) {
		if (te.hasLevel()) {

		}
		else {
//			ReikaTextureHelper.bindTerrainTexture();
			this.renderBlock(te, stack);//, /*par2, par4-0.3, par6,*/ te.insulated ? te.getInsulatedEndIcon() : te.getEndIcon());
			this.renderBlock(te, stack);//, /*par2, par4+0.1, par6,*/ te.insulated ? te.getInsulatedCenterIcon() : te.getCenterIcon());
			this.renderBlock(te, stack);//, /*par2, par4+0.5, par6,*/ te.insulated ? te.getInsulatedEndIcon() : te.getEndIcon());
		}
	}

	private void renderBlock(BlockEntityWire te, PoseStack stack) { //todo TEXTURE REQUIRED
		// TODO: Port to 26.1 rendering API (Tesselator.getBuilder() + vertex().uv().endVertex() + end() all removed)
	}

}
