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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import reika.electricraft.blockentities.BlockEntityPreciseResistor;
import reika.electricraft.blockentities.BlockEntityRelay;

//@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockElectricRelay extends BlockElectricMachine {// implements IWailaDataProvider {

    public BlockElectricRelay(Properties properties) {
        super(properties);
    }

    @Override
    public  <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
            ((BlockEntityRelay) pBlockEntity).updateEntity(pLevel1, pPos);
        });
    }

    @Override
    public  BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BlockEntityRelay(pPos, pState);
    }

}
