/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.api;

import net.minecraft.core.Direction;
import reika.rotarycraft.api.power.ShaftMachine;
import reika.rotarycraft.auxiliary.interfaces.PowerSourceTracker;

public interface WrappableWireSource extends ShaftMachine, PowerSourceTracker {

	public boolean canConnectToSide(Direction dir);

	public boolean isFunctional();

	public boolean hasPowerStatusChangedSinceLastTick();

}
