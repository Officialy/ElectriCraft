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
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.opengl.GL11;

import reika.electricraft.base.ElectriCable;

public class CableRenderer implements BlockEntityRenderer {

    public CableRenderer(int ID) {
//        super(ID);
    }

//    @Override
    public boolean renderWorldBlock(BlockGetter world, BlockPos pos, Block block, int modelId) {
//        super.renderWorldBlock(world, pos, block, modelId, renderer);
        ElectriCable tile = (ElectriCable) world.getBlockEntity(pos);
        float size = 0.25F;
    /*    GL11.glColor4f(1, 1, 1, 1);
        for (int i = 0; i < 6; i++) {
            Direction dir = dirs[i];
            this.renderFace(tile, pos, dir, size);
        }*/
        //ReikaTextureHelper.bindTerrainTexture();
        return true;
    }

    protected void renderFace(PoseStack stack, BlockEntity te, int x, int y, int z, Direction dir, double size) {
        ElectriCable tile = (ElectriCable) te;
		var tess = Tesselator.getInstance();
        var v5 = tess.getBuilder();

        float u =  1;//todo uv
        float v =  1;//todo uv
        float du = 1;//todo uv
        float dv = 1;//todo uv

//        v5.setColorOpaque(255, 255, 255);
        stack.translate(x, y, z);
        if (tile.isInWorld() && tile.isConnectedOnSideAt(tile.getLevel(), tile.getBlockPos(), dir)) {
            double size2 = size;
            if (tile.isConnectedToHandlerOnSideAt(tile.getLevel(), tile.getBlockPos(), dir))
                ;//size2 *= 1.5;
            v5.normal(dir.getStepX(), dir.getStepY(), dir.getStepZ()); //todo normals
            //ReikaJavaLibrary.pConsole(dir, tile.isConnectedToHandlerOnSideAt(tile.level, tile.xCoord, tile.yCoord, tile.zCoord, dir));
			switch (dir) {
				case DOWN -> {
//					this.faceBrightness(Direction.SOUTH, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 + size, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 + size, 0.5 + size / 2).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size, 0.5 + size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size, 0.5 + size / 2.01).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size, 0.5 + size / 2.01).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size * 2, 0.5 + size / 2.01).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size * 2, 0.5 + size / 2.01).uv(u, dv);
//					this.faceBrightness(Direction.EAST, v5);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 + size, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 + size, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 + size / 2.01, 0.5 + size / 2 + size * 2, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 + size / 2.01, 0.5 + size / 2 + size * 2, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2.01, 0.5 + size / 2 + size, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2.01, 0.5 + size / 2 + size, 0.5 - size / 2).uv(du, v);
//					this.faceBrightness(Direction.WEST, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 + size, 0.5 - size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 + size, 0.5 + size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size, 0.5 + size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size, 0.5 - size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2.01, 0.5 + size / 2 + size, 0.5 - size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2.01, 0.5 + size / 2 + size, 0.5 + size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2.01, 0.5 + size / 2 + size * 2, 0.5 + size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2.01, 0.5 + size / 2 + size * 2, 0.5 - size / 2).uv(u, dv);
//					this.faceBrightness(Direction.NORTH, v5);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size, 0.5 - size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 + size, 0.5 - size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 + size, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size * 2, 0.5 - size / 2.01).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size * 2, 0.5 - size / 2.01).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 + size, 0.5 - size / 2.01).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 + size, 0.5 - size / 2.01).uv(du, v);
				}
				case EAST -> {
//					this.faceBrightness(Direction.DOWN, v5);
					v5.vertex(0.5 - size / 2 + size, 0.5 + size2 / 2, 0.5 + size2 / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2, 0.5 + size2 / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2, 0.5 - size2 / 2).uv(du, v);
					v5.vertex(0.5 - size / 2 + size, 0.5 + size2 / 2, 0.5 - size2 / 2).uv(u, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2.01, 0.5 + size2 / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 + size2 / 2.01, 0.5 + size2 / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 + size2 / 2.01, 0.5 - size2 / 2).uv(du, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2.01, 0.5 - size2 / 2).uv(u, v);
//					this.faceBrightness(Direction.SOUTH, v5);
					v5.vertex(0.5 - size / 2 + size, 0.5 - size2 / 2, 0.5 + size2 / 2).uv(du, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2, 0.5 + size2 / 2).uv(u, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2, 0.5 + size2 / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2 + size, 0.5 + size2 / 2, 0.5 + size2 / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2, 0.5 + size2 / 2.01).uv(du, v);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 - size2 / 2, 0.5 + size2 / 2.01).uv(u, v);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 + size2 / 2, 0.5 + size2 / 2.01).uv(u, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2, 0.5 + size2 / 2.01).uv(du, dv);
//					this.faceBrightness(Direction.UP, v5);
					v5.vertex(0.5 - size / 2 + size, 0.5 - size2 / 2, 0.5 - size2 / 2).uv(du, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2, 0.5 - size2 / 2).uv(u, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2, 0.5 + size2 / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2 + size, 0.5 - size2 / 2, 0.5 + size2 / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2.01, 0.5 - size2 / 2).uv(du, v);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 - size2 / 2.01, 0.5 - size2 / 2).uv(u, v);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 - size2 / 2.01, 0.5 + size2 / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2.01, 0.5 + size2 / 2).uv(du, dv);
//					this.faceBrightness(Direction.NORTH, v5);
					v5.vertex(0.5 - size / 2 + size, 0.5 + size2 / 2, 0.5 - size2 / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2, 0.5 - size2 / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2, 0.5 - size2 / 2).uv(du, v);
					v5.vertex(0.5 - size / 2 + size, 0.5 - size2 / 2, 0.5 - size2 / 2).uv(u, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 + size2 / 2, 0.5 - size2 / 2.01).uv(u, dv);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 + size2 / 2, 0.5 - size2 / 2.01).uv(du, dv);
					v5.vertex(0.5 + size / 2 + size * 2, 0.5 - size2 / 2, 0.5 - size2 / 2.01).uv(du, v);
					v5.vertex(0.5 + size / 2 + size, 0.5 - size2 / 2, 0.5 - size2 / 2.01).uv(u, v);
				}
				case NORTH -> {
//					this.faceBrightness(Direction.DOWN, v5);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 + size / 2 + size).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 + size / 2 + size).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 - size / 2 + size).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 - size / 2 + size).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2.01, 0.5 + size / 2 + size * 2).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2.01, 0.5 + size / 2 + size * 2).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2.01, 0.5 + size / 2 + size).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2.01, 0.5 + size / 2 + size).uv(du, v);
//					this.faceBrightness(Direction.EAST, v5);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 - size / 2 + size).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 + size / 2 + size).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 + size / 2 + size).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 - size / 2 + size).uv(u, v);
					v5.vertex(0.5 + size / 2.01, 0.5 + size / 2, 0.5 + size / 2 + size).uv(u, dv);
					v5.vertex(0.5 + size / 2.01, 0.5 + size / 2, 0.5 + size / 2 + size * 2).uv(du, dv);
					v5.vertex(0.5 + size / 2.01, 0.5 - size / 2, 0.5 + size / 2 + size * 2).uv(du, v);
					v5.vertex(0.5 + size / 2.01, 0.5 - size / 2, 0.5 + size / 2 + size).uv(u, v);
//					this.faceBrightness(Direction.WEST, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 - size / 2 + size).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 + size / 2 + size).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 + size / 2 + size).uv(u, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 - size / 2 + size).uv(du, dv);
					v5.vertex(0.5 - size / 2.01, 0.5 - size / 2, 0.5 + size / 2 + size).uv(du, v);
					v5.vertex(0.5 - size / 2.01, 0.5 - size / 2, 0.5 + size / 2 + size * 2).uv(u, v);
					v5.vertex(0.5 - size / 2.01, 0.5 + size / 2, 0.5 + size / 2 + size * 2).uv(u, dv);
					v5.vertex(0.5 - size / 2.01, 0.5 + size / 2, 0.5 + size / 2 + size).uv(du, dv);
//					this.faceBrightness(Direction.UP, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 - size / 2 + size).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 - size / 2 + size).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 + size / 2 + size).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 + size / 2 + size).uv(u, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2.01, 0.5 + size / 2 + size).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2.01, 0.5 + size / 2 + size).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2.01, 0.5 + size / 2 + size * 2).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2.01, 0.5 + size / 2 + size * 2).uv(u, dv);
				}
				case SOUTH -> {
//					this.faceBrightness(Direction.DOWN, v5);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 + size / 2 - size).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 + size / 2 - size).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 - size / 2 - size).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 - size / 2 - size).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 1.99, 0.5 - size / 2 - size).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 1.99, 0.5 - size / 2 - size).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 1.99, 0.5 - size / 2 - size * 2).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 1.99, 0.5 - size / 2 - size * 2).uv(du, v);
//					this.faceBrightness(Direction.EAST, v5);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 - size / 2 - size).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 + size / 2 - size).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 + size / 2 - size).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 - size / 2 - size).uv(u, v);
					v5.vertex(0.5 + size / 1.99, 0.5 + size / 2, 0.5 - size / 2 - size * 2).uv(u, dv);
					v5.vertex(0.5 + size / 1.99, 0.5 + size / 2, 0.5 - size / 2 - size).uv(du, dv);
					v5.vertex(0.5 + size / 1.99, 0.5 - size / 2, 0.5 - size / 2 - size).uv(du, v);
					v5.vertex(0.5 + size / 1.99, 0.5 - size / 2, 0.5 - size / 2 - size * 2).uv(u, v);
//					this.faceBrightness(Direction.WEST, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 - size / 2 - size).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 + size / 2 - size).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 + size / 2 - size).uv(u, dv);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 - size / 2 - size).uv(du, dv);
					v5.vertex(0.5 - size / 1.99, 0.5 - size / 2, 0.5 - size / 2 - size * 2).uv(du, v);
					v5.vertex(0.5 - size / 1.99, 0.5 - size / 2, 0.5 - size / 2 - size).uv(u, v);
					v5.vertex(0.5 - size / 1.99, 0.5 + size / 2, 0.5 - size / 2 - size).uv(u, dv);
					v5.vertex(0.5 - size / 1.99, 0.5 + size / 2, 0.5 - size / 2 - size * 2).uv(du, dv);
//					this.faceBrightness(Direction.UP, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 - size / 2 - size).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 - size / 2 - size).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 + size / 2 - size).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 + size / 2 - size).uv(u, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 1.99, 0.5 - size / 2 - size * 2).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 1.99, 0.5 - size / 2 - size * 2).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 1.99, 0.5 - size / 2 - size).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 1.99, 0.5 - size / 2 - size).uv(u, dv);
				}
				case UP -> {
//					this.faceBrightness(Direction.SOUTH, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size, 0.5 + size / 2).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 - size, 0.5 + size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 - size, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size * 2, 0.5 + size / 1.99).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size * 2, 0.5 + size / 1.99).uv(du, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size, 0.5 + size / 1.99).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size, 0.5 + size / 1.99).uv(u, dv);
//					this.faceBrightness(Direction.EAST, v5);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 - size, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 - size, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 + size / 1.99, 0.5 - size / 2 - size, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 + size / 1.99, 0.5 - size / 2 - size, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 1.99, 0.5 - size / 2 - size * 2, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 1.99, 0.5 - size / 2 - size * 2, 0.5 - size / 2).uv(du, v);
//					this.faceBrightness(Direction.WEST, v5);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size, 0.5 - size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size, 0.5 + size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 - size, 0.5 + size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 - size, 0.5 - size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 1.99, 0.5 - size / 2 - size * 2, 0.5 - size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 1.99, 0.5 - size / 2 - size * 2, 0.5 + size / 2).uv(du, v);
					v5.vertex(0.5 - size / 1.99, 0.5 - size / 2 - size, 0.5 + size / 2).uv(u, v);
					v5.vertex(0.5 - size / 1.99, 0.5 - size / 2 - size, 0.5 - size / 2).uv(u, dv);
//					this.faceBrightness(Direction.NORTH, v5);
					v5.vertex(0.5 - size / 2, 0.5 + size / 2 - size, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 + size / 2 - size, 0.5 - size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size, 0.5 - size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size, 0.5 - size / 1.99).uv(u, v);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size, 0.5 - size / 1.99).uv(u, dv);
					v5.vertex(0.5 + size / 2, 0.5 - size / 2 - size * 2, 0.5 - size / 1.99).uv(du, dv);
					v5.vertex(0.5 - size / 2, 0.5 - size / 2 - size * 2, 0.5 - size / 1.99).uv(du, v);
				}
				case WEST -> {
//					this.faceBrightness(Direction.DOWN, v5);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 2, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2 - size, 0.5 + size / 2, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2 - size, 0.5 + size / 2, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 2, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 + size / 1.99, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 1.99, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 1.99, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 + size / 1.99, 0.5 - size / 2).uv(u, v);
//					this.faceBrightness(Direction.SOUTH, v5);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 2, 0.5 + size / 2).uv(du, v);
					v5.vertex(0.5 + size / 2 - size, 0.5 - size / 2, 0.5 + size / 2).uv(u, v);
					v5.vertex(0.5 + size / 2 - size, 0.5 + size / 2, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 2, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 - size / 2, 0.5 + size / 1.99).uv(du, v);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 2, 0.5 + size / 1.99).uv(u, v);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 2, 0.5 + size / 1.99).uv(u, dv);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 + size / 2, 0.5 + size / 1.99).uv(du, dv);
