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
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.registry.ReikaDyeHelper;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.electricraft.base.BlockEntityResistorBase.ColorBand;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.blockentities.BlockEntityMotor;
import reika.rotarycraft.auxiliary.RotaryAux;
import reika.rotarycraft.auxiliary.interfaces.NBTMachine;
import reika.rotarycraft.registry.RotaryItems;

//@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public abstract class BlockElectricMachine extends ElectriBlock {// implements IWailaDataProvider {

    public BlockElectricMachine(Properties properties) {
        super(properties);
    }

//    @Override
//    public ItemStack getPickBlock(BlockHitResult target, Level world, int x, int y, int z) {
//        return ElectriTiles.getTE(world, x, y, z).getCraftedProduct();
//    }

 /*   @Override
    public int damageDropped(int par1) {
        return ElectriTiles.getMachineFromIDandMetadata(this, par1).ordinal();
    }*/

    private boolean canHarvest(Level world, Player ep, BlockPos pos) {
        if (ep.isCreative())
            return false;
        return RotaryAux.canHarvestSteelMachine(ep);
    }

    @Override
    public void playerDestroy(Level world, Player ep, BlockPos pos, BlockState state,  BlockEntity blockEntity, ItemStack p_49832_) {
        if (!this.canHarvest(world, ep, pos))
            return;
        BlockEntity te = world.getBlockEntity(pos);
        ElectriTiles m = ElectriTiles.getTE(world, pos);
        if (m != null) {
            ItemStack is = m.getCraftedProduct();
            if (m.hasNBTVariants()) {
                CompoundTag nbt = ((NBTMachine) te).getTagsToWriteToStack();
                is.setTag(nbt != null ? nbt.copy() : null);
            }
            ReikaItemHelper.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, is);
        }    }
/*    @Override
    public final AABB getCollisionBoundingBoxFromPool(Level world, BlockPos pos) {
        ElectriTiles t = ElectriTiles.getTE(world, pos);
        if (t != null) {
            if (t.isSpecialWiringPiece()) {
                AABB box = ((BlockEntityWireComponent) world.getBlockEntity(pos)).getAABB();
                return box;
            } else if (t == ElectriTiles.TRANSFORMER) {
                AABB box = ((BlockEntityTransformer) world.getBlockEntity(pos)).getAABB();
                return box;
            }
        }
        return super.getCollisionBoundingBoxFromPool(world, pos);
    }*/

// todo   @Override
    public boolean onBlockActivated(Level world, BlockPos pos, Player ep, int side, float a, float b, float c) {
        ElectriTiles e = ElectriTiles.getTE(world, pos);
        ItemStack is = ep.getUseItem();
        RotaryItems ir = null;// RotaryItems.getEntry(is);
        if (ir != null && ir.overridesRightClick(is))
            return false;
        if (RotaryAux.isHoldingScrewdriver(ep))
            return false;
        if (e != null && e.isResistor() && ReikaDyeHelper.isDyeItem(is)) {
            BlockEntityResistorBase te = (BlockEntityResistorBase) world.getBlockEntity(pos);
            Direction dir = te.getFacing();
            float inc = dir.getStepX() != 0 ? a : c;
            if (dir.getStepX() > 0 || dir.getStepZ() > 0)
                inc = 1F - inc;
            if (te.isFlipped && dir.getStepZ() != 0) {
                inc = 1 - inc;
            }
            double dl = 0.1875;
            double l0 = 0.125;
            double w = 0.125;
            ColorBand[] bands = te.getColorBands();
            int band = -1;
            for (int i = 0; i < bands.length; i++) {
                double d0 = dl * i;
                if (ReikaMathLibrary.isValueInsideBoundsIncl(d0 + l0, d0 + l0 + w, inc)) {
                    band = i + 1;
                    break;
                }
            }
            if (band > 0) {
                if (te.setColor(ReikaDyeHelper.getColorFromItem(is), band)) {
                    if (!ep.isCreative())
                        is.setCount(is.getCount() - 1);
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (e == ElectriTiles.MOTOR) {
            BlockEntityMotor te = (BlockEntityMotor) world.getBlockEntity(pos);
            if (is != null && te.upgrade(is)) {
                if (!ep.isCreative())
                    is.setCount(is.getCount() - 1);
                return true;
            }
        }
        //	todo		ep.openMenu(ElectriCraft.instance, 0, world, x, y, z);
        return e == ElectriTiles.TRANSFORMER;
    }

}
