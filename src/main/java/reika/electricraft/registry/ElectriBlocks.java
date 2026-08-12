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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blocks.*;
import reika.electricraft.items.ItemBatteryPlacer;

import java.util.EnumMap;
import java.util.function.Supplier;

// 1.21.5: ForgeRegistries → typed DeferredRegister.Blocks/Items; RegistryObject → DeferredBlock/DeferredItem.
public class ElectriBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ElectriCraft.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ElectriCraft.MODID);

    /*
     * 1.7.10 encoded material and insulation in one wire block's metadata. That does not map
     * cleanly to modern recipes, block items, loot, or model identities. Every original conductor
     * is therefore a concrete block/item pair; the registry identity is the material contract.
     */
    private static final EnumMap<WireType, DeferredBlock<BlockWire>> BARE_WIRES = new EnumMap<>(WireType.class);
    private static final EnumMap<WireType, DeferredBlock<BlockWire>> INSULATED_WIRES = new EnumMap<>(WireType.class);
    private static final EnumMap<BatteryType, DeferredBlock<BlockElectricBattery>> BATTERIES = new EnumMap<>(BatteryType.class);
    private static final java.util.LinkedHashMap<Integer, DeferredBlock<BlockElectricFuse>> FUSES = new java.util.LinkedHashMap<>();

    public static final DeferredBlock<BlockWire> STEEL_WIRE = registerWire(WireType.STEEL, false);
    public static final DeferredBlock<BlockWire> INSULATED_STEEL_WIRE = registerWire(WireType.STEEL, true);
    public static final DeferredBlock<BlockWire> TIN_WIRE = registerWire(WireType.TIN, false);
    public static final DeferredBlock<BlockWire> INSULATED_TIN_WIRE = registerWire(WireType.TIN, true);
    public static final DeferredBlock<BlockWire> NICKEL_WIRE = registerWire(WireType.NICKEL, false);
    public static final DeferredBlock<BlockWire> INSULATED_NICKEL_WIRE = registerWire(WireType.NICKEL, true);
    public static final DeferredBlock<BlockWire> ALUMINUM_WIRE = registerWire(WireType.ALUMINUM, false);
    public static final DeferredBlock<BlockWire> INSULATED_ALUMINUM_WIRE = registerWire(WireType.ALUMINUM, true);
    public static final DeferredBlock<BlockWire> COPPER_WIRE = registerWire(WireType.COPPER, false);
    public static final DeferredBlock<BlockWire> INSULATED_COPPER_WIRE = registerWire(WireType.COPPER, true);
    public static final DeferredBlock<BlockWire> SILVER_WIRE = registerWire(WireType.SILVER, false);
    public static final DeferredBlock<BlockWire> INSULATED_SILVER_WIRE = registerWire(WireType.SILVER, true);
    public static final DeferredBlock<BlockWire> GOLD_WIRE = registerWire(WireType.GOLD, false);
    public static final DeferredBlock<BlockWire> INSULATED_GOLD_WIRE = registerWire(WireType.GOLD, true);
    public static final DeferredBlock<BlockWire> PLATINUM_WIRE = registerWire(WireType.PLATINUM, false);
    public static final DeferredBlock<BlockWire> INSULATED_PLATINUM_WIRE = registerWire(WireType.PLATINUM, true);
    public static final DeferredBlock<BlockWire> SUPERCONDUCTOR_WIRE = registerWire(WireType.SUPERCONDUCTOR, false);
    public static final DeferredBlock<BlockWire> INSULATED_SUPERCONDUCTOR_WIRE = registerWire(WireType.SUPERCONDUCTOR, true);
    public static final DeferredBlock<Block> GENERATOR = register("induction_generator", () -> new BlockElectricGenerator(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> MOTOR = register("induction_motor", () -> new BlockElectricMotor(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> METER = register("meter", () -> new BlockElectricMeter(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> PRECISE_RESISTOR = register("precise_resistor", () -> new BlockElectricPreciseResistor(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> TRANSFORMER = register("transformer", () -> new BlockElectricTransformer(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<BlockElectricFuse> FUSE_32A = registerFuse(32);
    public static final DeferredBlock<BlockElectricFuse> FUSE_128A = registerFuse(128);
    public static final DeferredBlock<BlockElectricFuse> FUSE_1024A = registerFuse(1024);
    public static final DeferredBlock<BlockElectricFuse> FUSE_8192A = registerFuse(8192);
    public static final DeferredBlock<Block> RESISTOR = register("resistor", () -> new BlockElectricResistor(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> RELAY = register("relay", () -> new BlockElectricRelay(blockProperties().mapColor(MapColor.METAL).noOcclusion()));

    public static final DeferredBlock<Block> ORE = register("electriore", () -> new BlockElectriOre(blockProperties().mapColor(MapColor.METAL).strength(2, 5)));

    //Per-ore blocks (1.7.10 packed all six ores into ELECTRIORE metadata; copper is vanilla now).
    //Deepslate variants exist for the ores whose remapped Y-range dips below y=0.
    public static final DeferredBlock<Block> TIN_ORE = registerOre("tin_ore");
    public static final DeferredBlock<Block> SILVER_ORE = registerOre("silver_ore");
    public static final DeferredBlock<Block> NICKEL_ORE = registerOre("nickel_ore");
    public static final DeferredBlock<Block> ALUMINUM_ORE = registerOre("aluminum_ore");
    public static final DeferredBlock<Block> PLATINUM_ORE = registerOre("platinum_ore");
    public static final DeferredBlock<Block> DEEPSLATE_SILVER_ORE = registerOre("deepslate_silver_ore");
    public static final DeferredBlock<Block> DEEPSLATE_NICKEL_ORE = registerOre("deepslate_nickel_ore");
    public static final DeferredBlock<Block> DEEPSLATE_PLATINUM_ORE = registerOre("deepslate_platinum_ore");

    private static DeferredBlock<Block> registerOre(String name) {
        boolean deepslate = name.startsWith("deepslate");
        return register(name, () -> new net.minecraft.world.level.block.DropExperienceBlock(
                net.minecraft.util.valueproviders.UniformInt.of(0, 0),
                blockProperties().mapColor(deepslate ? MapColor.DEEPSLATE : MapColor.STONE)
                        .strength(deepslate ? 4.5F : 3F, 3F)
                        .requiresCorrectToolForDrops()
                        .sound(deepslate ? SoundType.DEEPSLATE : SoundType.STONE)));
    }
    public static final DeferredBlock<BlockElectricBattery> REDSTONE_BATTERY = registerBattery(BatteryType.REDSTONE);
    public static final DeferredBlock<BlockElectricBattery> GLOWSTONE_BATTERY = registerBattery(BatteryType.GLOWSTONE);
    public static final DeferredBlock<BlockElectricBattery> LAPIS_BATTERY = registerBattery(BatteryType.LAPIS);
    public static final DeferredBlock<BlockElectricBattery> ENDER_BATTERY = registerBattery(BatteryType.ENDER);
    public static final DeferredBlock<BlockElectricBattery> DIAMOND_BATTERY = registerBattery(BatteryType.DIAMOND);
    public static final DeferredBlock<BlockElectricBattery> STAR_BATTERY = registerBattery(BatteryType.STAR);
    public static final DeferredBlock<Block> RF_CABLE = register("rfcable", () -> new BlockRFCable(blockProperties().mapColor(MapColor.METAL)));
    public static final DeferredBlock<Block> RFBATTERY = registerWithoutItem("electrirfbattery", () -> new BlockRFBattery(blockProperties().mapColor(MapColor.METAL).strength(2, 10)));
    public static final DeferredBlock<Block> WIRELESS_CHARGER = register("electrichargepad", () -> new BlockChargePad(blockProperties().mapColor(MapColor.METAL).strength(2, 10)));

    private static DeferredBlock<BlockElectricBattery> registerBattery(BatteryType type) {
        String name = type.name().toLowerCase(java.util.Locale.ROOT) + "_battery";
        DeferredBlock<BlockElectricBattery> block = registerWithItem(name,
                () -> new BlockElectricBattery(blockProperties().mapColor(MapColor.METAL)
                        .strength(2, 10).noOcclusion(), type),
                (placed, properties) -> new ItemBatteryPlacer(placed, type, properties));
        BATTERIES.put(type, block);
        return block;
    }

    private static DeferredBlock<BlockElectricFuse> registerFuse(int currentLimit) {
        DeferredBlock<BlockElectricFuse> block = registerWithItem("fuse_" + currentLimit + "a",
                () -> new BlockElectricFuse(blockProperties().mapColor(MapColor.METAL).noOcclusion(), currentLimit),
                BlockItem::new);
        FUSES.put(currentLimit, block);
        return block;
    }

    public static DeferredBlock<BlockElectricBattery> getBatteryBlock(BatteryType type) {
        DeferredBlock<BlockElectricBattery> block = BATTERIES.get(type);
        if (block == null)
            throw new IllegalArgumentException("No registered ElectriCraft battery for " + type);
        return block;
    }

    public static Block[] getBatteryBlocks() {
        return BATTERIES.values().stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static boolean isBattery(Block block) {
        return block instanceof BlockElectricBattery;
    }

    public static DeferredBlock<BlockElectricFuse> getFuseBlock(int currentLimit) {
        DeferredBlock<BlockElectricFuse> block = FUSES.get(currentLimit);
        if (block == null)
            throw new IllegalArgumentException("No registered ElectriCraft fuse for " + currentLimit + " A");
        return block;
    }

    public static Block[] getFuseBlocks() {
        return FUSES.values().stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static boolean isFuse(Block block) {
        return block instanceof BlockElectricFuse;
    }

    private static DeferredBlock<BlockWire> registerWire(WireType type, boolean insulated) {
        String material = type.name().toLowerCase(java.util.Locale.ROOT);
        String name = insulated ? "insulated_" + material + "_wire" : material + "_wire";
        DeferredBlock<BlockWire> block = registerWithItem(name,
                () -> new BlockWire(blockProperties().mapColor(MapColor.METAL).strength(0.05F, 2F)
                        .sound(SoundType.WOOL).noOcclusion(), type, insulated),
                reika.electricraft.items.ItemWire::new);
        (insulated ? INSULATED_WIRES : BARE_WIRES).put(type, block);
        return block;
    }

    public static DeferredBlock<BlockWire> getWireBlock(WireType type, boolean insulated) {
        DeferredBlock<BlockWire> result = (insulated ? INSULATED_WIRES : BARE_WIRES).get(type);
        if (result == null)
            throw new IllegalArgumentException("No registered ElectriCraft wire for " + type + " (insulated=" + insulated + ")");
        return result;
    }

    public static boolean isWire(Block block) {
        return block instanceof BlockWire;
    }

    public static Block[] getWireBlocks() {
        return java.util.stream.Stream.concat(BARE_WIRES.values().stream(), INSULATED_WIRES.values().stream())
                .map(DeferredBlock::get)
                .toArray(Block[]::new);
    }

    // 1.21.5: Block/Item Properties require setId() before the constructor runs. Stash the
    // ResourceKey in a ThreadLocal during each factory invocation; blockProperties() reads it.
    private static final ThreadLocal<ResourceKey<Block>> CURRENT_BLOCK_KEY = new ThreadLocal<>();

    public static BlockBehaviour.Properties blockProperties() {
        BlockBehaviour.Properties p = BlockBehaviour.Properties.of();
        ResourceKey<Block> k = CURRENT_BLOCK_KEY.get();
        if (k != null) p.setId(k);
        return p;
    }

    private static <BLOCK extends Block> DeferredBlock<BLOCK> registerWithItem(final String name, final Supplier<BLOCK> blockFactory,
            final java.util.function.BiFunction<BLOCK, net.minecraft.world.item.Item.Properties, ? extends net.minecraft.world.item.BlockItem> itemFactory) {
        DeferredBlock<BLOCK> block = BLOCKS.register(name, rl -> {
            CURRENT_BLOCK_KEY.set(ResourceKey.create(Registries.BLOCK, rl));
            try {
                return blockFactory.get();
            } finally {
                CURRENT_BLOCK_KEY.remove();
            }
        });
        ITEMS.register(name, rl -> itemFactory.apply(block.get(),
                new net.minecraft.world.item.Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, rl))
                        .useBlockDescriptionPrefix()));
        return block;
    }

    private static <BLOCK extends Block> DeferredBlock<BLOCK> register(final String name, final Supplier<BLOCK> blockFactory) {
        DeferredBlock<BLOCK> block = registerWithoutItem(name, blockFactory);
        ITEMS.registerSimpleBlockItem(block); // auto-sets the BlockItem id
        return block;
    }

    /** Use for blocks whose genuine item form is a stateful custom placer, not a duplicate BlockItem. */
    private static <BLOCK extends Block> DeferredBlock<BLOCK> registerWithoutItem(final String name, final Supplier<BLOCK> blockFactory) {
        return BLOCKS.register(name, rl -> {
            CURRENT_BLOCK_KEY.set(ResourceKey.create(Registries.BLOCK, rl));
            try {
                return blockFactory.get();
            } finally {
                CURRENT_BLOCK_KEY.remove();
            }
        });
    }
}
