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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import reika.dragonapi.base.BlockEntityBase;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.rendering.ReikaColorAPI;
import reika.dragonapi.libraries.rendering.ReikaRenderHelper;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityBattery;

public class RenderModBattery extends ElectriTERenderer<BlockEntityBattery> {

    public RenderModBattery(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(BlockEntityBattery tile, float p_112308_, PoseStack stack, MultiBufferSource bufferSource, int light, int p_112312_) {
		BatteryTile te = tile;
		//if (te.isInWorld()) {
		stack.pushPose();
		Tesselator tess = Tesselator.getInstance();
        BufferBuilder v5 = tess.getBuilder();
		if (((BlockEntityBase)te).isInWorld())
			ReikaRenderHelper.prepareGeoDraw(true);
		else {
			RenderSystem.disableTexture();
//			GL11.glDisable(GL11.GL_LIGHTING);
		}

		double o = 0.0025;
		double w = 0.1875;
		double h = 0.0625;

		//double[] l = ReikaMathLibrary.splitNumberByDigits(te.getStoredEnergy(), 10);
		//for (int k = 0; k < l.length; k++) {
		int v = 4;
		int n = 16*v;
		int c = (int)(64*ReikaMathLibrary.logbase(te.getStoredEnergy(), 10)/ReikaMathLibrary.logbase(te.getMaxEnergy(), 10));//(int)Math.min(n, 0.5+(double)te.getStoredEnergy()*n/te.CAPACITY);//(int)(n*l[k]);//;
		double d = (1-h*2)/n;
		//double r = 0.0625/3;
		int c1 = te.getEnergyColor();
		int c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(c1, 0.05F);
		for (int i = 0; i < c; i++) {
			v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.color(color);
			v5.vertex(0.5-w, h+d*(i+1), 0-o).endVertex();
			v5.vertex(0.5+w, h+d*(i+1), 0-o).endVertex();
			v5.vertex(0.5+w, h+d*i, 0-o).endVertex();
			v5.vertex(0.5-w, h+d*i, 0-o).endVertex();
            tess.end();
		}

		for (int i = 0; i < c; i++) {
			v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.color(color);
			v5.vertex(0.5-w, h+d*i, 1+o).endVertex();
			v5.vertex(0.5+w, h+d*i, 1+o).endVertex();
			v5.vertex(0.5+w, h+d*(i+1), 1+o).endVertex();
			v5.vertex(0.5-w, h+d*(i+1), 1+o).endVertex();
            tess.end();
		}

		for (int i = 0; i < c; i++) {
			v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.color(color);
			v5.vertex(1+o, h+d*(i+1), 0.5-w).endVertex();
			v5.vertex(1+o, h+d*(i+1), 0.5+w).endVertex();
			v5.vertex(1+o, h+d*i, 0.5+w).endVertex();
			v5.vertex(1+o, h+d*i, 0.5-w).endVertex();
            tess.end();
		}

		for (int i = 0; i < c; i++) {
            v5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
			int color = ReikaColorAPI.mixColors(c1, c2, (float)i/n);
			v5.color(color);
			v5.vertex(0-o, h+d*i, 0.5-w).endVertex();
			v5.vertex(0-o, h+d*i, 0.5+w).endVertex();
			v5.vertex(0-o, h+d*(i+1), 0.5+w).endVertex();
			v5.vertex(0-o, h+d*(i+1), 0.5-w).endVertex();
			tess.end();
		}

		//	}

		/*
			ReikaRenderHelper.disableEntityLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPushMatrix();
			double s = 0.125;
			double da = 0;
			double db = 5;
			double dc = 0;
			stack.translate(da, db, dc);
			GL11.glScaled(s, -s, s);
			this.getFontRenderer().drawString(String.format("%.5f", ReikaMathLibrary.logbase(te.getStoredEnergy(), 10)), 0, 0, 0xffffff);
			GL11.glPopMatrix();

			ReikaRenderHelper.enableEntityLighting();
			GL11.glEnable(GL11.GL_LIGHTING);*/

		stack.popPose();
		//}
	}

}
