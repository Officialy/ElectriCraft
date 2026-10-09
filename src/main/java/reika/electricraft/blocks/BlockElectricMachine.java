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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.registry.ReikaDyeHelper;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.electricraft.base.BlockEntityResistorBase.ColorBand;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.blockentities.BlockEntityMotor;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blocks.BlockElectricFuse;
import reika.electricraft.base.BlockEntityWireComponent;
import reika.electricraft.auxiliary.interfaces.ConversionTile;
import reika.rotarycraft.auxiliary.RotaryAux;
import reika.rotarycraft.auxiliary.interfaces.NBTMachine;
import reika.rotarycraft.registry.RotaryItems;

//@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public abstract class BlockElectricMachine extends ElectriBlock {// implements IWailaDataProvider {

    public BlockElectricMachine(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state,
                            net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        Direction playerLook = state.getValue(FACING).getOpposite();
        if (blockEntity instanceof BlockEntityTransformer transformer) {
            // V31a mounted the windings across the player's view: input/output are right/left.
            transformer.setFacing(playerLook.getClockWise());
        } else if (blockEntity instanceof BlockEntityWireComponent component) {
            component.setFacing(playerLook);
        } else if (blockEntity instanceof ConversionTile conversion) {
            conversion.setFacing(playerLook);
        }
        if (blockEntity != null)
            blockEntity.setChanged();
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape machineShape = this.getMachineShape(level, pos);
        return machineShape != null ? machineShape : super.getShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape machineShape = this.getMachineShape(level, pos);
        return machineShape != null ? machineShape : super.getCollisionShape(state, level, pos, context);
    }

    /** Restores V31a's non-cubic bounds for inline components and the transformer. */
    private VoxelShape getMachineShape(BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        AABB bounds = null;
        if (blockEntity instanceof reika.electricraft.base.BlockEntityWireComponent component)
            bounds = component.getAABB();
        else if (blockEntity instanceof BlockEntityTransformer transformer)
            bounds = transformer.getAABB();
        return bounds != null ? Shapes.create(bounds.move(-pos.getX(), -pos.getY(), -pos.getZ())) : null;
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
    public void playerDestroy(net.minecraft.server.level.ServerLevel world, net.minecraft.server.level.ServerPlayer ep, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack p_49832_) {
        if (!this.canHarvest(world, ep, pos))
            return;
        // Vanilla removes the block before invoking playerDestroy, so looking it up from the
        // world here loses both the machine identity and its BE. Use the supplied state/context.
        super.playerDestroy(world, ep, pos, state, blockEntity, p_49832_);
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ElectriTiles machine = ElectriTiles.getMachine(state.getBlock());
        if (machine == null)
            return java.util.List.of();
        // Fuse amperage is a modern registry identity, not mutable stack NBT. In particular, do
        // not collapse every fuse back to ElectriTiles.FUSE's representative 32 A item here.
        ItemStack result = state.getBlock() instanceof BlockElectricFuse
                ? new ItemStack(state.getBlock().asItem())
                : machine.getCraftedProduct();
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(state.getBlock() instanceof BlockElectricFuse)
                && machine.hasNBTVariants() && blockEntity instanceof NBTMachine nbtMachine) {
            CompoundTag nbt = nbtMachine.getTagsToWriteToStack();
            ReikaItemHelper.setStackTag(result, nbt != null ? nbt.copy() : null);
        }
        return java.util.List.of(result);
    }
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

    /** Native 26.3 held-item interaction, preserving V31a's band positions and motor upgrades. */
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world,
            BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (stack.is(RotaryItems.SCREWDRIVER.get()) || stack.is(RotaryItems.UPGRADE.get()))
            return InteractionResult.PASS;
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof BlockEntityResistorBase resistor && ReikaDyeHelper.isDyeItem(stack)) {
            Direction dir = resistor.getFacing();
            double inc = dir.getStepX() != 0 ? hit.getLocation().x - pos.getX() : hit.getLocation().z - pos.getZ();
            if (dir.getStepX() > 0 || dir.getStepZ() > 0) inc = 1 - inc;
            if (resistor.isFlipped && dir.getStepZ() != 0) inc = 1 - inc;
            ColorBand[] bands = resistor.getColorBands();
            int digit = -1;
            for (int i = 0; i < bands.length; i++) {
                if (ReikaMathLibrary.isValueInsideBoundsIncl(0.1875 * i + 0.125, 0.1875 * i + 0.25, inc)) {
                    digit = i + 1;
                    break;
                }
            }
            ReikaDyeHelper color = ReikaDyeHelper.getColorFromItem(stack);
            ColorBand band = ColorBand.getBandFromColor(color);
            if (digit < 1 || band == null || (digit == bands.length && band.ordinal() > 7))
                return InteractionResult.PASS;
            if (!world.isClientSide()) {
                if (!resistor.setColor(color, digit)) return InteractionResult.PASS;
                if (!player.isCreative()) stack.shrink(1);
                resistor.syncAllData(false);
            }
            return InteractionResult.SUCCESS;
        }
        if (entity instanceof BlockEntityMotor motor && motor.canUpgrade(stack)) {
            if (!world.isClientSide()) {
                if (!motor.upgrade(stack)) return InteractionResult.PASS;
                if (!player.isCreative()) stack.shrink(1);
                motor.syncAllData(false);
            }
            return InteractionResult.SUCCESS;
        }
        // Transformer useWithoutItem already opens its native menu; allow vanilla's fallback to reach it.
        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

}
