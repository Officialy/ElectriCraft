/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.registry;

import java.util.Locale;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;

import reika.dragonapi.libraries.registry.ReikaItemHelper;

public enum ElectriCrafting {

	BLUEDUST("dustLapis"),
	DIAMONDDUST("dustDiamond"),
	QUARTZDUST("dustNetherQuartz"),
	CRYSTALDUST();

	public static final ElectriCrafting[] craftingList = values();

	public final String oreDictName;

	ElectriCrafting() {
		this(null);
	}

	ElectriCrafting(String dict) {
		oreDictName = dict;
	}

	public ItemStack getItem() {
		return ElectriItems.CRAFTING.get().getDefaultInstance();//.getStackOfMetadata(this.ordinal());
	}

	public boolean hasOreName() {
		return oreDictName != null && !oreDictName.isEmpty();
	}

	public String getName() {
		return I18n.get("electricrafting."+this.name().toLowerCase(Locale.ENGLISH));
	}

	public ItemStack getItem(int amt) {
		return ReikaItemHelper.getSizedItemStack(this.getItem(), amt);
	}
}
