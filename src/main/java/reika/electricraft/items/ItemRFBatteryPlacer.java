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

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemStack;


import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;

public class ItemRFBatteryPlacer extends ItemBatteryPlacer {

	public ItemRFBatteryPlacer(Properties properties) {
		super(properties);
	}

	public void getSubItems(Item item, CreativeModeTab tab, List li) {
		li.add(new ItemStack(item));
		ItemStack is = new ItemStack(item);
		is.getOrCreateTag().putLong("nrg", BlockEntityRFBattery.CAPACITY);
		li.add(is);
	}

	@Override
	protected Block getPlacingBlock() {
		return ElectriBlocks.RFBATTERY.get();
	}


	@Override
	public void appendHoverText(ItemStack is,  Level p_41422_, List<Component> li, TooltipFlag p_41424_) {
		long e = 0;
		if (is.getTag() != null) {
			e = is.getTag().getLong("nrg");
		}
		long max = BlockEntityRFBattery.CAPACITY;
		String sg = ReikaEngLibrary.getSIPrefix(e);
		String sg2 = ReikaEngLibrary.getSIPrefix(max);
		double b = ReikaMathLibrary.getThousandBase(e);
		double b2 = ReikaMathLibrary.getThousandBase(max);
		li.add(Component.literal(String.format("Stored Energy: %.2f %sRF/%.2f %sRF", b, sg, b2, sg2)));
	}
}
