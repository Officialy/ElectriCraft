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

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;

public class BlockEntityResistor extends BlockEntityResistorBase {

	/** Use these to control current limits; change by right-clicking with dye */
	private ColorBand[] bands = {ColorBand.BLACK, ColorBand.BLACK, ColorBand.BLACK};

	public BlockEntityResistor(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.RESISTOR.get(), pos, state);
	}

	@Override
	protected int calculateCurrentLimit() {
		int digit1 = bands[0].ordinal();
		int digit2 = bands[1].ordinal();
		int multiplier = bands[2].ordinal();
		int base = Integer.parseInt(String.format("%d%d", digit1, digit2));
		return base*ReikaMathLibrary.intpow2(10, multiplier);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.RESISTOR;
	}

	@Override
	public void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	public void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);
	}

	@Override
	public ColorBand[] getColorBands() {
		return bands;
	}

	@Override
	protected void setColorBands(ColorBand[] bands) {
		this.bands = bands;
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

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
