/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


import net.minecraft.world.phys.AABB;
import reika.electricraft.registry.ElectriTiles;

public abstract class ElectriCable extends ElectriBlockEntity {

    private final boolean[] connections = new boolean[6];

    public ElectriCable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void readSyncTag(CompoundTag NBT) {
        super.readSyncTag(NBT);

        for (int i = 0; i < 6; i++) {
            connections[i] = NBT.getBoolean("conn" + i);
        }
    }

    @Override
    protected void writeSyncTag(CompoundTag NBT) {
        super.writeSyncTag(NBT);

        for (int i = 0; i < 6; i++) {
            NBT.putBoolean("conn" + i, connections[i]);
        }
    }

    public final boolean isConnectionValidForSide(Direction dir) {
        if (dir.getStepX() == 0)// && MinecraftForgeClient.getRenderPass() != 1)
            dir = dir.getOpposite();
        return connections[dir.ordinal()];
    }

    @Override
    public final AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
    }

    public final void recomputeConnections(Level world, BlockPos pos) {
        for (int i = 0; i < 6; i++) {
            Direction dir = dirs[i];

            connections[i] = this.isConnected(dir);
            //world.func_147479_m(dx, dy, dz);
            this.onNetworkUpdate(world, pos.getX() + dir.getStepX(), pos.getY() + dir.getStepY(), pos.getZ() + dir.getStepZ(), dir);
        }
//		world.func_147479_m(x, y, z);
    }

    protected void onNetworkUpdate(Level world, int x, int y, int z, Direction dir) {

    }

    public final void deleteFromAdjacentConnections(Level world, int x, int y, int z) {
        for (int i = 0; i < 6; i++) {
            Direction dir = dirs[i];
            int dx = x + dir.getStepX();
            int dy = x + dir.getStepY();
            int dz = x + dir.getStepZ();
            ElectriTiles m = ElectriTiles.getTE(world, new BlockPos(dx, dy, dz));
            if (m == this.getMachine()) {
                ElectriCable te = (ElectriCable) world.getBlockEntity(new BlockPos(dx, dy, dz));
                te.connections[dir.getOpposite().ordinal()] = false;
//				world.func_147479_m(dx, dy, dz);
            }
        }
    }

    public final void addToAdjacentConnections(Level world, BlockPos pos) {
        for (int i = 0; i < 6; i++) {
            Direction dir = dirs[i];
            int dx = pos.getX() + dir.getStepX();
            int dy = pos.getY() + dir.getStepY();
            int dz = pos.getZ() + dir.getStepZ();
            ElectriTiles m = ElectriTiles.getTE(world, new BlockPos(dx, dy, dz));
            if (m == this.getMachine()) {
                ElectriCable te = (ElectriCable) world.getBlockEntity(new BlockPos(dx, dy, dz));
                te.connections[dir.getOpposite().ordinal()] = true;
                //world.func_147479_m(dx, dy, dz);
            }
        }
    }

    private boolean isConnected(Direction dir) {
        int x = worldPosition.getX() + dir.getStepX();
        int y = worldPosition.getY() + dir.getStepY();
        int z = worldPosition.getZ() + dir.getStepZ();
        ElectriTiles m = this.getMachine();
        ElectriTiles m2 = ElectriTiles.getTE(level, new BlockPos(x, y, z));
        return m == m2;
        //certain TEs
    }

    public final boolean isConnectedOnSideAt(Level world, BlockPos pos, Direction dir) {
        dir = dir.getStepX() == 0 ? dir.getOpposite() : dir;
        int dx = pos.getX() + dir.getStepX();
        int dy = pos.getY() + dir.getStepY();
        int dz = pos.getZ() + dir.getStepZ();
        Block b = world.getBlockState(new BlockPos(dx, dy, dz)).getBlock();
        if (b == this.getBlockEntityBlockID())
            return true;
        BlockEntity te = world.getBlockEntity(new BlockPos(dx, dy, dz));
        return te != null && this.connectsToTile(te, dir);
    }

    public final boolean isConnectedToHandlerOnSideAt(Level world, BlockPos pos, Direction dir) {
        dir = dir.getStepX() == 0 ? dir.getOpposite() : dir;
        int dx = pos.getX() + dir.getStepX();
        int dy = pos.getY() + dir.getStepY();
        int dz = pos.getZ() + dir.getStepZ();
        Block b = world.getBlockState(new BlockPos(dx, dy, dz)).getBlock();
        if (b == this.getBlockEntityBlockID())
            return false;
        BlockEntity te = world.getBlockEntity(new BlockPos(dx, dy, dz));
        return te != null && this.connectsToTile(te, dir);
    }

    protected abstract boolean connectsToTile(BlockEntity te, Direction dir);


}
