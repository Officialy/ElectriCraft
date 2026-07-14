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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import reika.dragonapi.interfaces.blockentity.BreakAction;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriCable;
import reika.electricraft.network.rf.RFNetwork;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityRFCable extends ElectriCable implements BreakAction, net.minecraft.world.MenuProvider {

	@Override
	public net.minecraft.network.chat.Component getDisplayName() {
		return net.minecraft.network.chat.Component.literal("RF Cable");
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int windowId, net.minecraft.world.entity.player.Inventory inv, net.minecraft.world.entity.player.Player player) {
		return new reika.rotarycraft.gui.container.machine.BlankContainer<>(reika.electricraft.registry.ElectriMenus.RF_CABLE.get(), windowId, inv, this);
	}


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
	    /* 26.1-lifecycle */ super.updateEntity(); // 26.1: drive BlockEntityBase lifecycle (ticksExisted++, onFirstTick → recompute/sync).
		if ((this.getTicksExisted() == 0 || network == null) && !world.isClientSide()) {
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
				if (te instanceof BlockEntityRFCable n) {
					RFNetwork w = n.network;
					if (w != null) {
						w.merge(network);
					}
				}
				else {
					//Modern FE machines expose the block energy capability rather than
					//implementing IEnergyStorage on the BE.
					BlockPos adj = pos.relative(dir);
					EnergyHandler cap = world.getCapability(Capabilities.Energy.BLOCK, adj, dir.getOpposite());
					if (cap != null)
						network.addConnection(world, adj, dir.getOpposite());
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

	/**
	 * The FE face of the cable: inserts feed the shared network buffer (legacy: receive-only;
	 * the network pushes out itself on its tick).
	 */
	private final EnergyHandler energyView = new EnergyHandler() {
		@Override
		public long getAmountAsLong() {
			return network != null ? network.getBufferedEnergy() : 0;
		}

		@Override
		public long getCapacityAsLong() {
			return network != null ? network.getIOLimit() : 0;
		}

		@Override
		public int insert(int amt, net.neoforged.neoforge.transfer.transaction.TransactionContext tx) {
			return network != null ? network.insertEnergy(amt, tx) : 0;
		}

		@Override
		public int extract(int amt, net.neoforged.neoforge.transfer.transaction.TransactionContext tx) {
			return 0; //legacy canExtract() == false
		}
	};

	public EnergyHandler getEnergyView() {
		return energyView;
	}

	@Override
	protected void readSyncTag(CompoundTag NBT)
	{
		super.readSyncTag(NBT);

		if (NBT.contains("limit"))
			this.setRFLimit(NBT.getIntOr("limit", 0));
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT)
	{
		super.writeSyncTag(NBT);

		NBT.putInt("limit", RFlimit);
	}

	@Override
	protected boolean connectsToTile(BlockEntity te, Direction dir) {
		if (te == null || te.getLevel() == null)
			return false;
		if (te instanceof BlockEntityRFCable)
			return true;
		EnergyHandler cap = te.getLevel().getCapability(Capabilities.Energy.BLOCK, te.getBlockPos(), dir.getOpposite());
		return cap != null;
	}

	@Override
	protected void onNetworkUpdate(Level world, int x, int y, int z, Direction dir) {
		if (network != null) {
			BlockPos adj = worldPosition.relative(dir);
			EnergyHandler cap = world.getCapability(Capabilities.Energy.BLOCK, adj, dir.getOpposite());
			if (cap != null) {
				network.addConnection(world, adj, dir.getOpposite());
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
