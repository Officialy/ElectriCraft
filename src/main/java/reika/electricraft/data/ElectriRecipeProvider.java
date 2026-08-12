package reika.electricraft.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import reika.electricraft.blockentities.BlockEntityFuse;
import reika.electricraft.registry.BatteryType;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.registry.ElectriOres;
import reika.electricraft.registry.WireType;
import reika.rotarycraft.auxiliary.recipemanagers.GrinderRecipe;
import reika.rotarycraft.registry.RotaryBlocks;
import reika.rotarycraft.registry.RotaryItems;

/**
 * ElectriCraft recipes, ported from the 1.7.10 ElectriRecipes + WireType/BatteryType addCrafting.
 * <p>
 * Batteries and fuses retain real runtime state in CUSTOM_DATA templates. Wires do not: every
 * conductor/insulation pair is a concrete registered block item, so their recipes use ordinary
 * vanilla ingredients and results. Wire recipes yield 16 (the original medium PIPECRAFT count).
 * Legacy recipes NOT ported: the EU/IC2 family and WorktableRecipes duplicates (the worktable
 * handler is a separate dependency vertical).
 */
public final class ElectriRecipeProvider extends RecipeProvider.Runner {

    public ElectriRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public String getName() {
        return "ElectriCraft Recipes";
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput out) {
        return new Recipes(registries, out);
    }

    private static final class Recipes extends RecipeProvider {
        private final RecipeOutput out;

        Recipes(HolderLookup.Provider registries, RecipeOutput out) {
            super(registries, out);
            this.out = out;
        }

        @Override
        protected void buildRecipes() {
            oreSmelting();
            wires();
            batteries();
            crystals();
            machines();
            rfInterop();
        }

        private void oreSmelting() {
            for (ElectriOres ore : ElectriOres.oreList) {
                if (ore == ElectriOres.COPPER)
                    continue; //vanilla copper covers it
                ItemLike ingot = switch (ore) {
                    case TIN -> ElectriItems.TIN_INGOT.get();
                    case SILVER -> ElectriItems.SILVER_INGOT.get();
                    case NICKEL -> ElectriItems.NICKEL_INGOT.get();
                    case ALUMINUM -> ElectriItems.ALUMINUM_INGOT.get();
                    case PLATINUM -> ElectriItems.PLATINUM_INGOT.get();
                    default -> throw new IllegalStateException();
                };
                String name = ore.name().toLowerCase(java.util.Locale.ROOT);
                cook(name + "_ore_smelting", ore.getBlock(), ingot, ore.xpDropped, false);
                cook(name + "_ore_blasting", ore.getBlock(), ingot, ore.xpDropped, true);
                if (ore.getDeepslateBlock() != null && ore != ElectriOres.COPPER) {
                    cook("deepslate_" + name + "_ore_smelting", ore.getDeepslateBlock(), ingot, ore.xpDropped, false);
                    cook("deepslate_" + name + "_ore_blasting", ore.getDeepslateBlock(), ingot, ore.xpDropped, true);
                }
            }
        }

