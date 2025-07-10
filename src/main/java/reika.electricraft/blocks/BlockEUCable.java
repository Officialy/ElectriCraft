///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft.blocks;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//
//import net.minecraft.world.level.block.state.BlockState;
//
//import reika.electricraft.base.BlockElectriCable;
//import reika.electricraft.registry.ElectriTiles;
//
//public class BlockEUCable extends BlockElectriCable {
//
//	public BlockEUCable(Properties properties) {
//		super(properties);
//	}
//
//	/*
//	@Override
//	public boolean onBlockActivated(Level world, int x, int y, int z, Player ep, int s, float a, float b, float c) {
//		ItemStack is = ep.getCurrentEquippedItem();
//		if (!ReikaItemHelper.matchStacks(is, ElectriTiles.CABLE.getCraftedProduct())) {
//			ep.openGui(ElectriCraft.instance, 0, world, x, y, z);
//			return true;
//		}
//		return false;
//	}
//	 */
//	@Override
//	public void breakBlock(Level world, int x, int y, int z, Block oldid, int oldmeta) {
//		BlockEntityEUCable te = (BlockEntityEUCable)world.getBlockEntity(new BlockPos(x, y, z));
//		super.breakBlock(world, x, y, z, oldid, oldmeta);
//	}
//
//	@Override
//	public ElectriTiles getTile() {
//		return ElectriTiles.EUCABLE;
//	}
//
//	
//	@Override
//	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
//		return BlockEntityEUCable();
//	}
//}
