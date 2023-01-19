///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft.tileentities.ModInterface;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//import net.minecraftforge.common.MinecraftForge;
//
//
//import reika.dragonapi.modlist;
//import reika.dragonapi.asm.APIStripper.Strippable;
//import reika.dragonapi.asm.DependentMethodStripper.ModDependent;
//import reika.dragonapi.libraries.level.ReikaWorldHelper;
//import reika.dragonapi.modinteract.ItemHandlers.IC2Handler;
//import reika.dragonapi.modinteract.power.ReikaEUHelper;
//import reika.electricraft.base.ElectriBlockEntity;
//import reika.electricraft.registry.ElectriTiles;
//import reika.rotarycraft.api.interfaces.Screwdriverable;
//
//import ic2.api.energy.event.EnergyTileLoadEvent;
//import ic2.api.energy.event.EnergyTileUnloadEvent;
//import ic2.api.energy.tile.IEnergySink;
//import ic2.api.energy.tile.IEnergySource;
//
//@Strippable(value = {"ic2.api.energy.tile.IEnergySink", "ic2.api.energy.tile.IEnergySource"})
//public class BlockEntityEUSplitter extends ElectriBlockEntity implements IEnergySource, IEnergySink, Screwdriverable {
//
//	private boolean[] out = new boolean[6];
//	private int split;
//	private double EUin;
//	private double EUout;
//	private int tierout;
//
//	private Direction facing;
//
//	private void calculate() {
//		int s = 0;
//		for (int i = 0; i < 6; i++) {
//			Direction dir = dirs[i];
//			if (this.emitsTo(dir)) {
//				s++;
//			}
//		}
//		split = s;
//		EUout = split > 0 ? EUin/split : 0;
//		tierout = ReikaEUHelper.getIC2TierFromEUVoltage(EUout);
//	}
//
//	@Override
//	public void updateEntity(Level world, BlockPos pos) {
//		if (!world.isClientSide()) {
//			this.calculate();
//			EUin = 0; //clear for next tick
//		}
//	}
//
//	@Override
//	public boolean emitsEnergyTo(BlockEntity receiver, Direction direction) {
//		return this.emitsTo(direction);
//	}
//
//	@Override
//	public boolean acceptsEnergyFrom(BlockEntity emitter, Direction direction) {
//		return direction == this.getFacing();
//	}
//
//	@Override
//	public double getDemandedEnergy() {
//		return Double.MAX_VALUE;
//	}
//
//	@Override
//	public int getSinkTier() {
//		return IC2Handler.getInstance().isIC2Classic() ? 13 : Integer.MAX_VALUE;
//	}
//
//	@Override
//	public double injectEnergy(Direction directionFrom, double amount, double voltage) {
//		EUin = amount;
//		return amount;
//	}
//
//	@Override
//	public double getOfferedEnergy() {
//		return EUout;
//	}
//
//	@Override
//	public void drawEnergy(double amount) {
//
//	}
//
//	@Override
//	public int getSourceTier() {
//		return tierout;
//	}
//
//	@Override
//	public ElectriTiles getMachine() {
//		return ElectriTiles.EUSPLIT;
//	}
//
//	@Override
//	protected void animateWithTick(Level world, BlockPos pos) {
//
//	}
//
//	@Override
//	public boolean onShiftRightClick(Level world, int x, int y, int z, Direction side) {
//		if (!world.isClientSide() && ModList.IC2.isLoaded())
//			this.removeTileFromNet();
//		out[side.ordinal()] = !out[side.ordinal()];
//		world.markBlockForUpdate(x, y, z);
//		ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
//		if (!world.isClientSide() && ModList.IC2.isLoaded())
//			this.addTileToNet();
//		return true;
//	}
//
//	@Override
//	public boolean onRightClick(Level world, int x, int y, int z, Direction side) {
//		if (!world.isClientSide() && ModList.IC2.isLoaded())
//			this.removeTileFromNet();
//		facing = side;
//		world.markBlockForUpdate(x, y, z);
//		ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
//		if (!world.isClientSide() && ModList.IC2.isLoaded())
//			this.addTileToNet();
//		return true;
//	}
//
//	public boolean emitsTo(Direction s) {
//		return this.emitsTo(s.ordinal());
//	}
//
//	public boolean emitsTo(int s) {
//		return out[s] && s != this.getFacing().ordinal();
//	}
//
//	public Direction getFacing() {
//		return facing != null ? facing : Direction.EAST;
//	}
//
//	@Override
//	protected void readSyncTag(CompoundTag NBT) {
//		super.readSyncTag(NBT);
//
//		facing = dirs[NBT.getInt("face")];
//
//		for (int i = 0; i < 6; i++) {
//			out[i] = NBT.getBoolean("emit"+i);
//		}
//
//		split = NBT.getInt("split");
//		tierout = NBT.getInt("tierout");
//		EUin = NBT.getDouble("in");
//		EUout = NBT.getDouble("out");
//	}
//
//	@Override
//	protected void writeSyncTag(CompoundTag NBT) {
//		super.writeSyncTag(NBT);
//
//		NBT.putInt("face", this.getFacing().ordinal());
//
//		NBT.putInt("split", split);
//		NBT.putInt("tierout", tierout);
//		NBT.setDouble("in", EUin);
//		NBT.setDouble("out", EUout);
//
//		for (int i = 0; i < 6; i++) {
//			NBT.putBoolean("emit"+i, out[i]);
//		}
//	}
//
//	@Override
//	public void onFirstTick(Level world, int x, int y, int z) {
//		if (!world.isClientSide() && ModList.IC2.isLoaded())
//			this.addTileToNet();
//	}
//
//	@ModDependent(ModList.IC2)
//	private void addTileToNet() {
//		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
//	}
//
//	@Override
//	protected void onInvalidateOrUnload(Level world, int x, int y, int z, boolean invalidate) {
//		if (!world.isClientSide() && ModList.IC2.isLoaded())
//			this.removeTileFromNet();
//	}
//
//	@ModDependent(ModList.IC2)
//	private void removeTileFromNet() {
//		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
//	}
//
//}
