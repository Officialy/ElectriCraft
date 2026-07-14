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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.electricraft.base.BatteryTileBase;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityRFBattery extends BatteryTileBase implements EnergyHandler {

	private long energy;
	public static final long CAPACITY = 60000000000000L;//1099511627775L;//;

	public BlockEntityRFBattery(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.RF_BATTERY.get(), pos, state);
	}

	@Override
	public String getDisplayEnergy() {
		return formatNumber(energy);
	}

	@Override
	public long getStoredEnergy() {
		return energy;
	}

	@Override
	public long getMaxEnergy() {
		return CAPACITY;
	}

	@Override
	public String getFormattedCapacity() {
		return formatNumber(CAPACITY);
	}

	private static String formatNumber(long num) {
		return String.format("%.3f %sRF", ReikaMathLibrary.getThousandBase(num), ReikaEngLibrary.getSIPrefix(num));
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.RFBATTERY;
	}
	@Override
	public void updateEntity(Level world, BlockPos pos) {
	    /* 26.1-lifecycle */ super.updateEntity(); // 26.1: drive BlockEntityBase lifecycle (ticksExisted++, onFirstTick → recompute/sync).
		if (world.getGameTime()%64 == 0) {
			world.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
		}

		if (!world.isClientSide() && this.hasRedstoneSignal()) {
			int exp = (int)Math.min(energy, Integer.MAX_VALUE);
			if (exp > 0) {
				//Push up through the block energy capability (the legacy RF-API instanceof
				//branches connected to nothing on modern FE machines).
				EnergyHandler cap = world.getCapability(Capabilities.Energy.BLOCK, pos.above(), Direction.DOWN);
				if (cap != null) {
					try (Transaction tx = Transaction.openRoot()) {
						energyJournal.updateSnapshots(tx);
						energy -= cap.insert(exp, tx);
						tx.commit();
					}
				}
			}
		}
	}
	//Journals the buffer so mutations inside an aborted transaction revert.
	private final SnapshotJournal<Long> energyJournal = new SnapshotJournal<>() {
		@Override
		protected Long createSnapshot() {
			return energy;
		}

		@Override
		protected void revertToSnapshot(Long snapshot) {
			energy = snapshot;
		}
	};

	@Override
	public long getAmountAsLong() {
		return energy;
	}

	@Override
	public long getCapacityAsLong() {
		return CAPACITY;
	}

	@Override
	public int insert(int amt, TransactionContext tx) {
		int ret = (int)Math.min(amt, CAPACITY - energy);
		if (ret > 0) {
			energyJournal.updateSnapshots(tx);
			energy += ret;
		}
		return Math.max(ret, 0);
	}

	@Override
	public int extract(int amt, TransactionContext tx) {
		int ret = (int)Math.min(amt, Math.min(energy, Integer.MAX_VALUE));
		if (ret > 0) {
			energyJournal.updateSnapshots(tx);
			energy -= ret;
		}
		return Math.max(ret, 0);
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	protected void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);

		NBT.putLong("e", energy);
	}

	@Override
	protected void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);

		energy = NBT.getLongOr("e", 0L);
	}

	@Override
	public int getEnergyColor() {
		return 0xff1111;
	}

	@Override
	public String getUnitName() {
		return "RF";
	}

	@Override
	public boolean isDecimalSystem() {
		return false;
	}

	@Override
	protected Item getPlacerItem() {
		return ElectriItems.RFBATTERY.get();
	}

	@Override
	protected void setEnergy(long val) {
		energy = val;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
