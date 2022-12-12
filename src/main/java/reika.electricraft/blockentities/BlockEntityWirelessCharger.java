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
import java.util.Locale;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.base.ElectriBlockEntity;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;
import reika.rotarycraft.registry.RotaryBlocks;
import reika.rotarycraft.registry.RotaryItems;

public class BlockEntityWirelessCharger extends ElectriBlockEntity implements IEnergyStorage {
	public BlockEntityWirelessCharger(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.WIRELESS_CHARGER.get(), pos, state);
	}

	//private Direction facing = Direction.UP;

	public Direction getFacing() {
		//return facing != null ? facing : Direction.UP;
		return switch (getBlockState().getValue(ElectriBlock.FACING)) {
			case WEST -> Direction.WEST;
			case EAST -> Direction.EAST;
			case NORTH -> Direction.NORTH;
			case SOUTH -> Direction.SOUTH;
			case UP -> Direction.UP;
			case DOWN -> Direction.DOWN;
		};
	}

	private IEnergyStorage getBlockEntity() {
		Direction dir = this.getFacing();
		int x = worldPosition.getX()+dir.getStepX()*2;
		int y = worldPosition.getY()+dir.getStepY()*2;
		int z = worldPosition.getZ()+dir.getStepZ()*2;
		BlockEntity te = level.getBlockEntity(new BlockPos(x, y, z));
		return te instanceof IEnergyStorage ? (IEnergyStorage)te : null;
	}

	public ChargerTiers getTier() {
		return ChargerTiers.tierList[1]; //todo tiers
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.WIRELESSPAD;
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {

	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	protected void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);

		//NBT.putInt("dir", this.getFacing().ordinal());
	}

	@Override
	protected void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);

		//facing = dirs[NBT.getInt("facing")];
	}

	//todo this
	public boolean canConnectEnergy(Direction from) {
		return from != this.getFacing();
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		IEnergyStorage ier = this.getBlockEntity();
		float f = this.getTier().efficiency;
		maxReceive = Math.min(maxReceive, this.getTier().maxThroughput);
		return ier != null ? (int)(ier.receiveEnergy((int)(maxReceive*f), simulate)/f) : 0; //this.getFacing().getOpposite(),  was first argument
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}


	@Override
	public int getEnergyStored() {
		IEnergyStorage ier = this.getBlockEntity();
		return ier != null ? ier.getEnergyStored() : 0;
	}

	@Override
	public int getMaxEnergyStored() {
		IEnergyStorage ier = this.getBlockEntity();
		return ier != null ? ier.getMaxEnergyStored() : 0;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}

	public static enum ChargerTiers {
		BASIC(0.4F, 80),
		IMPROVED(0.6F, 800),
		ADVANCED(0.8F, 6000),
		HIGHTECH(0.9F, 40000),
		SUPERCONDUCTING(1F, Integer.MAX_VALUE);

		public final float efficiency;
		public final int maxThroughput;

		public static final ChargerTiers[] tierList = values();

		ChargerTiers(float f, int th) {
			efficiency = f;
			maxThroughput = th;
		}

		public Object[] getRecipe() {
			Object[] sides = {Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, RotaryBlocks.BLASTGLASS.get(), RotaryBlocks.BLASTGLASS.get()};
			Object[] cores = {Items.REDSTONE, Items.GOLD_INGOT, Items.DIAMOND, RotaryItems.INDUCTIVE_INGOT, RotaryItems.ENDERIUM.get()};
			Object[] ret = {"AEA", "SCS", "PRP", 'A', RotaryItems.HSLA_STEEL_INGOT, 'E', Items.ENDER_PEARL, 'P', RotaryItems.HSLA_PLATE, 'R', RotaryItems.SILICON.get(), 'S', sides[this.ordinal()], 'C', cores[this.ordinal()]};
			return ret;
		}

		public String getLocalizedName() {
			return I18n.get("electrichargepad."+this.name().toLowerCase(Locale.ENGLISH));
		}

		public static String getDataForDisplay() {
			StringBuilder sb = new StringBuilder();
			for (ChargerTiers type : tierList) {
				if (type == ChargerTiers.SUPERCONDUCTING)
					sb.append(type.getLocalizedName()).append(": No limit @ 100% efficiency");
				else
					sb.append(String.format("%s: Max %d RF/t @ %.0f%% efficiency", type.getLocalizedName(), type.maxThroughput, 100 * type.efficiency));
				sb.append("\n\n");
			}
			return sb.toString();
		}
	}

}
