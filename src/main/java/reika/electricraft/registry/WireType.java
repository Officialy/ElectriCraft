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

import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import reika.dragonapi.libraries.java.ReikaStringParser;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.dragonapi.modregistry.ModOreList;
import reika.electricraft.auxiliary.ElectriStacks;
import reika.rotarycraft.registry.RotaryItems;

public enum WireType {

	//Materials are suppliers: this enum classloads during datagen/registration, when building a
	//live ItemStack crashes ("Components not bound yet").
	STEEL(			16, 				64, () -> RotaryItems.HSLA_STEEL_INGOT.get().getDefaultInstance()),
	TIN(			64, 				32, () -> ElectriStacks.tinIngot, ModOreList.TIN),
	NICKEL(			256, 				16, () -> ElectriStacks.nickelIngot, ModOreList.NICKEL),
	ALUMINUM(		1024, 				8, 	() -> ElectriStacks.aluminumIngot, ModOreList.ALUMINUM),
	COPPER(			4096, 				2, 	() -> ElectriStacks.copperIngot, ModOreList.COPPER),
	SILVER(			32768, 				1, 	() -> ElectriStacks.silverIngot, ModOreList.SILVER),
	GOLD(			65536, 				4, 	() -> new ItemStack(Items.GOLD_INGOT)),
	PLATINUM(		131072, 			16, () -> ElectriStacks.platinumIngot, ModOreList.PLATINUM),
	SUPERCONDUCTOR(	Integer.MAX_VALUE, 	0, 	null);

	private final java.util.function.Supplier<ItemStack> material;
	private final String[] oreTypes;

	public final int maxCurrent;
	public final int resistance;

	public static final int INS_OFFSET = 16;

	public static final WireType[] wireList = values();

	WireType(int max, int res, java.util.function.Supplier<ItemStack> mat, ModOreList... ores) {
		material = mat;
		resistance = res;
		maxCurrent = max;
		ArrayList<String> li = new ArrayList<>();
		if (ores != null) {
			for (int i = 0; i < ores.length; i++) {
				String s = ores[i].getProductOreDictName();
				li.add(s);
			}
		}
		oreTypes = new String[li.size()];
		for (int i = 0; i < li.size(); i++) {
			oreTypes[i] = li.get(i);
		}
	}

	public static WireType getTypeFromWireDamage(int dmg) {
		return wireList[dmg%WireType.INS_OFFSET];
	}

	public String getIconTexture() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	/** Every conductor/insulation pair has its own modern block and BlockItem identity. */
	public ItemStack getCraftedProduct() {
		return new ItemStack(ElectriBlocks.getWireBlock(this, false).get());
	}

	public ItemStack getCraftedInsulatedProduct() {
		return new ItemStack(ElectriBlocks.getWireBlock(this, true).get());
	}

	private ArrayList<ItemStack> getAllValidCraftingIngots() {
		ArrayList<ItemStack> li = new ArrayList<>();
		li.add(material.get());
		for (String s : oreTypes) {
			ArrayList<ItemStack> li2 = null;//OreDictionary.getOres(s);
			for (ItemStack is2 : li2) {
				if (!ReikaItemHelper.collectionContainsItemStack(li, is2))
					li.add(is2);
			}
		}
		return li;
	}

	public void addCrafting() {
		if (material == null) {
        }
		/*int amt = DifficultyEffects.PIPECRAFT.getInt();
		ItemStack is = ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), amt);
		ItemStack is2 = ReikaItemHelper.getSizedItemStack(this.getCraftedInsulatedProduct(), amt);
		ArrayList<ItemStack> li = this.getAllValidCraftingIngots();
		for (ItemStack in : li) {
			Object[] obj2 = {"WIW", "WIW", "WIW", 'W', Blocks.WOOL.white(), 'I', in};
			Object[] obj = {"I", "I", "I", 'I', in};
			WorktableRecipes.getInstance().addRecipe(is, RecipeLevel.CORE, obj);
			WorktableRecipes.getInstance().addRecipe(is2, RecipeLevel.CORE, obj2);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(is, obj);
				GameRegistry.addRecipe(is2, obj2);
			}
		}*/
	}

	public static String getLimitsForDisplay() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType type = WireType.wireList[i];
			if (type.resistance > 0) {
				sb.append(ReikaStringParser.capFirstChar(type.name())+" - Resistance: "+type.resistance+" V/m;   Limit: "+type.maxCurrent+" A");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public boolean hasGlowLayer() {
		return this == SUPERCONDUCTOR;
	}

}
