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
import reika.electricraft.base.WiringTile;

public class LuaGetVoltage extends LuaMethod {

	public LuaGetVoltage() {
		super("getVoltage", WiringTile.class);
	}

	@Override
	protected Object[] invoke(BlockEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		return new Object[]{((WiringTile)te).getWireVoltage()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the wire voltage.";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
