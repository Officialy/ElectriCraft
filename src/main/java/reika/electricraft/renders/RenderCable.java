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
import org.lwjgl.opengl.GL11;


import net.minecraft.world.level.block.entity.BlockEntity;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriCable;
import reika.electricraft.base.ElectriTERenderer;

public class RenderCable extends ElectriTERenderer<ElectriCable> {

    @Override
    public void render(ElectriCable tile, float p_112308_, PoseStack stack, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
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
		float u = 1;//ico.getMinU();
		float v = 1;//ico.getMinV();
		float du = 1;//ico.getMaxU();
		float dv = 1;//ico.getMaxV();

		float u2 = 1;//ico2.getMinU();
		float v2 = 1;//ico2.getMinV();
		float du2 = 1;//ico2.getMaxU();
		float dv2 = 1;//ico2.getMaxV();
        stack.pushPose();
//        stack.translate(par2, par4, par6);
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder v5 = tess.getBuilder();

        float f = 0.5F;
        float s = 0.4F;
        v5.color(f, f, f, 1);
        stack.scale(s, s, s);
        stack.translate(0.5, 0.375, 0.5);
        v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        v5.normal(0, 1, 0);
        v5.vertex(0, 0, 1).uv(u, v).endVertex();
        v5.vertex(1, 0, 1).uv(du, v).endVertex();
        v5.vertex(1, 1, 1).uv(du, dv).endVertex();
        v5.vertex(0, 1, 1).uv(u, dv).endVertex();
        tess.end();

        v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        v5.normal(0, 1, 0);
        v5.vertex(0, 1, 0).uv(u, dv).endVertex();
        v5.vertex(1, 1, 0).uv(du, dv).endVertex();
        v5.vertex(1, 0, 0).uv(du, v).endVertex();
        v5.vertex(0, 0, 0).uv(u, v).endVertex();
        tess.end();

        f = 0.33F;
        v5.color(f, f, f, 1);
        v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        v5.normal(0, 1, 0);
        v5.vertex(1, 1, 0).uv(u, dv).endVertex();
        v5.vertex(1, 1, 1).uv(du, dv).endVertex();
        v5.vertex(1, 0, 1).uv(du, v).endVertex();
        v5.vertex(1, 0, 0).uv(u, v).endVertex();
        tess.end();

        v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        v5.normal(0, 1, 0);
        v5.vertex(0, 0, 0).uv(u, v).endVertex();
        v5.vertex(0, 0, 1).uv(du, v).endVertex();
        v5.vertex(0, 1, 1).uv(du, dv).endVertex();
        v5.vertex(0, 1, 0).uv(u, dv).endVertex();
        tess.end();

        f = 1F;
        v5.color(f, f, f, 1);
        v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        v5.normal(0, 1, 0);
        v5.vertex(0, 1, 1).uv(u2, dv2).endVertex();
        v5.vertex(1, 1, 1).uv(du2, dv2).endVertex();
        v5.vertex(1, 1, 0).uv(du2, v2).endVertex();
        v5.vertex(0, 1, 0).uv(u2, v2).endVertex();
        tess.end();

        v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        v5.normal(0, 1, 0);
        v5.vertex(0, 0, 0).uv(u2, v2).endVertex();
        v5.vertex(1, 0, 0).uv(du2, v2).endVertex();
        v5.vertex(1, 0, 1).uv(du2, dv2).endVertex();
        v5.vertex(0, 0, 1).uv(u2, dv2).endVertex();
        tess.end();
        stack.popPose();
        v5.color(255, 255, 255, 255);
    }

    @Override
    protected String getModID() {
        return ElectriCraft.MODID;
    }
}
