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

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Matrix4f;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityWire;

public class RenderWire extends ElectriTERenderer<BlockEntityWire> {

	public RenderWire(BlockEntityRendererProvider.Context context) {
	}
	@Override
	public void render(BlockEntityWire te, float p_112308_, PoseStack stack, MultiBufferSource bufferSource, int light, int p_112312_) {
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
		float u = 1;//ico.getMinU();
		float v = 1;//ico.getMinV();
		float du = 1;//ico.getMaxU();
		float dv = 1;//ico.getMaxV();
//		stack.translate(par2, par4, par6);
		Tesselator tess = Tesselator.getInstance();
		BufferBuilder v5 = tess.getBuilder();

		float f = 0.5F;
		float s = 0.4F;
//		v5.color(f, f, f, 1);
		stack.pushPose();
		stack.translate(0.5f, 0, 0.5f);
		Matrix4f matrix = stack.last().pose();
		stack.popPose();


		stack.pushPose();
		stack.scale(s, s, s);
		stack.translate(0.5, 0.375, 0.5);

		v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		v5.normal(0, 1, 0);
		v5.vertex(matrix,0, 0, 1).uv(u, v).endVertex();
		v5.vertex(matrix,1, 0, 1).uv(du, v).endVertex();
		v5.vertex(matrix,1, 1, 1).uv(du, dv).endVertex();
		v5.vertex(matrix,0, 1, 1).uv(u, dv).endVertex();
		v5.end();

		v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		v5.normal(0, 1, 0);
		v5.vertex(matrix,0, 1, 0).uv(u, dv).endVertex();
		v5.vertex(matrix,1, 1, 0).uv(du, dv).endVertex();
		v5.vertex(matrix,1, 0, 0).uv(du, v).endVertex();
		v5.vertex(matrix,0, 0, 0).uv(u, v).endVertex();
		v5.end();

		f = 0.33F;
		v5.color(f, f, f, 1);
		v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		v5.normal(0, 1, 0);
		v5.vertex(matrix,1, 1, 0).uv( u, dv).endVertex();
		v5.vertex(matrix,1, 1, 1).uv(du, dv).endVertex();
		v5.vertex(matrix,1, 0, 1).uv(du, v).endVertex();
		v5.vertex(matrix,1, 0, 0).uv(u, v).endVertex();
		v5.end();

		v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		v5.normal(0, 1, 0);
		v5.vertex(matrix,0, 0, 0).uv(u, v).endVertex();
		v5.vertex(matrix,0, 0, 1).uv(du, v).endVertex();
		v5.vertex(matrix,0, 1, 1).uv(du, dv).endVertex();
		v5.vertex(matrix,0, 1, 0).uv(u, dv).endVertex();
		v5.end();

		f = 1F;
		v5.color(f, f, f, 1);
		v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		v5.normal(0, 1, 0);
		v5.vertex(matrix,0, 1, 1).uv(u, dv).endVertex();
		v5.vertex(matrix,1, 1, 1).uv(du, dv).endVertex();
		v5.vertex(matrix,1, 1, 0).uv(du, v).endVertex();
		v5.vertex(matrix,0, 1, 0).uv(u, v).endVertex();
		v5.end();

		v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		v5.normal(0, 1, 0);
		v5.vertex(matrix,0, 0, 0).uv(u, v).endVertex();
		v5.vertex(matrix,1, 0, 0).uv(du, v).endVertex();
		v5.vertex(matrix,1, 0, 1).uv(du, dv).endVertex();
		v5.vertex(matrix,0, 0, 1).uv(u, dv).endVertex();
		v5.end();
		stack.popPose();
//		v5.color(255, 255, 255);
	}

}