        private void cook(String name, ItemLike input, ItemLike output, float xp, boolean blast) {
            var builder = blast
                    ? SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.MISC, CookingBookCategory.MISC, new ItemStackTemplate(output.asItem()), xp, 100)
                    : SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, CookingBookCategory.MISC, new ItemStackTemplate(output.asItem()), xp, 200);
            builder.unlockedBy("has_ore", has(input)).save(out, "electricraft:" + name);
        }

        /** The material each wire type is drawn from (1.7.10 table; copper/gold are vanilla). */
        private ItemLike wireMaterial(WireType type) {
            return switch (type) {
                case STEEL -> RotaryItems.HSLA_STEEL_INGOT.get();
                case TIN -> ElectriItems.TIN_INGOT.get();
                case NICKEL -> ElectriItems.NICKEL_INGOT.get();
                case ALUMINUM -> ElectriItems.ALUMINUM_INGOT.get();
                case COPPER -> Items.COPPER_INGOT;
                case SILVER -> ElectriItems.SILVER_INGOT.get();
                case GOLD -> Items.GOLD_INGOT;
                case PLATINUM -> ElectriItems.PLATINUM_INGOT.get();
                case SUPERCONDUCTOR -> null; //special recipe below
            };
        }

        private ItemStackTemplate wire(WireType type, boolean insulated, int count) {
            return new ItemStackTemplate(ElectriBlocks.getWireBlock(type, insulated).get().asItem(), count);
        }

        private void wires() {
            for (WireType type : WireType.wireList) {
                ItemLike mat = wireMaterial(type);
                if (mat == null)
                    continue;
                String name = type.name().toLowerCase(java.util.Locale.ROOT);
                //Legacy: 3 ingots in a column -> PIPECRAFT (default/medium = 16) wires.
                shaped(RecipeCategory.REDSTONE, wire(type, false, 16))
                        .define('I', mat)
                        .pattern("I").pattern("I").pattern("I")
                        .unlockedBy("has_material", has(mat))
                        .save(out, key("wire_" + name));
                //Legacy: same column flanked by wool -> insulated wires (same PIPECRAFT count).
                shaped(RecipeCategory.REDSTONE, wire(type, true, 16))
                        .define('I', mat)
                        .define('W', net.minecraft.tags.ItemTags.WOOL)
                        .pattern("WIW").pattern("WIW").pattern("WIW")
                        .unlockedBy("has_material", has(mat))
                        .save(out, key("wire_" + name + "_insulated"));
            }
            //Superconductor: "IGI","SRS","tgt" (I steel, G blast glass, S silver, R redstone,
            //t raw tungsten, g gold) -> PIPECRAFT (16).
            shaped(RecipeCategory.REDSTONE, wire(WireType.SUPERCONDUCTOR, false, 16))
                    .define('I', RotaryItems.HSLA_STEEL_INGOT.get())
                    .define('G', RotaryBlocks.BLASTGLASS.get())
                    .define('S', ElectriItems.SILVER_INGOT.get())
                    .define('R', Items.REDSTONE)
                    .define('t', RotaryItems.TUNGSTEN_INGOT.get())
                    .define('g', Items.GOLD_INGOT)
                    .pattern("IGI").pattern("SRS").pattern("tgt")
                    .unlockedBy("has_silver", has(ElectriItems.SILVER_INGOT.get()))
                    .save(out, key("wire_superconductor"));
            //Legacy: insulated superconductor is special - 3x from wool wrapped around the
            //non-insulated superconductor wire ("WwW" x3), NOT from ingots like the other insulateds.
            shaped(RecipeCategory.REDSTONE, wire(WireType.SUPERCONDUCTOR, true, 3))
                    .define('W', net.minecraft.tags.ItemTags.WOOL)
                    .define('w', Ingredient.of(ElectriBlocks.getWireBlock(WireType.SUPERCONDUCTOR, false).get()))
                    .pattern("WwW").pattern("WwW").pattern("WwW")
                    .unlockedBy("has_superconductor", has(RotaryItems.TUNGSTEN_INGOT.get()))
                    .save(out, key("wire_superconductor_insulated"));
        }

        private ItemStackTemplate battery(BatteryType tier) {
            return new ItemStackTemplate(ElectriBlocks.getBatteryBlock(tier).get().asItem());
        }

        private void batteries() {
            for (BatteryType tier : BatteryType.batteryList) {
                //Legacy "ScS","WCW","SPS": S steel, c top material, W wool, C tier crystal, P bottom material.
                ItemLike top = switch (tier) {
                    case GLOWSTONE, LAPIS -> ElectriItems.SILVER_INGOT.get();
                    case ENDER, STAR, DIAMOND -> RotaryItems.INDUCTIVE_INGOT.get();
                    default -> Items.COPPER_INGOT;
                };
                ItemLike bottom = switch (tier) {
                    case STAR -> RotaryItems.BEDROCK_ALLOY_INGOT.get();
                    case DIAMOND -> RotaryItems.TUNGSTEN_INGOT.get(); //legacy ItemStacks.tungsteningot (plain, not alloy)
                    case LAPIS -> RotaryItems.ALUMINUM_ALLOY_INGOT.get();
                    default -> RotaryItems.HSLA_PLATE.get();
                };
                shaped(RecipeCategory.REDSTONE, battery(tier))
                        .define('S', RotaryItems.HSLA_STEEL_INGOT.get())
                        .define('c', top)
                        .define('W', net.minecraft.tags.ItemTags.WOOL)
                        .define('C', ElectriItems.getCrystal(tier).get())
                        .define('P', bottom)
                        .pattern("ScS").pattern("WCW").pattern("SPS")
                        .unlockedBy("has_crystal", has(ElectriItems.getCrystal(tier).get()))
                        .save(out, key("battery_" + tier.name().toLowerCase(java.util.Locale.ROOT)));
            }
        }

        private void crystals() {
            //Dusts come from the RotaryCraft grinder (legacy RecipesGrinder entries).
            grind("diamond_to_dust", Items.DIAMOND, new ItemStackTemplate(ElectriItems.DIAMOND_DUST.get()));
            grind("lapis_to_dust", Items.LAPIS_LAZULI, new ItemStackTemplate(ElectriItems.BLUE_DUST.get()));
            grind("quartz_to_dust", Items.QUARTZ, new ItemStackTemplate(ElectriItems.QUARTZ_DUST.get()));

            //Legacy shapeless: blue + diamond + quartz + glowstone dust + 4x redstone -> 2 crystal dust.
            shapeless(RecipeCategory.MISC, new ItemStackTemplate(ElectriItems.CRYSTAL_DUST.get(), 2))
                    .requires(ElectriItems.BLUE_DUST.get())
                    .requires(ElectriItems.DIAMOND_DUST.get())
                    .requires(ElectriItems.QUARTZ_DUST.get())
                    .requires(Items.GLOWSTONE_DUST)
                    .requires(Items.REDSTONE, 4)
                    .unlockedBy("has_dusts", has(ElectriItems.DIAMOND_DUST.get()))
                    .save(out, key("crystal_dust"));

            //Smelting the dust yields the base (redstone) crystal.
            cook("crystal_smelting", ElectriItems.CRYSTAL_DUST.get(), ElectriItems.CRYSTAL_REDSTONE.get(), 1F, false);

            //Tier upgrades: "RCR","CIC","RCR" around glowstone/lapis blocks, ender eye, emerald block, nether star.
            ItemLike[] centers = {Blocks.GLOWSTONE, Blocks.LAPIS_BLOCK, Items.ENDER_EYE, Blocks.EMERALD_BLOCK, Items.NETHER_STAR};
            for (int i = 1; i < BatteryType.batteryList.length; i++) {
                ItemLike prev = ElectriItems.getCrystal(BatteryType.batteryList[i - 1]).get();
                ItemLike next = ElectriItems.getCrystal(BatteryType.batteryList[i]).get();
                shaped(RecipeCategory.MISC, next)
                        .define('R', Items.REDSTONE)
                        .define('C', prev)
                        .define('I', centers[i - 1])
                        .pattern("RCR").pattern("CIC").pattern("RCR")
                        .unlockedBy("has_crystal", has(prev))
                        .save(out, key("crystal_" + BatteryType.batteryList[i].name().toLowerCase(java.util.Locale.ROOT)));
            }
        }

        private void machines() {
            //Legacy: "gts","iGn","ppp" (g copper, t tin, s steel, i impeller, G generator unit, n nickel, p plate).
            shaped(RecipeCategory.REDSTONE, ElectriBlocks.GENERATOR.get())
                    .define('g', Items.COPPER_INGOT)
                    .define('t', ElectriItems.TIN_INGOT.get())
                    .define('s', RotaryItems.HSLA_STEEL_INGOT.get())
                    .define('i', RotaryItems.IMPELLER.get())
                    .define('G', RotaryItems.GENERATOR.get())
                    .define('n', ElectriItems.NICKEL_INGOT.get())
                    .define('p', RotaryItems.HSLA_PLATE.get())
                    .pattern("gts").pattern("iGn").pattern("ppp")
                    .unlockedBy("has_generator", has(RotaryItems.GENERATOR.get()))
                    .save(out, key("generator"));

            //Legacy: "scs","gCg","BcB" (s silver, c copper, g gold coil, C shaft core, B plate).
            shaped(RecipeCategory.REDSTONE, ElectriBlocks.MOTOR.get())
                    .define('s', ElectriItems.SILVER_INGOT.get())
                    .define('c', Items.COPPER_INGOT)
                    .define('g', RotaryItems.GOLD_COIL.get())
                    .define('C', RotaryItems.HSLA_SHAFT_CORE.get())
                    .define('B', RotaryItems.HSLA_PLATE.get())
                    .pattern("scs").pattern("gCg").pattern("BcB")
                    .unlockedBy("has_coil", has(RotaryItems.GOLD_COIL.get()))
                    .save(out, key("motor"));

            //Legacy: 4x from "SCS","CPC" (C copper, P plate, S steel).
            shaped(RecipeCategory.REDSTONE, new ItemStackTemplate(ElectriBlocks.RELAY.get().asItem(), 4))
                    .define('S', RotaryItems.HSLA_STEEL_INGOT.get())
                    .define('C', Items.COPPER_INGOT)
                    .define('P', RotaryItems.HSLA_PLATE.get())
                    .pattern("SCS").pattern("CPC")
                    .unlockedBy("has_plate", has(RotaryItems.HSLA_PLATE.get()))
                    .save(out, key("relay"));

            //Legacy: 4x from "SCS","PCP" (C coal dust, S steel, P plate).
            shaped(RecipeCategory.REDSTONE, new ItemStackTemplate(ElectriBlocks.RESISTOR.get().asItem(), 4))
                    .define('S', RotaryItems.HSLA_STEEL_INGOT.get())
                    .define('C', RotaryItems.COAL_DUST.get())
                    .define('P', RotaryItems.HSLA_PLATE.get())
                    .pattern("SCS").pattern("PCP")
                    .unlockedBy("has_plate", has(RotaryItems.HSLA_PLATE.get()))
                    .save(out, key("resistor"));

            //Legacy: "SsS","wCw","SbS" (S steel, s screen, w SILVER wire, C circuit board, b plate).
            //Silver Wire is a concrete item identity, replacing 1.7.10 metadata exactly.
            shaped(RecipeCategory.REDSTONE, ElectriBlocks.METER.get())
                    .define('S', RotaryItems.HSLA_STEEL_INGOT.get())
                    .define('s', RotaryItems.SCREEN.get())
                    .define('w', Ingredient.of(ElectriBlocks.getWireBlock(WireType.SILVER, false).get()))
                    .define('C', RotaryItems.CIRCUIT_BOARD.get())
                    .define('b', RotaryItems.HSLA_PLATE.get())
                    .pattern("SsS").pattern("wCw").pattern("SbS")
                    .unlockedBy("has_screen", has(RotaryItems.SCREEN.get()))
                    .save(out, key("meter"));

            //Legacy: "SSS","I I","SSS" (S plate, I inductive/redgold ingot).
            shaped(RecipeCategory.REDSTONE, ElectriBlocks.TRANSFORMER.get())
                    .define('S', RotaryItems.HSLA_PLATE.get())
                    .define('I', RotaryItems.INDUCTIVE_INGOT.get())
                    .pattern("SSS").pattern("I I").pattern("SSS")
                    .unlockedBy("has_inductive", has(RotaryItems.INDUCTIVE_INGOT.get()))
                    .save(out, key("transformer"));

            //Legacy: "aaa","tRt","PPP" (a silumin, t tungsten spring, R resistor, P plate).
            shaped(RecipeCategory.REDSTONE, ElectriBlocks.PRECISE_RESISTOR.get())
                    .define('a', RotaryItems.ALUMINUM_ALLOY_INGOT.get())
                    .define('t', RotaryItems.TUNGSTEN_ALLOY_SPRING.get())
                    .define('R', ElectriBlocks.RESISTOR.get())
                    .define('P', RotaryItems.HSLA_PLATE.get())
                    .pattern("aaa").pattern("tRt").pattern("PPP")
                    .unlockedBy("has_resistor", has(ElectriBlocks.RESISTOR.get()))
                    .save(out, key("precise_resistor"));

            //Legacy: 8x fuses per tier from " G ","GCG","PPP" (G glass, C the tier ingot, P plate),
            //Each rating is a concrete block/item identity in 26.2.
            ItemLike[] fuseIngots = {RotaryItems.COAL_DUST.get(), RotaryItems.HSLA_STEEL_INGOT.get(), Items.COPPER_INGOT, Items.GOLD_INGOT};
            String[] fuseNames = {"coal", "steel", "copper", "gold"};
            for (int i = 0; i < fuseIngots.length; i++) {
                shaped(RecipeCategory.REDSTONE, new ItemStackTemplate(
                                ElectriBlocks.getFuseBlock(BlockEntityFuse.TIERS[i]).get().asItem(), 8))
                        .define('G', Blocks.GLASS)
                        .define('C', fuseIngots[i])
                        .define('P', RotaryItems.HSLA_PLATE.get())
                        .pattern(" G ").pattern("GCG").pattern("PPP")
                        .unlockedBy("has_plate", has(RotaryItems.HSLA_PLATE.get()))
                        .save(out, key("fuse_" + fuseNames[i]));
            }

            //Legacy: "RSR","PPP","PPP" (R gold, S steel, P paper).
            shaped(RecipeCategory.MISC, ElectriItems.BOOK.get())
                    .define('R', Items.GOLD_INGOT)
                    .define('S', RotaryItems.HSLA_STEEL_INGOT.get())
                    .define('P', Items.PAPER)
                    .pattern("RSR").pattern("PPP").pattern("PPP")
                    .unlockedBy("has_steel", has(RotaryItems.HSLA_STEEL_INGOT.get()))
                    .save(out, key("book"));

            //Legacy RF cable (post-load, RF power system is native now): PIPECRAFT (16) from "RDR","BGB","RER".
            shaped(RecipeCategory.REDSTONE, new ItemStackTemplate(ElectriBlocks.RF_CABLE.get().asItem(), 16))
                    .define('D', Items.DIAMOND)
                    .define('R', Blocks.REDSTONE_BLOCK)
                    .define('G', Blocks.GOLD_BLOCK)
                    .define('E', Items.ENDER_PEARL)
                    .define('B', RotaryBlocks.BLASTGLASS.get())
                    .pattern("RDR").pattern("BGB").pattern("RER")
                    .unlockedBy("has_glass", has(RotaryBlocks.BLASTGLASS.get()))
                    .save(out, key("rf_cable"));
        }

        private void rfInterop() {
            //Legacy post-load (RF native now): star crystal ringed by redstone blocks -> RF crystal.
            shaped(RecipeCategory.MISC, ElectriItems.CRYSTAL_RF.get())
                    .define('R', Blocks.REDSTONE_BLOCK)
                    .define('C', ElectriItems.CRYSTAL_STAR.get())
                    .pattern("RRR").pattern("RCR").pattern("RRR")
                    .unlockedBy("has_crystal", has(ElectriItems.CRYSTAL_STAR.get()))
                    .save(out, key("crystal_rf"));

            //Legacy RF battery: "ScS","WCW","tPt" (t raw tungsten, c inductive, C RF crystal, P bedrock ingot).
            shaped(RecipeCategory.REDSTONE, ElectriItems.RFBATTERY.get())
                    .define('S', RotaryItems.HSLA_STEEL_INGOT.get())
                    .define('c', RotaryItems.INDUCTIVE_INGOT.get())
                    .define('W', net.minecraft.tags.ItemTags.WOOL)
                    .define('C', ElectriItems.CRYSTAL_RF.get())
                    .define('t', RotaryItems.TUNGSTEN_INGOT.get())
                    .define('P', RotaryItems.BEDROCK_ALLOY_INGOT.get())
                    .pattern("ScS").pattern("WCW").pattern("tPt")
                    .unlockedBy("has_crystal", has(ElectriItems.CRYSTAL_RF.get()))
                    .save(out, key("rf_battery"));

            //Legacy charge pads: "AEA","SCS","PRP" per tier (A steel, E ender pearl, P plate,
            //R silicon, S glass->blast glass, C redstone/gold/diamond/inductive). The 5th
            //SUPERCONDUCTING tier needed Thermal's enderium and stays unported.
            ItemLike[] sides = {Blocks.GLASS, Blocks.GLASS, Blocks.GLASS, RotaryBlocks.BLASTGLASS.get()};
            ItemLike[] cores = {Items.REDSTONE, Items.GOLD_INGOT, Items.DIAMOND, RotaryItems.INDUCTIVE_INGOT.get()};
            String[] tierNames = {"basic", "improved", "advanced", "hightech"};
            for (int i = 0; i < tierNames.length; i++) {
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("tier", i);
                DataComponentPatch patch = DataComponentPatch.builder()
                        .set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
                        .build();
                shaped(RecipeCategory.REDSTONE, new ItemStackTemplate(ElectriBlocks.WIRELESS_CHARGER.get().asItem(), 1, patch))
                        .define('A', RotaryItems.HSLA_STEEL_INGOT.get())
                        .define('E', Items.ENDER_PEARL)
                        .define('P', RotaryItems.HSLA_PLATE.get())
                        .define('R', RotaryItems.SILICON.get())
                        .define('S', sides[i])
                        .define('C', cores[i])
                        .pattern("AEA").pattern("SCS").pattern("PRP")
                        .unlockedBy("has_silicon", has(RotaryItems.SILICON.get()))
                        .save(out, key("charge_pad_" + tierNames[i]));
            }
        }

        private void grind(String name, ItemLike input, ItemStackTemplate output) {
            GrinderRecipe recipe = new GrinderRecipe(Ingredient.of(input), output);
            ResourceKey<Recipe<?>> rkey = ResourceKey.create(Registries.RECIPE,
                    Identifier.fromNamespaceAndPath("electricraft", "grinder/" + name));
            out.accept(rkey, recipe, null);
        }

        private static ResourceKey<Recipe<?>> key(String name) {
            return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("electricraft", name));
        }

    }
}
