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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.registry.ReikaDyeHelper;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.electricraft.base.BlockEntityResistorBase.ColorBand;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.blockentities.BlockEntityMotor;
import reika.electricraft.blockentities.BlockEntityResistor;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.registry.ElectriTiles;
import reika.rotarycraft.auxiliary.RotaryAux;
import reika.rotarycraft.auxiliary.interfaces.NBTMachine;
import reika.rotarycraft.registry.RotaryItems;

//@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockElectricTransformer extends BlockElectricMachine {// implements IWailaDataProvider {

    public BlockElectricTransformer(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
            ((BlockEntityTransformer) pBlockEntity).updateEntity(pLevel1, pPos);
        });
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BlockEntityTransformer(pPos, pState);
    }


}
