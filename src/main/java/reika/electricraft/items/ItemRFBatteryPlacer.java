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

	public void getSubItems(Item item, CreativeModeTab tab, List<ItemStack> li) {
		li.add(new ItemStack(item));
		ItemStack is = new ItemStack(item);
		// 1.21.5: getOrCreateTag/setTag removed; persist via CUSTOM_DATA helper.
		reika.dragonapi.libraries.registry.ReikaItemHelper.updateStackTag(is, __T__ -> __T__.putLong("nrg", BlockEntityRFBattery.CAPACITY));
		li.add(is);
	}

	@Override
	protected Block getPlacingBlock() {
		return ElectriBlocks.RFBATTERY.get();
	}

	// 1.21.5: Item.appendHoverText now takes (ItemStack, TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag).
	@Override
	public void appendHoverText(ItemStack is, net.minecraft.world.item.Item.TooltipContext ctx, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> li, TooltipFlag flag) {
		long e = 0;
		net.minecraft.nbt.CompoundTag tag = reika.dragonapi.libraries.registry.ReikaItemHelper.getStackTag(is);
		if (tag != null) {
			e = tag.getLongOr("nrg", 0L);
		}
		long max = BlockEntityRFBattery.CAPACITY;
		String sg = ReikaEngLibrary.getSIPrefix(e);
		String sg2 = ReikaEngLibrary.getSIPrefix(max);
		double b = ReikaMathLibrary.getThousandBase(e);
		double b2 = ReikaMathLibrary.getThousandBase(max);
		li.accept(Component.literal(String.format("Stored Energy: %.2f %sRF/%.2f %sRF", b, sg, b2, sg2)));
	}
}
