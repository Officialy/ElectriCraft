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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import reika.electricraft.base.NetworkBlock;
import reika.electricraft.blockentities.BlockEntityFuse;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.blockentities.BlockEntityBattery;

//@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockElectricBattery extends NetworkBlock {// implements IWailaDataProvider {

    public BlockElectricBattery(Properties par2Material) {
        super(par2Material);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
            ((BlockEntityBattery) pBlockEntity).updateEntity(pLevel1, pPos);
        });
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BlockEntityBattery(pPos, pState);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        ArrayList li = new ArrayList<>();
        BlockEntityBattery te = (BlockEntityBattery) context.getLevel().getBlockEntity(null); //todo NULL BLOCKPOS
        long e = te.getStoredEnergy();
        ItemStack is = ElectriItems.BATTERY.get().getDefaultInstance();//.getStackOfMetadata(meta);
        is.getOrCreateTag().putLong("nrg", e);
        li.add(is);
        return li;
    }

//    @Override
    public ItemStack getPickBlock(BlockHitResult target, Level world, BlockPos pos) {
        BlockEntityBattery te = (BlockEntityBattery) world.getBlockEntity(pos);
        ItemStack is = ElectriItems.BATTERY.get().getDefaultInstance();//.getStackOfMetadata(meta);
        return is;
    }

}
