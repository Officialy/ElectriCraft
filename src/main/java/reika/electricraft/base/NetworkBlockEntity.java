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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.electricraft.ElectriCraft;
import reika.electricraft.auxiliary.interfaces.NetworkTile;
import reika.electricraft.network.WireNetwork;

public abstract class NetworkBlockEntity extends ElectriBlockEntity implements NetworkTile {

    protected WireNetwork network;
    private boolean isConnectable = true;

    public NetworkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void updateEntity(Level world, BlockPos pos) {
        if ((this.getTicksExisted() == 0 || network == null) && !world.isClientSide()) {
            this.findAndJoinNetwork(world, pos);
            //ReikaJavaLibrary.pConsole(network, Dist.DEDICATED_SERVER);
        }
    }

    //	@Override //todo onInvalidateOrUnload
    public final void onInvalidateOrUnload(Level world, int x, int y, int z, boolean invalid) {
        isConnectable = false;
        if (network != null)
            network.removeElement(this);
    }

    public final boolean isConnectable() {
        return isConnectable && !level.isClientSide() && level.hasChunk(worldPosition.getX(), worldPosition.getZ());
    }

    public void onNetworkChanged() {

    }

    public final void findAndJoinNetwork(Level world, BlockPos pos) {
        network = null;
        if (!isConnectable)
            return;
        network = new WireNetwork();
        network.addElement(this);
        for (int i = 0; i < 6; i++) {
            Direction dir = dirs[i];
            if (this.canNetworkOnSide(dir)) {//todo && this.isChunkLoadedOnSide(dir)) {
                BlockEntity te = this.getAdjacentBlockEntity(dir);
                this.linkTile(te, dir);
            }
        }
        this.onJoinNetwork();
    }

    private void linkTile(BlockEntity te, Direction dir) {
        if (te instanceof NetworkBlockEntity n) {
            //ReikaJavaLibrary.pConsole(te, Dist.DEDICATED_SERVER);
            if (n.canNetworkOnSide(dir.getOpposite())) {
                WireNetwork w = n.network;
                if (w != null) {
                    //ReikaJavaLibrary.pConsole(dir+":"+te, Dist.DEDICATED_SERVER);
                    w.merge(network);
                }
            }
        }
/*		else if (te instanceof WorldRift) {
			WorldRift sr = (WorldRift)te;
			WorldLocation loc = sr.getLinkTarget();
			if (loc != null) {
				this.linkTile(sr.getBlockEntityFrom(dir), dir);
			}
		}*/
    }

    protected void onJoinNetwork() {
    }

    public abstract boolean canNetworkOnSide(Direction dir);

    public final WireNetwork getNetwork() {
        return network;
    }

    public final void setNetwork(WireNetwork n) {
        if (n == null) {
            ElectriCraft.LOGGER.error(this + " was told to join a null network!");
        } else {
            network = n;
            network.addElement(this);
        }
    }

    public final void removeFromNetwork() {
        if (network == null)
            ElectriCraft.LOGGER.error(this + " was removed from a null network!");
        else
            network.removeElement(this);
    }

    public final void rebuildNetwork() {
        this.removeFromNetwork();
        this.resetNetwork();
        this.findAndJoinNetwork(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }

    public final void resetNetwork() {
        network = null;
    }

    @Override
    public final Level getWorld() {
        return level;
    }

    @Override
    public final int getX() {
        return worldPosition.getX();
    }

    @Override
    public final int getY() {
        return worldPosition.getY();
    }

    @Override
    public final int getZ() {
        return worldPosition.getZ();
    }

}
