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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import reika.electricraft.blockentities.BlockEntityGenerator;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import reika.electricraft.blockentities.BlockEntityWirelessCharger.ChargerTiers;


public class BlockChargePad extends Block implements EntityBlock {
    public static ChargerTiers itemRender;

    public BlockChargePad(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityWirelessCharger(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return pLevel.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
            ((BlockEntityWirelessCharger) pBlockEntity).updateEntity(pLevel1, pPos);
        });
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack stack) {
        if (!player.isCreative() && this.canEntityDestroy(state, world, pos, player))
            this.destroy(world, pos, state);
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
//   todo     if (world.getBlockState(pos) == this.defaultBlockState())
//            ReikaItemHelper.dropItems(world, pos, this.getDrops(world, pos));
        super.destroy(world, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootParams.Builder builder) {
        ArrayList li = new ArrayList<>();
        ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
        is.getOrCreateTag().putInt("tier", 1); //todo tier
        li.add(is);
        return li;
    }

    public ItemStack getPickBlock(BlockHitResult target, Level world, BlockPos pos) {
        ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
        is.getOrCreateTag().putInt("tier", 1); //todo tier level
        return is;
    }

}
