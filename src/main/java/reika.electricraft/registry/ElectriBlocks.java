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

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blocks.*;

import java.util.function.Supplier;

public class ElectriBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ElectriCraft.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ElectriCraft.MODID);

    public static final RegistryObject<Block> WIRE = register("wire", () -> new BlockWire(BlockBehaviour.Properties.of(Material.METAL).strength(0.05F, 2F).sound(SoundType.WOOL).noOcclusion()));
    public static final RegistryObject<Block> GENERATOR = register("converter", () -> new BlockElectricGenerator(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> MOTOR = register("motor", () -> new BlockElectricMotor(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> METER = register("meter", () -> new BlockElectricMeter(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> PRECISE_RESISTOR = register("precise_resistor", () -> new BlockElectricPreciseResistor(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> TRANSFORMER = register("transformer", () -> new BlockElectricTransformer(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> FUSE = register("fuse", () -> new BlockElectricFuse(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> RESISTOR = register("resistor", () -> new BlockElectricResistor(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Block> RELAY = register("relay", () -> new BlockElectricRelay(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));

    public static final RegistryObject<Block> ORE = register("electriore", () -> new BlockElectriOre(BlockBehaviour.Properties.of(Material.METAL).strength(2, 5)));
    public static final RegistryObject<Block> BATTERY = register("electribattery", () -> new BlockElectricBattery(BlockBehaviour.Properties.of(Material.METAL).strength(2, 10).noOcclusion()));
    public static final RegistryObject<Block> RF_CABLE = register("rfcable", () -> new BlockRFCable(BlockBehaviour.Properties.of(Material.METAL)));
    public static final RegistryObject<Block> RFBATTERY = register("electrirfbattery", () -> new BlockRFBattery(BlockBehaviour.Properties.of(Material.METAL).strength(2, 10)));
    //	 public static final RegistryObject<Block> EUSPLIT = register(BlockEUSplitter.class, "EUSplitter", false),
//	 public static final RegistryObject<Block> EUCABLE = register(BlockEUCable.class, "EUCable", false),
//	 public static final RegistryObject<Block> EUBATTERY = register(BlockEUBattery.class, "ElectriEUBattery", true),
    public static final RegistryObject<Block> WIRELESS_CHARGER = register("electrichargepad", () -> new BlockChargePad(BlockBehaviour.Properties.of(Material.METAL).strength(2, 10)));

    private static <BLOCK extends Block> RegistryObject<BLOCK> register(final String name, final Supplier<BLOCK> blockFactory) {
        return registerBlock(name, blockFactory, block -> new BlockItem(block, defaultItemProperties()));
    }

    private static <BLOCK extends Block> RegistryObject<BLOCK> registerBlock(final String name, final Supplier<BLOCK> blockFactory, final IBlockItemFactory<BLOCK> itemFactory) {
        final RegistryObject<BLOCK> block = BLOCKS.register(name, blockFactory);

        ITEMS.register(name, () -> itemFactory.create(block.get()));

        return block;
    }

    private static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }

    @FunctionalInterface
    private interface IBlockItemFactory<BLOCK extends Block> {
        Item create(BLOCK block);
    }

}
