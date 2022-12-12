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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WiringTile extends NetworkBlockEntity {

	private int voltage;
	private int current;

	public WiringTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public abstract int getResistance();

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	protected void onJoinNetwork() {
		current = network.getPointCurrent(this);
		voltage = network.getPointVoltage(this);
	}

	@Override
	public void onNetworkChanged() {
		current = network.getPointCurrent(this);
		voltage = network.getPointVoltage(this);
	}

	public final int getWireVoltage() {
		return voltage;
	}

	public final int getWireCurrent() {
		return current;
	}

	public final long getWirePower() {
		return (long)current*(long)voltage;
	}

	@Override
	protected void readSyncTag(CompoundTag NBT)
	{
		super.readSyncTag(NBT);

		voltage = NBT.getInt("v");
		current = NBT.getInt("a");
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT)
	{
		super.writeSyncTag(NBT);

		NBT.putInt("a", current);
		NBT.putInt("v", voltage);
	}

}
