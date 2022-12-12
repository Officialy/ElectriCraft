/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import reika.electricraft.registry.ElectriItems;
import reika.rotarycraft.auxiliary.HandbookTracker;

public class ElectriBookTracker extends HandbookTracker {

	@Override
	public ItemStack getItem() {
		return ElectriItems.BOOK.get().getDefaultInstance();
	}

	@Override
	public String getID() {
		return "ElectriCraft_Handbook";
	}

}
