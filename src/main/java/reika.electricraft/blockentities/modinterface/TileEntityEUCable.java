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
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//import net.minecraftforge.common.MinecraftForge;
//
//
//import Reika.ChromatiCraft.API.Interfaces.WorldRift;
//import reika.dragonapi.modlist;
//import reika.dragonapi.asm.APIStripper.Strippable;
//import reika.dragonapi.asm.DependentMethodStripper.ModDependent;
//import reika.electricraft.base.ElectriCable;
//import reika.electricraft.registry.ElectriTiles;
//
//import ic2.api.energy.event.EnergyTileLoadEvent;
//import ic2.api.energy.event.EnergyTileUnloadEvent;
//import ic2.api.energy.tile.IEnergyAcceptor;
//import ic2.api.energy.tile.IEnergyConductor;
//import ic2.api.energy.tile.IEnergyEmitter;
//import ic2.api.energy.tile.IEnergyTile;
//
//@Strippable(value="ic2.api.energy.tile.IEnergyConductor")
//public class BlockEntityEUCable extends ElectriCable implements IEnergyConductor {
//
//	public static final double CAPACITY = Double.MAX_VALUE;//10e9;//Double.POSITIVE_INFINITY;
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
//	public boolean acceptsEnergyFrom(BlockEntity te, Direction dir) {
//		if (!(te instanceof IEnergyTile))
//			return false;
//		return true;
//	}
//
//	@Override
//	public boolean emitsEnergyTo(BlockEntity te, Direction dir) {
//		if (!(te instanceof IEnergyTile))
//			return false;
//		return true;
//	}
//
//	@Override
//	public double getConductionLoss() {
//		return 1e-12;
//	}
//
//	@Override
//	public double getInsulationEnergyAbsorption() {
//		return Double.POSITIVE_INFINITY;
//	}
//
//	@Override
//	public double getInsulationBreakdownEnergy() {
//		return CAPACITY;
//	}
//
//	@Override
//	public double getConductorBreakdownEnergy() {
//		return CAPACITY;
//	}
//
//	@Override
//	public void removeInsulation() {
//
//	}
//
//	@Override
//	public void removeConductor() {
//		this.delete();
//	}
//
//	@Override
//	public ElectriTiles getMachine() {
//		return ElectriTiles.EUCABLE;
//	}
//
//	@Override
//	public void updateEntity(Level world, BlockPos pos) {
//
//	}
//
//	@Override
//	protected void animateWithTick(Level world, BlockPos pos) {
//
//	}
//
//	@Override
//	protected boolean connectsToTile(BlockEntity te, Direction dir) {
//		if (te instanceof IEnergyEmitter && ((IEnergyEmitter)te).emitsEnergyTo(this, dir.getOpposite()))
//			return true;
//		if (te instanceof IEnergyAcceptor && ((IEnergyAcceptor)te).acceptsEnergyFrom(this, dir.getOpposite()))
//			return true;
//		if (te instanceof WorldRift)
//			return true;
//		return false;
//	}
//}
