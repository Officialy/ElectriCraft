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

public class LuaGetCurrent extends LuaMethod {

	public LuaGetCurrent() {
		super("getCurrent", WiringTile.class);
	}

	@Override
	protected Object[] invoke(BlockEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		return new Object[]{((WiringTile)te).getWireCurrent()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the wire current.";
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
