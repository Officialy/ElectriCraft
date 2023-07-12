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
import net.minecraftforge.energy.IEnergyStorage;
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.electricraft.base.BatteryTileBase;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityRFBattery extends BatteryTileBase implements IEnergyStorage {

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
		if (world.getGameTime()%64 == 0) {
			world.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
		}

		if (!world.isClientSide() && this.hasRedstoneSignal()) {
			int exp = (int)Math.min(energy, Integer.MAX_VALUE);
			if (exp > 0) {
				BlockEntity te = this.getAdjacentBlockEntity(Direction.UP);
				if (te instanceof IEnergyStorage) {
					if (((IEnergyStorage)te).canReceive()) {
						if (te instanceof IEnergyStorage ier) {
							int added = ier.receiveEnergy(exp, false);
							energy -= added;
						}
						else if (te instanceof IEnergyStorage ieh) {
							int added = ieh.receiveEnergy(exp, false);
							energy -= added;
						}
					}
				}
			}
		}
	}
	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public int receiveEnergy(int amt, boolean simulate) {
		long ret = /*todo dir != Direction.UP ? */Math.min(amt, CAPACITY-energy);//todo : 0;
		if (ret > 0 && !simulate) {
			energy += ret;
		}
		return (int)ret;
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		long ret = /*todo dir == Direction.UP ? */Math.min(amt, energy);//todo : 0;
		if (ret > 0 && !simulate) {
			energy -= ret;
		}
		return (int)ret;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public int getEnergyStored() {
		return energy == this.getMaxEnergy() ? Integer.MAX_VALUE : (int)Math.min(energy, Integer.MAX_VALUE-1);
	}

	@Override
	public int getMaxEnergyStored() {
		return Integer.MAX_VALUE;
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

		energy = NBT.getLong("e");
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
