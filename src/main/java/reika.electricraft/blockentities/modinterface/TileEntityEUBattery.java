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
//import net.minecraft.item.Item;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.util.Mth;
//
//import net.minecraftforge.common.MinecraftForge;
//
//
//import reika.dragonapi.modlist;
//import reika.dragonapi.asm.APIStripper.Strippable;
//import reika.dragonapi.asm.DependentMethodStripper.ModDependent;
//import reika.dragonapi.modinteract.ItemHandlers.IC2Handler;
//import reika.dragonapi.modinteract.power.ReikaEUHelper;
//import reika.electricraft.base.BatteryTileBase;
//import reika.electricraft.registry.ElectriItems;
//import reika.electricraft.registry.ElectriTiles;
//
//import ic2.api.energy.event.EnergyTileLoadEvent;
//import ic2.api.energy.event.EnergyTileUnloadEvent;
//import ic2.api.energy.tile.IEnergySink;
//import ic2.api.energy.tile.IEnergySource;
//
//@Strippable(value={"ic2.api.energy.tile.IEnergySink", "ic2.api.energy.tile.IEnergySource"})
//public class BlockEntityEUBattery extends BatteryTileBase implements IEnergySink, IEnergySource {
//
//	public static final double CAPACITY = 9e9; //MFSU is 40M, each tier is 10x in IC2
//	public static final double THROUGHPUT = 1048576;//65536*2; //MFSU is 2048, MFE is 512
//
//	private double energy;
//
//	@Override
//	public String getDisplayEnergy() {
//		return String.format("%.3f EU", energy);
//	}
//
//	@Override
//	public long getStoredEnergy() {
//		return (long)energy;
//	}
//
//	@Override
//	public long getMaxEnergy() {
//		return (long)CAPACITY;
//	}
//
//	@Override
//	public String getFormattedCapacity() {
//		return String.format("%d EU", this.getMaxEnergy());
//	}
//
//	@Override
//	protected void writeSyncTag(CompoundTag NBT) {
//		super.writeSyncTag(NBT);
//
//		NBT.setDouble("e", energy);
//	}
//
//	@Override
//	protected void readSyncTag(CompoundTag NBT) {
//		super.readSyncTag(NBT);
//
//		energy = NBT.getDouble("e");
//	}
//
//	@Override
//	public int getEnergyColor() {
//		return 0xffff00;
//	}
//
//	@Override
//	public boolean emitsEnergyTo(BlockEntity receiver, Direction dir) {
//		return dir == Direction.UP;
//	}
//
//	@Override
//	public boolean acceptsEnergyFrom(BlockEntity emitter, Direction dir) {
//		return dir != Direction.UP;
//	}
//
//	@Override
//	public ElectriTiles getMachine() {
//		return ElectriTiles.EUBATTERY;
//	}
//
//	@Override
//	public void updateEntity(Level world, BlockPos pos) {
//		super.updateEntity(world, pos);
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
//	@Override
//	public double getOfferedEnergy() {
//		return this.hasRedstoneSignal() ? Math.min(THROUGHPUT, energy) : 0;
//	}
//
//	@Override
//	public void drawEnergy(double amt) {
//		//if (amt > 0)
//		energy -= amt;
//		//ReikaJavaLibrary.pConsole("Drawing "+amt+" from "+this);
//		energy = Mth.clamp_double(energy, 0, CAPACITY);
//	}
//
//	@Override
//	public int getSourceTier() {
//		return IC2Handler.getInstance().isIC2Classic() ? ReikaEUHelper.getIC2TierFromEUVoltage(THROUGHPUT) : 4;
//	}
//
//	@Override
//	public double getDemandedEnergy() {
//		return CAPACITY-energy;
//	}
//
//	@Override
//	public int getSinkTier() {
//		return this.getSourceTier();
//	}
//
//	@Override
//	public double injectEnergy(Direction dir, double amt, double voltage) {
//		double add = Math.min(CAPACITY-energy, amt);
//		//ReikaJavaLibrary.pConsole("Injecting "+amt+" to "+this);
//		if (add > 0) {
//			energy += add;
//			return amt-add;
//		}
//		return 0;
//	}
//
//	@Override
//	public String getUnitName() {
//		return "EU";
//	}
//
//	@Override
//	public boolean isDecimalSystem() {
//		return true;
//	}
//
//	@Override
//	protected Item getPlacerItem() {
//		return ElectriItems.EUBATTERY.get();
//	}
//
//	@Override
//	protected void setEnergy(long val) {
//		energy = val;
//	}
//
//}
