/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft;

public class ElectriRecipes {

	/*public static void loadOreDict() {
		for (int i = 0; i < ElectriOres.oreList.length; i++) {
			ElectriOres ore = ElectriOres.oreList[i];
			OreDictionary.registerOre(ore.getDictionaryName(), ore.getOreBlock());
			OreDictionary.registerOre(ore.getProductDictionaryName(), ore.getProduct());
		}

		for (int i = 0; i < ElectriCrafting.craftingList.length; i++) {
			ElectriCrafting c = ElectriCrafting.craftingList[i];
			if (c.hasOreName()) {
				ItemStack is = c.getItem();
				String s = c.oreDictName;
				OreDictionary.registerOre(s, is);
			}
		}

		OreDictionary.registerOre("dustGlowstone", Items.GLOWSTONE_DUST);
	}

	public static void addRecipes() {
		for (int i = 0; i < WireType.wireList.length; i++) {
			WireType wire = WireType.wireList[i];
			wire.addCrafting();
		}
		for (int i = 0; i < BatteryType.batteryList.length; i++) {
			BatteryType bat = BatteryType.batteryList[i];
			bat.addCrafting();
		}
		for (int i = 0; i < BlockEntityWirelessCharger.ChargerTiers.tierList.length; i++) {
			ChargerTiers pad = BlockEntityWirelessCharger.ChargerTiers.tierList[i];
			ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
			is.stackTagCompound = new CompoundTag();
			is.stackTagCompound.putInt("tier", i);
			GameRegistry.addRecipe(new ShapedRecipe(is, pad.getRecipe()));
		}
		for (int i = 0; i < ElectriOres.oreList.length; i++) {
			ElectriOres ore = ElectriOres.oreList[i];
			ReikaRecipeHelper.addSmelting(ore.getOreBlock(), ore.getProduct(), ore.xpDropped);
		}

		GameRegistry.addRecipe(new ShapedRecipe(ElectriItems.BOOK.get(), "RSR", "PPP", "PPP", 'R', "ingotGold", 'S', RotaryItems.HSLA_STEEL_INGOT, 'P', Items.paper));

		RecipesGrinder.getRecipes().addRecipe(new ItemStack(Items.DIAMOND), ElectriCrafting.DIAMONDDUST.getItem());
		RecipesGrinder.getRecipes().addRecipe(ReikaItemHelper.lapisdye, ElectriCrafting.BLUEDUST.getItem());
		RecipesGrinder.getRecipes().addRecipe(new ItemStack(Items.QUARTZ), ElectriCrafting.QUARTZDUST.getItem());

		ReikaRecipeHelper.addSmelting(ElectriCrafting.CRYSTALDUST.getItem(), ElectriItems.CRYSTAL.get(), 1F);
		GameRegistry.addRecipe(new ShapelessRecipe(ElectriCrafting.CRYSTALDUST.getItem(2), ElectriCrafting.BLUEDUST.oreDictName, ElectriCrafting.DIAMONDDUST.oreDictName, ElectriCrafting.QUARTZDUST.oreDictName, "dustGlowstone", "dustRedstone", "dustRedstone", "dustRedstone", "dustRedstone"));

		Object[] ctr = {Blocks.glowstone, Blocks.lapis_block, Items.ender_eye, Blocks.emerald_block, Items.nether_star};
		for (int i = 1; i < BatteryType.batteryList.length; i++) {
			ItemStack cry = ElectriItems.CRYSTAL.getStackOfMetadata(i-1);
			ItemStack is = ElectriItems.CRYSTAL.getStackOfMetadata(i);
			GameRegistry.addRecipe(is, "RCR", "CIC", "RCR", 'R', Items.redstone, 'C', cry, 'I', ctr[i-1]);
		}

		ItemStack w = ReikaItemHelper.getSizedItemStack(WireType.SUPERCONDUCTOR.getCraftedProduct(), DifficultyEffects.PIPECRAFT.getInt());
		ItemStack w2 = ReikaItemHelper.getSizedItemStack(WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct(), 3);
		ShapedRecipe ir = new ShapedRecipe(w, "IGI", "SRS", "tgt", 't', RotaryItems.TUNGSTEN_ALLOY_INGOT, 'I', RotaryItems.HSLA_STEEL_INGOT, 'G', BlockRegistry.BLASTGLASS.get(), 'S', "ingotSilver", 'g', "ingotGold", 'R', Items.redstone);
		Object[] obj2 = {"WwW", "WwW", "WwW", 'W', Blocks.wool, 'w', w};
		WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
		WorktableRecipes.getInstance().addRecipe(w2, RecipeLevel.CORE, obj2);
		if (RotaryConfig.COMMON.TABLEMACHINES.get()) {
			GameRegistry.addRecipe(ir);
			GameRegistry.addRecipe(w2, obj2);
		}

		ElectriTiles.GENERATOR.addOreCrafting("gts", "iGn", "ppp", 'n', "ingotNickel", 't', "ingotTin", 'p', RotaryItems.HSLA_PLATE, 'g', "ingotCopper", 's', RotaryItems.HSLA_STEEL_INGOT, 'G', RotaryItems.GENERATOR, 'i', RotaryItems.IMPELLER);
		ElectriTiles.MOTOR.addOreCrafting("scs", "gCg", "BcB", 'g', RotaryItems.GOLD_COIL, 'c', "ingotCopper", 's', "ingotSilver", 'S', RotaryItems.HSLA_STEEL_INGOT, 'B', RotaryItems.HSLA_PLATE, 'C', RotaryItems.HSLA_SHAFT_CORE);
		ElectriTiles.RELAY.addSizedOreCrafting(4, "SCS", "CPC", 'C', "ingotCopper", 'P', RotaryItems.HSLA_PLATE, 'S', RotaryItems.HSLA_STEEL_INGOT);
		ElectriTiles.RESISTOR.addSizedOreCrafting(4, "SCS", "PCP", 'C', "dustCoal", 'S', RotaryItems.HSLA_STEEL_INGOT, 'P', RotaryItems.HSLA_PLATE);
		ElectriTiles.METER.addCrafting("SsS", "wCw", "SbS", 'S', RotaryItems.HSLA_STEEL_INGOT, 'w', WireType.SILVER.getCraftedProduct(), 'C', RotaryItems.pcb, 's', RotaryItems.screen, 'b', RotaryItems.HSLA_PLATE);
		ElectriTiles.TRANSFORMER.addCrafting("SSS", "I I", "SSS", 'S', RotaryItems.HSLA_PLATE, 'I', RotaryItems.redgoldingot);
		ElectriTiles.PRECISERESISTOR.addCrafting("aaa", "tRt", "PPP", 'a', RotaryItems.SILUMIN, 't', RotaryItems.springtungsten, 'R', ElectriTiles.RESISTOR.getCraftedProduct(), 'P', RotaryItems.HSLA_PLATE);

		ItemStack[] FUSE_INGOTS = {
				RotaryItems.COAL_DUST.copy(),
				RotaryItems.HSLA_STEEL_INGOT.get(),
				ElectriStacks.copperIngot.copy(),
				new ItemStack(Items.GOLD_INGOT)
		};

		for (int i = 0; i < FUSE_INGOTS.length; i++) {
			Object ingot = FUSE_INGOTS[i];
			CompoundTag tag = new BlockEntityFuse(BlockEntityFuse.TIERS[i]).getTagsToWriteToStack();
			ElectriTiles.FUSE.addSizedOreNBTCrafting(8, tag, " G ", "GCG", "PPP", 'G', Blocks.GLASS, 'C', ingot, 'P', RotaryItems.BASEPANEL, 'S', RotaryItems.HSLA_STEEL_INGOT);
		}
	}

	public static void addPostLoadRecipes() {
		if (PowerTypes.RF.isLoaded()) {
			ElectriTiles.CABLE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "RDR", "BGB", "RER", 'D', Items.diamond, 'R', Blocks.REDSTONE_BLOCK, 'G', Blocks.gold_block, 'E', Items.ender_pearl, 'B', BlockRegistry.BLASTGLASS.get());

			Object[] obj = {"ScS", "WCW", "tPt", 't', RotaryItems.TUNGSTEN_ALLOY_INGOT, 'W', Blocks.WHITE_WOOL, 'c', RotaryItems.redgoldingot, 'C', ElectriItems.CRYSTAL.getStackOfMetadata(BatteryType.batteryList.length), 'P', RotaryItems.BEDROCK_ALLOY_INGOT, 'S', RotaryItems.HSLA_STEEL_INGOT};
			ShapedRecipe ir2 = new ShapedRecipe(ElectriTiles.RFBATTERY.getCraftedProduct(), obj);
			WorktableRecipes.getInstance().addRecipe(ir2, RecipeLevel.CORE);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir2);
			}

			ItemStack cry = ElectriItems.CRYSTAL.getStackOfMetadata(BatteryType.batteryList.length-1);
			ItemStack out = ElectriItems.CRYSTAL.getStackOfMetadata(BatteryType.batteryList.length);
			GameRegistry.addRecipe(out, "RRR", "RCR", "RRR", 'R', Blocks.REDSTONE_BLOCK, 'C', cry);
		}

		/*if (PowerTypes.EU.isLoaded() && ModList.IC2.isLoaded()) {
			ElectriTiles.EUSPLIT.addOreCrafting("PCP", "CcC", "PCP", 'P', RotaryItems.HSLA_PLATE, 'C', "ingotCopper", 'c', RotaryItems.GOLD_COIL);
			ItemStack dust = IC2Handler.getInstance().isIC2Classic() ? IC2Handler.IC2Stacks.ADVANCEDALLOY.getItem() : IC2Handler.IC2Stacks.ENERGIUM.getItem();
			ElectriTiles.EUCABLE.addSizedCrafting(DifficultyEffects.PIPECRAFT.getInt(), "RDR", "BGB", "tEt", 'D', Items.diamond, 'R', dust, 't', RotaryItems.TUNGSTEN_ALLOY_INGOT, 'G', Blocks.gold_block, 'E', Items.ender_pearl, 'B', BlockRegistry.BLASTGLASS.get());

			ItemStack plate = IC2Handler.IC2Stacks.ADVANCEDALLOY.getItem();
			Object[] obj = {"ScS", "WCW", "tPt", 't', RotaryItems.TUNGSTEN_ALLOY_INGOT, 'W', plate, 'c', RotaryItems.redgoldingot, 'C', ElectriItems.CRYSTAL.getStackOfMetadata(BatteryType.batteryList.length+1), 'P', RotaryItems.BEDROCK_ALLOY_INGOT, 'S', RotaryItems.HSLA_STEEL_INGOT};
			ShapedRecipe ir2 = new ShapedRecipe(ElectriTiles.EUBATTERY.getCraftedProduct(), obj);
			WorktableRecipes.getInstance().addRecipe(ir2, RecipeLevel.CORE);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir2);
			}

			ItemStack cry = ElectriItems.CRYSTAL.getStackOfMetadata(BatteryType.batteryList.length-1);
			ItemStack out = ElectriItems.CRYSTAL.getStackOfMetadata(BatteryType.batteryList.length+1);
			GameRegistry.addRecipe(out, "fLf", "LCL", "RLG", 'f', IC2Stacks.CARBONFIBER.getItem(), 'R', Blocks.REDSTONE_BLOCK, 'G', Blocks.GLOWSTONE, 'C', cry, 'L', ReikaItemHelper.getAnyMetaStack(IC2Handler.IC2Stacks.LAPOTRON.getItem()));
		}*/

		/*if (ModList.THERMALFOUNDATION.isLoaded()) {
			ItemStack is = WireType.SUPERCONDUCTOR.getCraftedProduct();
			ItemStack is2 = WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct();
			ItemWirePlacer item = (ItemWirePlacer)ElectriItems.WIRE.get();
			FluidStack f1 = new FluidStack(RotaryFluids.LIQUID_NITROGEN.get(), item.getCapacity(is));
			FluidStack f2 = new FluidStack(FluidRegistry.getFluid("cryotheum"), item.getCapacity(is));
			ThermalRecipeHelper.addFluidTransposerFill(is, item.getFilledSuperconductor(false), 200, f1);
			ThermalRecipeHelper.addFluidTransposerFill(is, item.getFilledSuperconductor(false), 200, f2);
			ThermalRecipeHelper.addFluidTransposerFill(is2, item.getFilledSuperconductor(true), 200, f1);
			ThermalRecipeHelper.addFluidTransposerFill(is2, item.getFilledSuperconductor(true), 200, f2);
		}
	}*/

}
