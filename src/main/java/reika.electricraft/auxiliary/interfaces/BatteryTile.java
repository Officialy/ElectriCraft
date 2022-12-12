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

import net.minecraft.world.item.ItemStack;
import reika.electricraft.auxiliary.BatteryTracker;

public interface BatteryTile {

	 String getDisplayEnergy();
	 long getStoredEnergy();

	 long getMaxEnergy();
	 String getFormattedCapacity();

	 void setEnergyFromNBT(ItemStack is);

	 int getEnergyColor();
	 String getUnitName();
	 boolean isDecimalSystem();

	 BatteryTracker getTracker();

}