//					this.faceBrightness(Direction.UP, v5);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 2, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 + size / 2 - size, 0.5 - size / 2, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 + size / 2 - size, 0.5 - size / 2, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 2, 0.5 + size / 2).uv(du, dv);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 - size / 1.99, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 1.99, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 1.99, 0.5 + size / 2).uv(u, dv);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 - size / 1.99, 0.5 + size / 2).uv(du, dv);
//					this.faceBrightness(Direction.NORTH, v5);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 2, 0.5 - size / 2).uv(u, dv);
					v5.vertex(0.5 + size / 2 - size, 0.5 + size / 2, 0.5 - size / 2).uv(du, dv);
					v5.vertex(0.5 + size / 2 - size, 0.5 - size / 2, 0.5 - size / 2).uv(du, v);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 2, 0.5 - size / 2).uv(u, v);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 + size / 2, 0.5 - size / 1.99).uv(u, dv);
					v5.vertex(0.5 - size / 2 - size, 0.5 + size / 2, 0.5 - size / 1.99).uv(du, dv);
					v5.vertex(0.5 - size / 2 - size, 0.5 - size / 2, 0.5 - size / 1.99).uv(du, v);
					v5.vertex(0.5 - size / 2 - size * 2, 0.5 - size / 2, 0.5 - size / 1.99).uv(u, v);
				}
				default -> {
				}
			}
        } else {
//            this.faceBrightness(dir, v5);
            v5.normal(dir.getStepX(), dir.getStepY(), dir.getStepZ());
            switch (dir) {
                case DOWN -> {
                    v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 + size / 2).uv(u, dv);
                    v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 + size / 2).uv(du, dv);
                    v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 - size / 2).uv(du, v);
                    v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 - size / 2).uv(u, v);
                }
                case NORTH -> {
                    v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 + size / 2).uv(du, v);
                    v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 + size / 2).uv(u, v);
                    v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 + size / 2).uv(u, dv);
                    v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 + size / 2).uv(du, dv);
                }
                case EAST -> {
                    v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 - size / 2).uv(u, dv);
                    v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 + size / 2).uv(du, dv);
                    v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 + size / 2).uv(du, v);
                    v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 - size / 2).uv(u, v);
                }
                case WEST -> {
                    v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 - size / 2).uv(du, v);
                    v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 + size / 2).uv(u, v);
                    v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 + size / 2).uv(u, dv);
                    v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 - size / 2).uv(du, dv);
                }
                case UP -> {
                    v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 - size / 2).uv(du, v);
                    v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 - size / 2).uv(u, v);
                    v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 + size / 2).uv(u, dv);
                    v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 + size / 2).uv(du, dv);
                }
                case SOUTH -> {
                    v5.vertex(0.5 - size / 2, 0.5 + size / 2, 0.5 - size / 2).uv(u, dv);
                    v5.vertex(0.5 + size / 2, 0.5 + size / 2, 0.5 - size / 2).uv(du, dv);
                    v5.vertex(0.5 + size / 2, 0.5 - size / 2, 0.5 - size / 2).uv(du, v);
                    v5.vertex(0.5 - size / 2, 0.5 - size / 2, 0.5 - size / 2).uv(u, v);
                }
                default -> {
                }
            }
        }
        stack.translate(-x, -y, -z);
        stack.popPose();
    }

	@Override
	public void render(BlockEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {

	}
}
