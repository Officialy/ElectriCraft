/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import reika.electricraft.base.BlockElectriCable;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;

public class BlockRFCable extends BlockElectriCable {

	public BlockRFCable(Properties par2Material) {
		super(par2Material);
	}

/*	@Override
	public boolean onBlockActivated(Level world, int x, int y, int z, Player ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (!ReikaItemHelper.matchStacks(is, ElectriTiles.CABLE.getCraftedProduct())) {
			ep.openMenu(ElectriCraft.instance, 0, world, x, y, z);
			return true;
		}
		return false;
	}*/

	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
		BlockEntityRFCable te = (BlockEntityRFCable)world.getBlockEntity(pos);
		if (!world.isClientSide() && te != null)
			te.removeFromNetwork();
		super.destroy(world, pos, state);
	}

/*
	@Override
	public ElectriTiles getTile() {
		return ElectriTiles.CABLE;
	}*/

	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
		return p_153212_.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
			((BlockEntityRFCable) pBlockEntity).updateEntity(pLevel1, pPos);
		});
	}

	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlockEntityRFCable(pos, state);
	}
}
