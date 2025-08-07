///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft;
//
//import com.mojang.blaze3d.vertex.Tesselator;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import org.lwjgl.opengl.GL11;
//
//import reika.electricraft.tileentities.BlockEntityWire;
//
//public class WireRenderer extends WorldPipingRenderer {
//
//	public WireRenderer(int ID) {
//		super(ID);
//	}
//
//	@Override
//	public boolean renderWorldBlock(BlockGetter world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
//		super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
//		BlockEntityWire tile = (BlockEntityWire)world.getBlockEntity(new BlockPos(x, y, z));
//		float size = tile.insulated ? 0.333333F : 0.25F;
//		GL11.glColor4f(1, 1, 1, 1);
//		for (int i = 0; i < 6; i++) {
//			Direction dir = dirs[i];
//			this.renderFace(tile, x, y, z, dir, size);
//		}
//		//ReikaTextureHelper.bindTerrainTexture();
//		return true;
//	}
//
//	@Override
//	protected void renderFace(BlockEntity te, int x, int y, int z, Direction dir, double size) {
//		BlockEntityWire tile = (BlockEntityWire)te;
//		Tesselator tess = Tesselator.getInstance();
//			BufferBuilder v5 = tess.getBuilder();
//		IIcon ico = tile.insulated ? tile.getInsulatedCenterIcon() : tile.getCenterIcon();
//		float u = ico.getMinU();
//		float v = ico.getMinV();
//		float du = ico.getMaxU();
//		float dv = ico.getMaxV();
//
//		v5.setColorOpaque(255, 255, 255);
//		v5.addTranslation(x, y, z);
//		if (tile.isInWorld() && tile.isConnectedOnSideAt(tile.level, tile.xCoord, tile.yCoord, tile.zCoord, dir)) {
//			ico = tile.insulated ? tile.getInsulatedEndIcon() : tile.getEndIcon();
//			u = ico.getMinU();
//			v = ico.getMinV();
//			du = ico.getMaxU();
//			dv = ico.getMaxV();
//			v5.normal(dir.getStepX(), dir.getStepY(), dir.getStepZ());
//			switch(dir) {
//				case DOWN:
//					this.faceBrightness(Direction.SOUTH, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2.01, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2.01, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size*2, 0.5+size/2.01, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size*2, 0.5+size/2.01, du, dv);
//
//					this.faceBrightness(Direction.EAST, v5);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5+size/2.01, 0.5+size/2+size*2, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2.01, 0.5+size/2+size*2, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2.01, 0.5+size/2+size, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2.01, 0.5+size/2+size, 0.5-size/2, u, v);
//
//					this.faceBrightness(Direction.WEST, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2.01, 0.5+size/2+size, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2.01, 0.5+size/2+size, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5-size/2.01, 0.5+size/2+size*2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2.01, 0.5+size/2+size*2, 0.5-size/2, du, dv);
//
//					this.faceBrightness(Direction.NORTH, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2+size, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2+size, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size*2, 0.5-size/2.01, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size*2, 0.5-size/2.01, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2+size, 0.5-size/2.01, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2+size, 0.5-size/2.01, u, v);
//
//					if (tile.insulated) {
//						this.faceBrightness(Direction.DOWN, v5);
//						v5.addVertexWithUV(0.5-size/2, 0.5+size+size+size/2, 0.5+size/2, du, dv);
//						v5.addVertexWithUV(0.5+size/2, 0.5+size+size+size/2, 0.5+size/2, u, dv);
//						v5.addVertexWithUV(0.5+size/2, 0.5+size+size+size/2, 0.5-size/2, u, v);
//						v5.addVertexWithUV(0.5-size/2, 0.5+size+size+size/2, 0.5-size/2, du, v);
//					}
//					break;
//				case EAST:
//					this.faceBrightness(Direction.DOWN, v5);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2.01, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5+size/2.01, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5+size/2.01, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2.01, 0.5-size/2, u, v);
//
//					this.faceBrightness(Direction.SOUTH, v5);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5+size/2, du, dv);
//
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2.01, du, v);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5-size/2, 0.5+size/2.01, u, v);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5+size/2, 0.5+size/2.01, u, dv);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5+size/2.01, du, dv);
//
//					this.faceBrightness(Direction.UP, v5);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5+size/2, du, dv);
//
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2.01, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5-size/2.01, 0.5-size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5-size/2.01, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2.01, 0.5+size/2, du, dv);
//
//					this.faceBrightness(Direction.NORTH, v5);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5+size/2, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2+size, 0.5-size/2, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5+size/2+size, 0.5+size/2, 0.5-size/2.01, u, dv);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5+size/2, 0.5-size/2.01, du, dv);
//					v5.addVertexWithUV(0.5+size/2+size*2, 0.5-size/2, 0.5-size/2.01, du, v);
//					v5.addVertexWithUV(0.5+size/2+size, 0.5-size/2, 0.5-size/2.01, u, v);
//
//					if (tile.insulated) {
//						this.faceBrightness(Direction.EAST, v5);
//						v5.addVertexWithUV(0.5+size+size+size/2, 0.5+size/2, 0.5-size/2, du, dv);
//						v5.addVertexWithUV(0.5+size+size+size/2, 0.5+size/2, 0.5+size/2, u, dv);
//						v5.addVertexWithUV(0.5+size+size+size/2, 0.5-size/2, 0.5+size/2, u, v);
//						v5.addVertexWithUV(0.5+size+size+size/2, 0.5-size/2, 0.5-size/2, du, v);
//					}
//					break;
//				case NORTH:
//					this.faceBrightness(Direction.DOWN, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, u, v);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2.01, 0.5+size/2+size*2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2.01, 0.5+size/2+size*2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2.01, 0.5+size/2+size, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2.01, 0.5+size/2+size, u, v);
//
//					this.faceBrightness(Direction.EAST, v5);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2+size, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2+size, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);
//
//					v5.addVertexWithUV(0.5+size/2.01, 0.5+size/2, 0.5+size/2+size, u, dv);
//					v5.addVertexWithUV(0.5+size/2.01, 0.5+size/2, 0.5+size/2+size*2, du, dv);
//					v5.addVertexWithUV(0.5+size/2.01, 0.5-size/2, 0.5+size/2+size*2, du, v);
//					v5.addVertexWithUV(0.5+size/2.01, 0.5-size/2, 0.5+size/2+size, u, v);
//
//					this.faceBrightness(Direction.WEST, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, u, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2+size, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2+size, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2.01, 0.5-size/2, 0.5+size/2+size, du, v);
//					v5.addVertexWithUV(0.5-size/2.01, 0.5-size/2, 0.5+size/2+size*2, u, v);
//					v5.addVertexWithUV(0.5-size/2.01, 0.5+size/2, 0.5+size/2+size*2, u, dv);
//					v5.addVertexWithUV(0.5-size/2.01, 0.5+size/2, 0.5+size/2+size, du, dv);
//
//					this.faceBrightness(Direction.UP, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2+size, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2+size, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2+size, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2+size, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2.01, 0.5+size/2+size, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2.01, 0.5+size/2+size, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2.01, 0.5+size/2+size*2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2.01, 0.5+size/2+size*2, du, dv);
//
//					if (tile.insulated) {
//						this.faceBrightness(Direction.SOUTH, v5);
//						v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size+size+size/2, du, v);
//						v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size+size+size/2, u, v);
//						v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size+size+size/2, u, dv);
//						v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size+size+size/2, du, dv);
//					}
//					break;
//				case SOUTH:
//					this.faceBrightness(Direction.DOWN, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, u, v);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/1.99, 0.5-size/2-size, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/1.99, 0.5-size/2-size, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/1.99, 0.5-size/2-size*2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/1.99, 0.5-size/2-size*2, u, v);
//
//					this.faceBrightness(Direction.EAST, v5);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2-size, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2-size, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);
//
//					v5.addVertexWithUV(0.5+size/1.99, 0.5+size/2, 0.5-size/2-size*2, u, dv);
//					v5.addVertexWithUV(0.5+size/1.99, 0.5+size/2, 0.5-size/2-size, du, dv);
//					v5.addVertexWithUV(0.5+size/1.99, 0.5-size/2, 0.5-size/2-size, du, v);
//					v5.addVertexWithUV(0.5+size/1.99, 0.5-size/2, 0.5-size/2-size*2, u, v);
//
//					this.faceBrightness(Direction.WEST, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, u, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2-size, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2-size, du, dv);
//
//					v5.addVertexWithUV(0.5-size/1.99, 0.5-size/2, 0.5-size/2-size*2, du, v);
//					v5.addVertexWithUV(0.5-size/1.99, 0.5-size/2, 0.5-size/2-size, u, v);
//					v5.addVertexWithUV(0.5-size/1.99, 0.5+size/2, 0.5-size/2-size, u, dv);
//					v5.addVertexWithUV(0.5-size/1.99, 0.5+size/2, 0.5-size/2-size*2, du, dv);
//
//					this.faceBrightness(Direction.UP, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2-size, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2-size, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2-size, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2-size, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/1.99, 0.5-size/2-size*2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/1.99, 0.5-size/2-size*2, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/1.99, 0.5-size/2-size, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/1.99, 0.5-size/2-size, du, dv);
//
//					if (tile.insulated) {
//						this.faceBrightness(Direction.SOUTH, v5);
//						v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size-size*0-size/2, du, dv);
//						v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size-size*0-size/2, u, dv);
//						v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size-size*0-size/2, u, v);
//						v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size-size*0-size/2, du, v);
//					}
//					break;
//				case UP:
//					this.faceBrightness(Direction.SOUTH, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size*2, 0.5+size/1.99, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size*2, 0.5+size/1.99, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/1.99, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/1.99, du, dv);
//
//					this.faceBrightness(Direction.EAST, v5);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5+size/1.99, 0.5-size/2-size, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/1.99, 0.5-size/2-size, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/1.99, 0.5-size/2-size*2, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/1.99, 0.5-size/2-size*2, 0.5-size/2, u, v);
//
//					this.faceBrightness(Direction.WEST, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, du, dv);
//
//					v5.addVertexWithUV(0.5-size/1.99, 0.5-size/2-size*2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/1.99, 0.5-size/2-size*2, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5-size/1.99, 0.5-size/2-size, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/1.99, 0.5-size/2-size, 0.5-size/2, du, dv);
//
//					this.faceBrightness(Direction.NORTH, v5);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2-size, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2-size, 0.5-size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size, 0.5-size/1.99, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size, 0.5-size/1.99, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2-size*2, 0.5-size/1.99, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2-size*2, 0.5-size/1.99, u, v);
//
//					if (tile.insulated) {
//						this.faceBrightness(Direction.UP, v5);
//						v5.addVertexWithUV(0.5-size/2, 0.5-size-size-size/2, 0.5-size/2, du, v);
//						v5.addVertexWithUV(0.5+size/2, 0.5-size-size-size/2, 0.5-size/2, u, v);
//						v5.addVertexWithUV(0.5+size/2, 0.5-size-size-size/2, 0.5+size/2, u, dv);
//						v5.addVertexWithUV(0.5-size/2, 0.5-size-size-size/2, 0.5+size/2, du, dv);
//					}
//					break;
//				case WEST:
//					this.faceBrightness(Direction.DOWN, v5);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5+size/1.99, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/1.99, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/1.99, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5+size/1.99, 0.5-size/2, u, v);
//
//					this.faceBrightness(Direction.SOUTH, v5);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/2, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5-size/2, 0.5+size/1.99, du, v);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/1.99, u, v);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5+size/1.99, u, dv);
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5+size/2, 0.5+size/1.99, du, dv);
//
//					this.faceBrightness(Direction.UP, v5);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5+size/2, du, dv);
//
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5-size/1.99, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/1.99, 0.5-size/2, u, v);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/1.99, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5-size/1.99, 0.5+size/2, du, dv);
//
//					this.faceBrightness(Direction.NORTH, v5);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5+size/2, 0.5-size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2-size, 0.5-size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/2, u, v);
//
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5+size/2, 0.5-size/1.99, u, dv);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5+size/2, 0.5-size/1.99, du, dv);
//					v5.addVertexWithUV(0.5-size/2-size, 0.5-size/2, 0.5-size/1.99, du, v);
//					v5.addVertexWithUV(0.5-size/2-size*2, 0.5-size/2, 0.5-size/1.99, u, v);
//
//					if (tile.insulated) {
//						this.faceBrightness(Direction.WEST, v5);
//						v5.addVertexWithUV(0.5-size/2-size-size, 0.5-size/2, 0.5-size/2, du, v);
//						v5.addVertexWithUV(0.5-size/2-size-size, 0.5-size/2, 0.5+size/2, u, v);
//						v5.addVertexWithUV(0.5-size/2-size-size, 0.5+size/2, 0.5+size/2, u, dv);
//						v5.addVertexWithUV(0.5-size/2-size-size, 0.5+size/2, 0.5-size/2, du, dv);
//					}
//					break;
//				default:
//					break;
//			}
//		}
//		else {
//			this.faceBrightness(dir, v5);
//			v5.normal(dir.getStepX(), dir.getStepY(), dir.getStepZ());
//			switch(dir) {
//				case DOWN:
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, u, v);
//					break;
//				case NORTH:
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, du, dv);
//					break;
//				case EAST:
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5+size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, u, v);
//					break;
//				case WEST:
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, u, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, du, dv);
//					break;
//				case UP:
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, u, v);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5+size/2, u, dv);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5+size/2, du, dv);
//					break;
//				case SOUTH:
//					v5.addVertexWithUV(0.5-size/2, 0.5+size/2, 0.5-size/2, u, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5+size/2, 0.5-size/2, du, dv);
//					v5.addVertexWithUV(0.5+size/2, 0.5-size/2, 0.5-size/2, du, v);
//					v5.addVertexWithUV(0.5-size/2, 0.5-size/2, 0.5-size/2, u, v);
//					break;
//				default:
//					break;
//			}
//		}
//		v5.addTranslation(-x, -y, -z);
//	}
//}
