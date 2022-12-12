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

import net.minecraft.world.level.block.Blocks;
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.rotarycraft.registry.RotaryItems;

public enum BatteryType {

	REDSTONE(	20,		256, 		32),
	GLOWSTONE(	24, 	512, 		64),
	LAPIS(		28, 	2048, 		128),
	ENDER(		33, 	4096, 		256),
	DIAMOND(	40, 	16384, 		1024),
	STAR(		48, 	65536,		4096);

	public final long maxCapacity;
	public final int outputVoltage;
	public final int outputCurrent;

	BatteryType(int cap, int v, int a) {
		maxCapacity = ReikaMathLibrary.longpow(2, cap);
		outputCurrent = a;
		outputVoltage = v;
	}

	public static final BatteryType[] batteryList = values();

	public String getName() {
		return I18n.get("battery."+this.name().toLowerCase(Locale.ENGLISH));
	}

	public String getFormattedCapacity() {
		double base = ReikaMathLibrary.getThousandBase(maxCapacity);
		String exp = ReikaEngLibrary.getSIPrefix(maxCapacity);
		return String.format("%.3f%sJ", base, exp);
	}

	public void addCrafting() {
		ItemStack is = this.getCraftedProduct();
		ItemStack in = ElectriItems.CRYSTAL.get().getDefaultInstance();//todo .getStackOfMetadata(this.ordinal());
		Object[] obj = {"ScS", "WCW", "SPS", 'W', Blocks.WHITE_WOOL /*todo wool color?*/, 'c', this.getTopMaterial(), 'C', in, 'P', this.getBottomMaterial(), 'S', RotaryItems.HSLA_STEEL_INGOT.get()};
		/*todo ShapedRecipe ir = new ShapedRecipe(is, obj);
		WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
		if (ConfigRegistry.TABLEMACHINES.getState()) {
			GameRegistry.addRecipe(ir);
		}*/
	}

	private Object getBottomMaterial() {
		return switch (this) {
			case STAR -> RotaryItems.BEDROCK_ALLOY_INGOT.get();
			case DIAMOND -> RotaryItems.TUNGSTEN_ALLOY_INGOT.get();
//todo			case LAPIS -> RotaryItems.silumin;
			default -> RotaryItems.HSLA_PLATE.get();
		};
	}

	private Object getTopMaterial() {
		return switch (this) {
			case GLOWSTONE, LAPIS -> "ingotSilver";
			case ENDER, STAR, DIAMOND -> RotaryItems.INDUCTIVE_INGOT.get();
			default -> "ingotCopper";
		};
	}

	public ItemStack getCraftedProduct() {
		return ElectriItems.BATTERY.get().getDefaultInstance();//todo getStackOfMetadata(this.ordinal());
	}

//	@SideOnly(Dist.CLIENT)
//	public void loadIcon(IIconRegister ico) {
//		icon = ico.registerIcon("electricraft:battery/"+this.name().toLowerCase(Locale.ENGLISH)+"_glow");
//	}

	public static String getDataForDisplay() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < batteryList.length; i++) {
			BatteryType type = batteryList[i];
			sb.append(type.getName()+" - Capacity: "+type.getFormattedCapacity()+"; Output: "+type.outputCurrent+"A @ "+type.outputVoltage+"V");
			sb.append("\n");
		}
		return sb.toString();
	}

}
