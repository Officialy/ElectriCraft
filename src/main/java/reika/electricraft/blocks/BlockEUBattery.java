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
//import net.minecraft.block.material.Material;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//
//import reika.electricraft.base.BatteryBlock;
//import reika.electricraft.registry.ElectriItems;
//import reika.electricraft.registry.ElectriTiles;
//import reika.electricraft.tileentities.modinterface.BlockEntityEUBattery;
//
//public class BlockEUBattery extends BatteryBlock {
//
//	public BlockEUBattery(Material par2Material) {
//		super(par2Material);
//	}
//
//	@Override
//	public BlockEntity createBlockEntity(Level world, int meta) {
//		return new BlockEntityEUBattery();
//	}
//
//	@Override
//	protected String getTextureSubstring() {
//		return "eu";
//	}
//
//	@Override
//	public ElectriTiles getTile() {
//		return ElectriTiles.EUBATTERY;
//	}
//
//	@Override
//	public ElectriItems getItem() {
//		return ElectriItems.EUBATTERY;
//	}
//
//}
