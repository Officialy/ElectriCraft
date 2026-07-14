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

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriItemBase;
import reika.electricraft.items.ItemBatteryPlacer;
import reika.electricraft.items.ItemElectriBook;
import reika.electricraft.items.ItemEnergyCrystal;
import reika.electricraft.items.ItemRFBatteryPlacer;

import java.util.function.Supplier;

// 1.21.5: typed DeferredRegister.Items and DeferredItem replace ForgeRegistries/RegistryObject.
public class ElectriItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ElectriCraft.MODID);

    // 1.21.5: Item.Properties needs setId() before Item.<init>. Threadlocal-driven helper.
    private static final ThreadLocal<ResourceKey<Item>> CURRENT_ITEM_KEY = new ThreadLocal<>();

    public static Item.Properties itemProperties() {
        Item.Properties p = new Item.Properties();
        ResourceKey<Item> k = CURRENT_ITEM_KEY.get();
        if (k != null) p.setId(k);
        return p;
    }

    private static <I extends Item> DeferredItem<I> reg(String name, Supplier<I> factory) {
        return ITEMS.register(name, rl -> {
            CURRENT_ITEM_KEY.set(ResourceKey.create(Registries.ITEM, rl));
            try {
                return factory.get();
            } finally {
                CURRENT_ITEM_KEY.remove();
            }
        });
    }

    //1.7.10 packed the material items into three metadata items (electriingots/crafting/
    //electricrystal); the port registers them individually, modern-convention style.
    public static final DeferredItem<Item> TIN_INGOT = reg("tin_ingot", () -> new ElectriItemBase(itemProperties()));
    public static final DeferredItem<Item> SILVER_INGOT = reg("silver_ingot", () -> new ElectriItemBase(itemProperties()));
    public static final DeferredItem<Item> NICKEL_INGOT = reg("nickel_ingot", () -> new ElectriItemBase(itemProperties()));
    public static final DeferredItem<Item> ALUMINUM_INGOT = reg("aluminum_ingot", () -> new ElectriItemBase(itemProperties()));
    public static final DeferredItem<Item> PLATINUM_INGOT = reg("platinum_ingot", () -> new ElectriItemBase(itemProperties()));

    public static final DeferredItem<Item> BLUE_DUST = reg("blue_dust", () -> new ElectriItemBase(itemProperties()));
    public static final DeferredItem<Item> DIAMOND_DUST = reg("diamond_dust", () -> new ElectriItemBase(itemProperties()));
    public static final DeferredItem<Item> QUARTZ_DUST = reg("quartz_dust", () -> new ElectriItemBase(itemProperties()));
    public static final DeferredItem<Item> CRYSTAL_DUST = reg("crystal_dust", () -> new ElectriItemBase(itemProperties()));

    //One energy crystal per battery tier (BatteryType order).
    public static final DeferredItem<Item> CRYSTAL_REDSTONE = reg("energy_crystal_redstone", () -> new ItemEnergyCrystal(itemProperties()));
    public static final DeferredItem<Item> CRYSTAL_GLOWSTONE = reg("energy_crystal_glowstone", () -> new ItemEnergyCrystal(itemProperties()));
    public static final DeferredItem<Item> CRYSTAL_LAPIS = reg("energy_crystal_lapis", () -> new ItemEnergyCrystal(itemProperties()));
    public static final DeferredItem<Item> CRYSTAL_ENDER = reg("energy_crystal_ender", () -> new ItemEnergyCrystal(itemProperties()));
    public static final DeferredItem<Item> CRYSTAL_DIAMOND = reg("energy_crystal_diamond", () -> new ItemEnergyCrystal(itemProperties()));
    public static final DeferredItem<Item> CRYSTAL_STAR = reg("energy_crystal_star", () -> new ItemEnergyCrystal(itemProperties()));
    //The beyond-STAR tier from 1.7.10's RF interop chain (crystal metadata 6).
    public static final DeferredItem<Item> CRYSTAL_RF = reg("energy_crystal_rf", () -> new ItemEnergyCrystal(itemProperties()));

    public static final DeferredItem<Item> BATTERY = reg("battery", () -> new ItemBatteryPlacer(itemProperties()));
    public static final DeferredItem<Item> RFBATTERY = reg("rfbattery", () -> new ItemRFBatteryPlacer(itemProperties()));
    public static final DeferredItem<Item> BOOK = reg("electribook", () -> new ItemElectriBook(itemProperties()));

    public static DeferredItem<Item> getCrystal(BatteryType tier) {
        return switch (tier) {
            case REDSTONE -> CRYSTAL_REDSTONE;
            case GLOWSTONE -> CRYSTAL_GLOWSTONE;
            case LAPIS -> CRYSTAL_LAPIS;
            case ENDER -> CRYSTAL_ENDER;
            case DIAMOND -> CRYSTAL_DIAMOND;
            case STAR -> CRYSTAL_STAR;
        };
    }
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
