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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import reika.electricraft.base.BatteryBlock;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;

public class BlockRFBattery extends BatteryBlock {

	public BlockRFBattery(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
			((BlockEntityRFBattery) pBlockEntity).updateEntity(pLevel1, pPos);
		});
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BlockEntityRFBattery(pPos, pState);
	}

	@Override
	public ElectriTiles getTile() {
		return ElectriTiles.RFBATTERY;
	}

	@Override
	public RegistryObject<Item> getItem() {
		return ElectriItems.RFBATTERY;
	}

}
