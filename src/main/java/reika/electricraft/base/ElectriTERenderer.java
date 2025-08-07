/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.base.BlockEntityBase;
import reika.dragonapi.base.BlockEntityRenderBase;
import reika.electricraft.ElectriCraft;
public abstract class ElectriTERenderer<TE extends BlockEntity> extends BlockEntityRenderBase<TE> {

	@Override
	protected Class getModClass() {
		return ElectriCraft.class;
	}

/*	protected void setupGL(ElectriBlockEntity tile, double par2, double par4, double par6) {
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		stack.translate(par2, par4, par6);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		stack.translate(0, -2, -1);

		if (tile.isInWorld() && tile.isFlipped) {//&& MinecraftForgeClient.getRenderPass() == 0) {
			GL11.glRotated(180, 1, 0, 0);
			stack.translate(0, -2, 0);
		}
	}

	protected void closeGL(ElectriBlockEntity tile) {
		if (tile.isInWorld())
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}*/

	@Override
	protected boolean doRenderModel(PoseStack stack, BlockEntityBase te) {
		return this.isValidMachineRenderPass(te);
	}

	@Override
	protected final DragonAPIMod getOwnerMod() {
		return ElectriCraft.instance;
	}

	@Override
	protected String getModID() {
		return ElectriCraft.MODID;
	}
}
