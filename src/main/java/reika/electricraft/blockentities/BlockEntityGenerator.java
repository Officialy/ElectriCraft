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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;


import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.electricraft.auxiliary.interfaces.ConversionTile;
import reika.electricraft.base.ElectricalEmitter;
import reika.electricraft.network.WireNetwork;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;
import reika.rotarycraft.api.interfaces.Screwdriverable;

import reika.rotarycraft.api.power.PowerTransferHelper;
import reika.rotarycraft.api.power.ShaftMerger;

import reika.rotarycraft.api.power.ShaftPowerReceiver;
import reika.rotarycraft.auxiliary.PowerSourceList;
import reika.rotarycraft.auxiliary.interfaces.PowerSourceTracker;

public class BlockEntityGenerator extends ElectricalEmitter implements Screwdriverable, ShaftPowerReceiver, ConversionTile, PowerSourceTracker {

	private int lastomega;
	private int lasttorque;

	protected int omega;
	protected int torque;
	protected long power;

	protected int iotick;

	private Direction facing;

	public BlockEntityGenerator(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.GENERATOR.get(), pos, state);
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
		super.updateEntity(world, pos);

		if (iotick > 0)
			iotick -= 8;

		if (!PowerTransferHelper.checkPowerFrom(this, this.getFacing())) {
			this.noInputMachine();
		}

		if (power == 0 || omega == 0 || torque == 0) {
			power = 0;
			omega = torque = 0;
		}

		if (power > 400e6 && !world.isClientSide()) {
			this.delete();
			world.explode(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 4, true, Level.ExplosionInteraction.BLOCK);
		}

		//ReikaJavaLibrary.pConsole(network, Dist.DEDICATED_SERVER);
		if (!world.isClientSide() && network != null) {
			//ReikaJavaLibrary.pConsole(omega);
			if (omega != lastomega || torque != lasttorque) {
				network.updateWires();
			}
		}

		lastomega = omega;
		lasttorque = torque;
	}

	public final Direction getFacing() {
		return facing != null ? facing : Direction.EAST;
	}

	public void setFacing(Direction dir) {
		facing = dir;
	}

	@Override
	public final int getOmega() {
		return omega;
	}

	@Override
	public final int getTorque() {
		return torque;
	}

	@Override
	public final long getPower() {
		return power;
	}

	@Override
	public final int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public final void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInt("face")];

		omega = NBT.getInt("omg");
		torque = NBT.getInt("tq");
		power = NBT.getLong("pwr");

		iotick = NBT.getInt("io");
	}

	@Override
	public void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);

		NBT.putInt("face", this.getFacing().ordinal());

		NBT.putInt("omg", omega);
		NBT.putInt("tq", torque);
		NBT.putLong("pwr", power);

		NBT.putInt("io", iotick);
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omega+1, 2), 1.05);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.GENERATOR;
	}

	@Override
	public int getGenVoltage() {
		return omega*WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public int getGenCurrent() {
		return torque/WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public boolean canNetworkOnSide(Direction dir) {
		return dir == this.getFacing().getOpposite();
	}

	@Override
	public void setOmega(int omega) {
		this.omega = omega;
	}

	@Override
	public void setTorque(int torque) {
		this.torque = torque;
	}

	@Override
	public void setPower(long power) {
		this.power = power;
	}

	@Override
	public boolean canReadFrom(Direction from) {
		Direction dir = this.getFacing();
		return from == dir;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public void noInputMachine() {
		omega = torque = 0;
		power = 0;
	}

	@Override
	public boolean canEmitPowerToSide(Direction dir) {
		return this.canNetworkOnSide(dir);
	}

	@Override
	public boolean onShiftRightClick(Level world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public boolean onRightClick(Level world, BlockPos pos, Direction side) {
		this.incrementFacing();
		return true;
	}

	protected void incrementFacing() {
		int o = this.getFacing().ordinal();
		if (o == 5)
			this.setFacing(dirs[2]);
		else
			this.setFacing(dirs[o+1]);
		this.rebuildNetwork();
	}

	@Override
	public boolean canEmitPower() {
		return true;
	}

	@Override
	public int getMinTorque(int available) {
		return torque;
	}

	@Override
	public PowerSourceList getPowerSources(PowerSourceTracker io, ShaftMerger caller) {
		Direction dir = this.getFacing();
		return PowerSourceList.getAllFrom(level, dir, new BlockPos(worldPosition.getX()+dir.getStepX(), worldPosition.getY()+dir.getStepY(), worldPosition.getZ()+dir.getStepZ()), this, caller);
	}

	@Override
	public BlockPos getIoOffsetPos() {
		return null;
	}

	@Override
	public void getAllOutputs(Collection<BlockEntity> c, Direction dir) {

	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
