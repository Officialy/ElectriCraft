/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.libraries.level.ReikaWorldHelper;
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.electricraft.auxiliary.BatteryTracker;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
import reika.electricraft.auxiliary.interfaces.WireEmitter;
import reika.electricraft.auxiliary.interfaces.WireReceiver;
import reika.electricraft.base.NetworkBlockEntity;
import reika.electricraft.registry.BatteryType;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityBattery extends NetworkBlockEntity implements WireEmitter, WireReceiver, BatteryTile {

	private long energy;
	private long lastE;
	private boolean lastPower;

	private final BatteryTracker tracker = new BatteryTracker();

	public BlockEntityBattery(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.BATTERY.get(), pos, state);
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
		super.updateEntity(world, pos);

		tracker.update(this);

		if (!world.isClientSide() && network != null) {/*
			if ((world.getGameTime()&31) == 0) { what was this for
				network.updateWires();
			}*/

			if (world.getGameTime()%64 == 0) {
				ReikaWorldHelper.causeAdjacentUpdates(world, pos);
			}

			if (this.canReceivePower()) {
				int v = network.getTerminalVoltage(this);
				int a = network.getTerminalCurrent(this);
				long p = (long)v*(long)a;
				if (energy/20 < this.getMaxEnergy())
					energy += p;
				if (energy/20 >= this.getMaxEnergy()) {
					energy = this.getMaxEnergy()*20;
					network.updateWires();
				}
			}
			boolean flag = this.hasRedstoneSignal();
			if (flag != lastPower) {
				network.updateWires();
			}
			if (this.canEmitPower()) {
				int v = this.getGenVoltage();
				int a = this.getGenCurrent();
				long p = (long)v*(long)a;
				energy -= p;
				if (energy <= 0) {
					energy = 0;
					network.updateWires();
				}
				else if (lastE == 0) {
					network.updateWires();
				}
			}
			lastPower = flag;
			lastE = energy;
		}
	}

	@Override
	public boolean canEmitPower() {
		return energy > 0 && this.hasRedstoneSignal() && network.getNumberPathsStartingAt(this) > 0;
	}

	private long getGenPower() {
		return (long)this.getGenCurrent()*(long)this.getGenVoltage();
	}

	public long getStoredEnergy() {
		return energy/20;
	}

	public long getMaxEnergy() {
		return this.getBatteryType().maxCapacity;
	}

	public BatteryType getBatteryType() {
		return BatteryType.batteryList[1]; //todo battery type old meta code
	}

	public String getDisplayEnergy() {
		long e = this.getStoredEnergy();
		String pre = ReikaEngLibrary.getSIPrefix(e);
		double b = ReikaMathLibrary.getThousandBase(e);
		return String.format("%.3f%sJ", b, pre);
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public boolean canNetworkOnSide(Direction dir) {
		return true;
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.BATTERY;
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT)
	{
		super.writeSyncTag(NBT);

		NBT.putLong("e", energy);
	}

	@Override
	protected void readSyncTag(CompoundTag NBT)
	{
		super.readSyncTag(NBT);

		energy = NBT.getLong("e");
	}

	@Override
	public boolean canReceivePowerFromSide(Direction dir) {
		return dir != Direction.UP;
	}

	@Override
	public boolean canEmitPowerToSide(Direction dir) {
		return dir == Direction.UP;
	}

	@Override
	public int getGenVoltage() {
		return this.canEmitPower() ? this.getBatteryType().outputVoltage : 0;
	}

	@Override
	public int getGenCurrent() {
		return this.canEmitPower() ? this.getBatteryType().outputCurrent : 0;
	}

	public void setEnergyFromNBT(ItemStack is) {
		if (is.getItem() == ElectriItems.BATTERY.get()) {
			if (is.getTag() != null)
				energy = is.getTag().getLong("nrg")*20L;
			else
				energy = 0;
		}
		else {
			energy = 0;
		}
	}

	@Override
	public boolean canReceivePower() {
		return energy < this.getMaxEnergy()*20;
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	public int getRedstoneOverride() {
		return (int)(15D*this.getStoredEnergy()/this.getMaxEnergy());
	}

	@Override
	public String getFormattedCapacity() {
		return this.getBatteryType().getFormattedCapacity();
	}

	@Override
	public int getEnergyColor() {
		return 0xffffff;
	}

	@Override
	public BatteryTracker getTracker() {
		return tracker;
	}

	@Override
	public String getUnitName() {
		return "J";
	}

	@Override
	public boolean isDecimalSystem() {
		return false;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
