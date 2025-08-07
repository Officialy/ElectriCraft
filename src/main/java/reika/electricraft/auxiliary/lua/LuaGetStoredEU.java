///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft.auxiliary.lua;
//
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//import reika.dragonapi.modinteract.lua.LuaMethod;
//import reika.dragonapi.modinteract.lua.LuaMethod.ModTileDependent;
//import reika.electricraft.tileentities.ModInterface.BlockEntityEUBattery;
//
//@ModTileDependent(value = "ic2.api.energy.tile.IEnergyTile")
//public class LuaGetStoredEU extends LuaMethod {
//
//	public LuaGetStoredEU() {
//		super("getFullStoredEnergy", BlockEntityEUBattery.class);
//	}
//
//	@Override
//	protected Object[] invoke(BlockEntity te, Object[] args) throws LuaMethodException, InterruptedException {
//		return new Object[]{((BlockEntityEUBattery)te).getStoredEnergy()};
//	}
//
//	@Override
//	public String getDocumentation() {
//		return "Returns the stored EU value.\nArgs: None\nReturns: Energy";
//	}
//
//	@Override
//	public String getArgsAsString() {
//		return "";
//	}
//
//	@Override
//	public ReturnType getReturnType() {
//		return ReturnType.LONG;
//	}
//
//}
