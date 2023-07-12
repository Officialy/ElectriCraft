/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.blockentities.modinterface;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import reika.dragonapi.interfaces.blockentity.BreakAction;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriCable;
import reika.electricraft.network.rf.RFNetwork;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityRFCable extends ElectriCable implements IEnergyStorage, BreakAction {

	protected RFNetwork network;
	private int RFlimit;

	public BlockEntityRFCable(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.RF_CABLE.get(), pos, state);
	}

	public void setRFLimit(int limit) {
		if (RFlimit != limit) {
			RFlimit = limit;
			if (network != null)
				network.setIOLimit(limit);
		}
	}

	public int getRFLimit() {
		return RFlimit;
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
		if ((this.getTicksExisted() == 0 || network == null) && !world.isClientSide) {
			this.findAndJoinNetwork(world, pos);
			//ReikaJavaLibrary.pConsole(network, Dist.DEDICATED_SERVER);
		}
		if (network != null && network.getIOLimit() != RFlimit) {
			RFlimit = network.getIOLimit();
		}
	}

	public final void findAndJoinNetwork(Level world, BlockPos pos) {
		network = new RFNetwork();
		network.setIOLimit(this.getRFLimit());
		network.addElement(this);
		for (int i = 0; i < 6; i++) {
			Direction dir = dirs[i];
			if (this.isChunkLoadedOnSide(dir)) {
				BlockEntity te = this.getAdjacentBlockEntity(dir);
				if (te instanceof BlockEntityRFCable) {
					//ReikaJavaLibrary.pConsole(te, Dist.DEDICATED_SERVER);
					BlockEntityRFCable n = (BlockEntityRFCable)te;
					RFNetwork w = n.network;
					if (w != null) {
						//ReikaJavaLibrary.pConsole(dir+":"+te, Dist.DEDICATED_SERVER);
						w.merge(network);
					}
				}
				else if (te instanceof IEnergyStorage) {
					//ReikaJavaLibrary.pConsole(te, Dist.DEDICATED_SERVER);
					IEnergyStorage n = (IEnergyStorage)te;
					if (n.canReceive())
						network.addConnection(n, dir.getOpposite());
				}
			}
		}
		this.onJoinNetwork();
	}

	protected void onJoinNetwork() {

	}

	public final RFNetwork getNetwork() {
		return network;
	}

	public final void setNetwork(RFNetwork n) {
		if (n == null) {
			ElectriCraft.LOGGER.error(this+" was told to join a null network!");
		}
		else {
			network = n;
			network.addElement(this);
		}
	}

	public final void removeFromNetwork() {
		if (network == null)
			ElectriCraft.LOGGER.error(this+" was removed from a null network!");
		else
			network.removeElement(this);
	}

	public final void rebuildNetwork() {
		this.removeFromNetwork();
		this.resetNetwork();
		this.findAndJoinNetwork(level, worldPosition);
	}

	public final void resetNetwork() {
		network = null;
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.RF_CABLE;
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return network != null ? network.addEnergy(maxReceive, simulate) : 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return network != null ? network.drainEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public int getEnergyStored() {
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	protected void readSyncTag(CompoundTag NBT)
	{
		super.readSyncTag(NBT);

		if (NBT.contains("limit"))
			this.setRFLimit(NBT.getInt("limit"));
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT)
	{
		super.writeSyncTag(NBT);

		NBT.putInt("limit", RFlimit);
	}

	@Override
	protected boolean connectsToTile(BlockEntity te, Direction dir) {
		return (te instanceof IEnergyStorage && ((IEnergyStorage)te).canReceive());// || te instanceof WorldRift;
	}

	@Override
	protected void onNetworkUpdate(Level world, int x, int y, int z, Direction dir) {
		if (network != null) {
			BlockEntity te = this.getAdjacentBlockEntity(dir);
			if (te instanceof IEnergyStorage ih) {
				if (ih.canReceive()) {
					network.addConnection(ih, dir.getOpposite());
				}
			}
		}
	}

	@Override
	public void breakBlock() {
		if (network != null) {
			network.removeElement(this);
		}
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
