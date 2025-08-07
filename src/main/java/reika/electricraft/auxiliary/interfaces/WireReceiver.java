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

public interface WireReceiver extends WireTerminus {

	boolean canReceivePowerFromSide(Direction dir);

	boolean canReceivePower();

}
