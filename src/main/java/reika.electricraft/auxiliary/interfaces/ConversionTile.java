/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary.interfaces;

import net.minecraft.core.Direction;
import reika.rotarycraft.api.power.ShaftMachine;

public interface ConversionTile extends ShaftMachine {

	public void setFacing(Direction dir);

	public Direction getFacing();

}
