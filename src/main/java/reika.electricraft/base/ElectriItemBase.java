/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.base;

import java.util.List;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reika.electricraft.ElectriCraft;
import reika.electricraft.registry.ElectriItems;
public class ElectriItemBase extends Item /*implements IndexedItemSprites*/ {

	public ElectriItemBase(Properties properties) {
		super(properties);
//		this.setCreativeTab(ElectriCraft.tabElectri);
//		hasSubtypes = true;
//		this.setMaxDamage(0);
	}

/*	public void getSubItems(Item ID, CreativeModeTab cr, List li)
	{
		ElectriItems ri = ElectriItems.getEntryByID(ID);
		for (int i = 0; i < this.getDataValues(); i++) {
			ItemStack item = new ItemStack(ID, 1);
			if (ri.isAvailableInCreative(item))
				li.add(item);
		}
	}

	public final int getDataValues() {
		ElectriItems i = ElectriItems.getEntryByID(this);
		if (i == null)
			return 0;
		return i.getNumberMetadatas();
	}*/

/*	public String getItemStackDisplayName(ItemStack is) {
		ElectriItems ir = ElectriItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}*/

}
