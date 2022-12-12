/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary.lua;

import net.minecraft.world.level.block.entity.BlockEntity;

import reika.dragonapi.modinteract.lua.LuaMethod;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;

public class LuaGetLongStoredRF extends LuaMethod {

	public LuaGetLongStoredRF() {
		super("getFullStoredEnergy", BlockEntityRFBattery.class);
	}

	@Override
	protected Object[] invoke(BlockEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		return new Object[]{((BlockEntityRFBattery)te).getStoredEnergy()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the real stored RF value.\nArgs: None\nReturns: Energy";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.LONG;
	}

}
