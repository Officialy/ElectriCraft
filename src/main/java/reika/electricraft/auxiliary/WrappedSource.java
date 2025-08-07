/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;



import reika.electricraft.api.WrappableWireSource;
import reika.electricraft.auxiliary.interfaces.WireEmitter;
import reika.electricraft.network.WireNetwork;
import reika.rotarycraft.api.power.ShaftMerger;
import reika.rotarycraft.auxiliary.PowerSourceList;
import reika.rotarycraft.auxiliary.interfaces.PowerSourceTracker;


public final class WrappedSource implements WireEmitter, PowerSourceTracker {

	private final WrappableWireSource source;

	public WrappedSource(WrappableWireSource src) {
		source = src;
		if (!(src instanceof BlockEntity)) {
			throw new IllegalArgumentException("You cannot wrap non-tile sources!");
		}
		if (!src.getClass().getName().startsWith("Reika"))
			throw new IllegalArgumentException("This class is for internal use only!");
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {}
	@Override
	public WireNetwork getNetwork() {return null;}
	@Override
	public void setNetwork(WireNetwork n) {}
	@Override
	public void removeFromNetwork() {}
	@Override
	public void resetNetwork() {}
	@Override
	public void onNetworkChanged() {}

	@Override
	public boolean isConnectable() {
		return this.canEmitPower();
	}

	@Override
	public boolean canNetworkOnSide(Direction dir) {
		return this.canEmitPowerToSide(dir);
	}

	@Override
	public Level getWorld() {
		return ((BlockEntity)source).getLevel();
	}

	@Override
	public int getX() {
		return ((BlockEntity)source).getBlockPos().getX();
	}

	@Override
	public int getY() {
		return ((BlockEntity)source).getBlockPos().getY();
	}

	@Override
	public int getZ() {
		return ((BlockEntity)source).getBlockPos().getZ();
	}

	@Override
	public int getGenVoltage() {
		return source.getOmega()*WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public int getGenCurrent() {
		return source.getTorque()/WireNetwork.TORQUE_PER_AMP;
	}

	@Override
	public boolean canEmitPowerToSide(Direction dir) {
		return source.canConnectToSide(dir);
	}

	@Override
	public boolean canEmitPower() {
		return source.isFunctional();
	}

	public boolean needsUpdate() {
		return source.hasPowerStatusChangedSinceLastTick();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WrappedSource && ((WrappedSource)o).source.equals(source);
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	@Override
	public PowerSourceList getPowerSources(PowerSourceTracker io, ShaftMerger caller) {
		return source.getPowerSources(io, caller);
	}

	@Override
	public void getAllOutputs(Collection<BlockEntity> c, Direction dir) {
		source.getAllOutputs(c, dir);
	}

	@Override
	public BlockPos getIoOffsetPos() {
		return source.getIoOffsetPos();
	}

}
