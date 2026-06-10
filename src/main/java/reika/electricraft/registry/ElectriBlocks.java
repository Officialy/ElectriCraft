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

import java.util.function.Supplier;

// 1.21.5: ForgeRegistries → typed DeferredRegister.Blocks/Items; RegistryObject → DeferredBlock/DeferredItem.
public class ElectriBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ElectriCraft.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ElectriCraft.MODID);

    public static final DeferredBlock<Block> WIRE = register("wire", () -> new BlockWire(blockProperties().mapColor(MapColor.METAL).strength(0.05F, 2F).sound(SoundType.WOOL).noOcclusion()));
    public static final DeferredBlock<Block> GENERATOR = register("converter", () -> new BlockElectricGenerator(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> MOTOR = register("motor", () -> new BlockElectricMotor(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> METER = register("meter", () -> new BlockElectricMeter(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> PRECISE_RESISTOR = register("precise_resistor", () -> new BlockElectricPreciseResistor(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> TRANSFORMER = register("transformer", () -> new BlockElectricTransformer(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> FUSE = register("fuse", () -> new BlockElectricFuse(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> RESISTOR = register("resistor", () -> new BlockElectricResistor(blockProperties().mapColor(MapColor.METAL).noOcclusion()));
    public static final DeferredBlock<Block> RELAY = register("relay", () -> new BlockElectricRelay(blockProperties().mapColor(MapColor.METAL).noOcclusion()));

    public static final DeferredBlock<Block> ORE = register("electriore", () -> new BlockElectriOre(blockProperties().mapColor(MapColor.METAL).strength(2, 5)));
    public static final DeferredBlock<Block> BATTERY = register("electribattery", () -> new BlockElectricBattery(blockProperties().mapColor(MapColor.METAL).strength(2, 10).noOcclusion()));
    public static final DeferredBlock<Block> RF_CABLE = register("rfcable", () -> new BlockRFCable(blockProperties().mapColor(MapColor.METAL)));
    public static final DeferredBlock<Block> RFBATTERY = register("electrirfbattery", () -> new BlockRFBattery(blockProperties().mapColor(MapColor.METAL).strength(2, 10)));
    public static final DeferredBlock<Block> WIRELESS_CHARGER = register("electrichargepad", () -> new BlockChargePad(blockProperties().mapColor(MapColor.METAL).strength(2, 10)));

    // 1.21.5: Block/Item Properties require setId() before the constructor runs. Stash the
    // ResourceKey in a ThreadLocal during each factory invocation; blockProperties() reads it.
    private static final ThreadLocal<ResourceKey<Block>> CURRENT_BLOCK_KEY = new ThreadLocal<>();

    public static BlockBehaviour.Properties blockProperties() {
        BlockBehaviour.Properties p = BlockBehaviour.Properties.of();
        ResourceKey<Block> k = CURRENT_BLOCK_KEY.get();
        if (k != null) p.setId(k);
        return p;
    }

    private static <BLOCK extends Block> DeferredBlock<BLOCK> register(final String name, final Supplier<BLOCK> blockFactory) {
        DeferredBlock<BLOCK> block = BLOCKS.register(name, rl -> {
            CURRENT_BLOCK_KEY.set(ResourceKey.create(Registries.BLOCK, rl));
            try {
                return blockFactory.get();
            } finally {
                CURRENT_BLOCK_KEY.remove();
            }
        });
        ITEMS.registerSimpleBlockItem(block); // auto-sets the BlockItem id
        return block;
    }
}
