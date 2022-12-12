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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.libraries.level.ReikaWorldHelper;
import reika.electricraft.base.WiringTile;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityMeter extends WiringTile {

	public BlockEntityMeter(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.METER.get(), pos, state);
	}

	@Override
	public int getResistance() {
		return 0;
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public void onNetworkChanged() {
		super.onNetworkChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		ReikaWorldHelper.causeAdjacentUpdates(level, worldPosition);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.METER;
	}

	@Override
	public boolean canNetworkOnSide(Direction dir) {
		return dir != Direction.UP;
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	public int getRedstoneOverride() {
		long p = this.getWirePower();
		return p > 0 ? Math.max(1, Math.min(15, (int)(p/1048576))) : 0;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
