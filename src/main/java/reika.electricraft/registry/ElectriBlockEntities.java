package reika.electricraft.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.registries.DeferredRegister;
import net.neoforged.registries.ForgeRegistries;
import net.neoforged.registries.RegistryObject;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.*;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;
import reika.rotarycraft.RotaryCraft;

public class ElectriBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ElectriCraft.MODID);

    public static final RegistryObject<BlockEntityType<BlockEntityBattery>> BATTERY = BLOCK_ENTITIES.register("battery", () ->
            BlockEntityType.Builder.of(BlockEntityBattery::new, ElectriBlocks.BATTERY.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityBattery>> RF_BATTERY = BLOCK_ENTITIES.register("rf_battery", () ->
            BlockEntityType.Builder.of(BlockEntityBattery::new, ElectriBlocks.RFBATTERY.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockEntityRFCable>> RF_CABLE = BLOCK_ENTITIES.register("rf_cable", () ->
            BlockEntityType.Builder.of(BlockEntityRFCable::new, ElectriBlocks.RF_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityFuse>> FUSE = BLOCK_ENTITIES.register("fuse", () ->
            BlockEntityType.Builder.of(BlockEntityFuse::new, ElectriBlocks.FUSE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityGenerator>> GENERATOR = BLOCK_ENTITIES.register("generator", () ->
            BlockEntityType.Builder.of(BlockEntityGenerator::new, ElectriBlocks.GENERATOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityMeter>> METER = BLOCK_ENTITIES.register("meter", () ->
            BlockEntityType.Builder.of(BlockEntityMeter::new, ElectriBlocks.METER.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityMotor>> MOTOR = BLOCK_ENTITIES.register("motor", () ->
            BlockEntityType.Builder.of(BlockEntityMotor::new, ElectriBlocks.MOTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityPreciseResistor>> PRECISE_RESISTOR = BLOCK_ENTITIES.register("precise_resistor", () ->
            BlockEntityType.Builder.of(BlockEntityPreciseResistor::new, ElectriBlocks.RESISTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityRelay>> RELAY = BLOCK_ENTITIES.register("relay", () ->
            BlockEntityType.Builder.of(BlockEntityRelay::new, ElectriBlocks.RELAY.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityResistor>> RESISTOR = BLOCK_ENTITIES.register("resistor", () ->
            BlockEntityType.Builder.of(BlockEntityResistor::new, ElectriBlocks.RESISTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityTransformer>> TRANSFORMER = BLOCK_ENTITIES.register("transformer", () ->
            BlockEntityType.Builder.of(BlockEntityTransformer::new, ElectriBlocks.TRANSFORMER.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityWire>> WIRE = BLOCK_ENTITIES.register("wire", () ->
            BlockEntityType.Builder.of(BlockEntityWire::new, ElectriBlocks.WIRE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockEntityWirelessCharger>> WIRELESS_CHARGER = BLOCK_ENTITIES.register("wireless_charger", () ->
            BlockEntityType.Builder.of(BlockEntityWirelessCharger::new, ElectriBlocks.WIRELESS_CHARGER.get()).build(null));
}