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
//
//@ModTileDependent(value = "ic2.api.energy.tile.IEnergyTile")
//public class LuaGetEUCapacity extends LuaMethod {
//
//	public LuaGetEUCapacity() {
//		super("getFullCapacity", BlockEntityEUBattery.class);
//	}
//
//	@Override
//	protected Object[] invoke(BlockEntity te, Object[] args) throws LuaMethodException, InterruptedException {
//		return new Object[]{((BlockEntityEUBattery)te).getMaxEnergy()};
//	}
//
//	@Override
//	public String getDocumentation() {
//		return "Returns the EU capacity.\nArgs: None\nReturns: Capacity";
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
