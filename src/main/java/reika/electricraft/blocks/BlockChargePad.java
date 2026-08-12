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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import reika.electricraft.blockentities.BlockEntityWirelessCharger.ChargerTiers;


public class BlockChargePad extends Block implements EntityBlock {
    public static final EnumProperty<net.minecraft.core.Direction> FACING = BlockStateProperties.FACING;
    public static ChargerTiers itemRender;

    public BlockChargePad(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        //The charge pad item carries its tier in the "tier" stack tag (was block metadata in 1.7.10).
        if (world.getBlockEntity(pos) instanceof reika.electricraft.blockentities.BlockEntityWirelessCharger te
                && reika.dragonapi.libraries.registry.ReikaItemHelper.hasStackTag(stack)) {
            te.setTier(reika.dragonapi.libraries.registry.ReikaItemHelper.getStackTag(stack).getIntOr("tier", 0));
        }
    }

    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityWirelessCharger(pos, state);
    }

    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return pLevel.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
            ((BlockEntityWirelessCharger) pBlockEntity).updateEntity(pLevel1, pPos);
        });
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state,  BlockEntity entity, ItemStack stack) {
        if (!player.isCreative())
            super.playerDestroy(world, player, pos, state, entity, stack);
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
//   todo     if (world.getBlockState(pos) == this.defaultBlockState())
//            ReikaItemHelper.dropItems(world, pos, this.getDrops(world, pos));
        super.destroy(world, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootParams.Builder builder) {
        ArrayList<ItemStack> li = new ArrayList<>();
        ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
        BlockEntity raw = builder.getOptionalParameter(
                net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
        int tier = raw instanceof BlockEntityWirelessCharger charger ? charger.getTier().ordinal() : 0;
        ReikaItemHelper.updateStackTag(is, __T__ -> __T__.putInt("tier", tier));
        li.add(is);
        return li;
    }

    public ItemStack getPickBlock(BlockHitResult target, Level world, BlockPos pos) {
        ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
        int tier = world.getBlockEntity(pos) instanceof BlockEntityWirelessCharger charger
                ? charger.getTier().ordinal() : 0;
        ReikaItemHelper.updateStackTag(is, __T__ -> __T__.putInt("tier", tier));
        return is;
    }

}
