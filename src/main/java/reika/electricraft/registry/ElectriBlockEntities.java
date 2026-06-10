package reika.electricraft.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.*;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;

// 1.21.5: ForgeRegistries → BuiltInRegistries; RegistryObject → DeferredHolder;
// BlockEntityType.Builder.of(...).build(null) → new BlockEntityType<>(factory, blocks…).
public class ElectriBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ElectriCraft.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityBattery>> BATTERY = BLOCK_ENTITIES.register("battery", () ->
            new BlockEntityType<>(BlockEntityBattery::new, ElectriBlocks.BATTERY.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityBattery>> RF_BATTERY = BLOCK_ENTITIES.register("rf_battery", () ->
            new BlockEntityType<>(BlockEntityBattery::new, ElectriBlocks.RFBATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityRFCable>> RF_CABLE = BLOCK_ENTITIES.register("rf_cable", () ->
            new BlockEntityType<>(BlockEntityRFCable::new, ElectriBlocks.RF_CABLE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFuse>> FUSE = BLOCK_ENTITIES.register("fuse", () ->
            new BlockEntityType<>(BlockEntityFuse::new, ElectriBlocks.FUSE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityGenerator>> GENERATOR = BLOCK_ENTITIES.register("generator", () ->
            new BlockEntityType<>(BlockEntityGenerator::new, ElectriBlocks.GENERATOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityMeter>> METER = BLOCK_ENTITIES.register("meter", () ->
            new BlockEntityType<>(BlockEntityMeter::new, ElectriBlocks.METER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityMotor>> MOTOR = BLOCK_ENTITIES.register("motor", () ->
            new BlockEntityType<>(BlockEntityMotor::new, ElectriBlocks.MOTOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityPreciseResistor>> PRECISE_RESISTOR = BLOCK_ENTITIES.register("precise_resistor", () ->
            new BlockEntityType<>(BlockEntityPreciseResistor::new, ElectriBlocks.RESISTOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityRelay>> RELAY = BLOCK_ENTITIES.register("relay", () ->
            new BlockEntityType<>(BlockEntityRelay::new, ElectriBlocks.RELAY.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityResistor>> RESISTOR = BLOCK_ENTITIES.register("resistor", () ->
            new BlockEntityType<>(BlockEntityResistor::new, ElectriBlocks.RESISTOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityTransformer>> TRANSFORMER = BLOCK_ENTITIES.register("transformer", () ->
            new BlockEntityType<>(BlockEntityTransformer::new, ElectriBlocks.TRANSFORMER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityWire>> WIRE = BLOCK_ENTITIES.register("wire", () ->
            new BlockEntityType<>(BlockEntityWire::new, ElectriBlocks.WIRE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityWirelessCharger>> WIRELESS_CHARGER = BLOCK_ENTITIES.register("wireless_charger", () ->
            new BlockEntityType<>(BlockEntityWirelessCharger::new, ElectriBlocks.WIRELESS_CHARGER.get()));
}
