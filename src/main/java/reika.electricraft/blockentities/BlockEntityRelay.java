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
import reika.electricraft.auxiliary.interfaces.ToggledConnection;
import reika.electricraft.base.BlockEntityWireComponent;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityRelay extends BlockEntityWireComponent implements ToggledConnection {

	private boolean lastPower;

	public BlockEntityRelay(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.RELAY.get(), pos, state);
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
		super.updateEntity(world, pos);

		if (!world.isClientSide())
			network.toString();

		boolean pwr = this.hasRedstoneSignal();
		if (!world.isClientSide())
			if (pwr != lastPower)
				network.updateWires();
		lastPower = pwr;
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	public int getResistance() {
		return this.isEnabled() ? 0 : Integer.MAX_VALUE;
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public void onNetworkChanged() {

	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.RELAY;
	}

	public boolean isEnabled() {
		return this.hasRedstoneSignal();
	}

	@Override
	public float getHeight() {
		return 0.75F;
	}

	@Override
	public float getWidth() {
		return 0.75F;
	}

	@Override
	public boolean canConnect() {
		return this.isEnabled();
	}

	@Override
	public boolean onShiftRightClick(Level world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
