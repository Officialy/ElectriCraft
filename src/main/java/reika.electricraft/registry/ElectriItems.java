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

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriItemBase;
import reika.electricraft.items.ItemBatteryPlacer;
import reika.electricraft.items.ItemElectriBook;
import reika.electricraft.items.ItemEnergyCrystal;
import reika.electricraft.items.ItemRFBatteryPlacer;
import reika.rotarycraft.RotaryCraft;

public class ElectriItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ElectriCraft.MODID);

    //	public static final RegistryObject<Item> PLACER= ITEMS.register(0, false, "placer", ItemElectriPlacer),
    public static final RegistryObject<Item> INGOTS = ITEMS.register("electriingots", () -> new ElectriItemBase(new Item.Properties()));
    //	public static final RegistryObject<Item> WIRE= ITEMS.register(1, true, "machine.wire", ItemWirePlacer(new Item.Properties()));
    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery", () -> new ItemBatteryPlacer(new Item.Properties()));
    public static final RegistryObject<Item> CRAFTING = ITEMS.register("crafting", () -> new ElectriItemBase(new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL = ITEMS.register("electricrystal", () -> new ItemEnergyCrystal(new Item.Properties()));
    public static final RegistryObject<Item> RFBATTERY = ITEMS.register("rfbattery", () -> new ItemRFBatteryPlacer(new Item.Properties()));
    public static final RegistryObject<Item> BOOK = ITEMS.register("electribook", () -> new ItemElectriBook(new Item.Properties()));
//	public static final RegistryObject<Item> EUBATTERY(5, false, "machine.eubattery", ItemEUBatteryPlacer);


/*	public void addRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.get(), params);
			WorktableRecipes.getInstance().addRecipe(this.get(), RecipeLevel.CORE, params);
		}
	}

	public void addSizedRecipe(int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedProduct(num), params);
			WorktableRecipes.getInstance().addRecipe(this.getCraftedProduct(num), RecipeLevel.CORE, params);
		}
	}

	public void addSizedMetaRecipe(int meta, int num, Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(this.getCraftedMetadataProduct(num, meta), params);
			WorktableRecipes.getInstance().addRecipe(this.getCraftedMetadataProduct(num, meta), RecipeLevel.CORE, params);
		}
	}

	public void addShapelessRecipe(Object... params) {
		if (!this.isDummiedOut()) {
			GameRegistry.addShapelessRecipe(this.get(), params);
			WorktableRecipes.getInstance().addShapelessRecipe(this.get(), RecipeLevel.CORE, params);
		}
	}

	public void addRecipe(Recipe ir) {
		if (!this.isDummiedOut()) {
			GameRegistry.addRecipe(ir);
			WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
		}
	}

	public void addOreRecipe(Object... in) {
		if (!this.isDummiedOut()) {
			ItemStack out = this.get();
			boolean added = ReikaRecipeHelper.addOreRecipe(out, in);
			if (added)
				WorktableRecipes.getInstance().addRecipe(new ShapedRecipe(out, in), RecipeLevel.CORE);
		}
	}*/

}
