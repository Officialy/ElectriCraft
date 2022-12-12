/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.items;

import net.minecraft.world.item.ItemStack;

import reika.electricraft.base.ElectriItemBase;
import reika.electricraft.registry.BatteryType;


public class ItemEnergyCrystal extends ElectriItemBase {

	public ItemEnergyCrystal(Properties properties) {
		super(properties);
	}

/*	@Override
	public boolean hasEffect(ItemStack is) {
		return is.getItemDamage() >= BatteryType.batteryList.length-1;
	}*/

}
