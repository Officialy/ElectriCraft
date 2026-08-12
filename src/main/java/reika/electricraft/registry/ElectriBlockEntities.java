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
            new BlockEntityType<>(BlockEntityBattery::new, ElectriBlocks.getBatteryBlocks()));
    //Was wrongly constructing BlockEntityBattery (the EC battery) — the RF battery got the wrong BE.
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<reika.electricraft.blockentities.modinterface.BlockEntityRFBattery>> RF_BATTERY = BLOCK_ENTITIES.register("rf_battery", () ->
            new BlockEntityType<>(reika.electricraft.blockentities.modinterface.BlockEntityRFBattery::new, ElectriBlocks.RFBATTERY.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityRFCable>> RF_CABLE = BLOCK_ENTITIES.register("rf_cable", () ->
            new BlockEntityType<>(BlockEntityRFCable::new, ElectriBlocks.RF_CABLE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityFuse>> FUSE = BLOCK_ENTITIES.register("fuse", () ->
            new BlockEntityType<>(BlockEntityFuse::new, ElectriBlocks.getFuseBlocks()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityGenerator>> GENERATOR = BLOCK_ENTITIES.register("generator", () ->
            new BlockEntityType<>(BlockEntityGenerator::new, ElectriBlocks.GENERATOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityMeter>> METER = BLOCK_ENTITIES.register("meter", () ->
            new BlockEntityType<>(BlockEntityMeter::new, ElectriBlocks.METER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityMotor>> MOTOR = BLOCK_ENTITIES.register("motor", () ->
            new BlockEntityType<>(BlockEntityMotor::new, ElectriBlocks.MOTOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityPreciseResistor>> PRECISE_RESISTOR = BLOCK_ENTITIES.register("precise_resistor", () ->
            new BlockEntityType<>(BlockEntityPreciseResistor::new, ElectriBlocks.PRECISE_RESISTOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityRelay>> RELAY = BLOCK_ENTITIES.register("relay", () ->
            new BlockEntityType<>(BlockEntityRelay::new, ElectriBlocks.RELAY.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityResistor>> RESISTOR = BLOCK_ENTITIES.register("resistor", () ->
            new BlockEntityType<>(BlockEntityResistor::new, ElectriBlocks.RESISTOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityTransformer>> TRANSFORMER = BLOCK_ENTITIES.register("transformer", () ->
            new BlockEntityType<>(BlockEntityTransformer::new, ElectriBlocks.TRANSFORMER.get()));
    /** One BE type serves the 18 concrete conductor blocks; the immutable block selects its variant. */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityWire>> WIRE = BLOCK_ENTITIES.register("wire", () ->
            new BlockEntityType<>(BlockEntityWire::new, ElectriBlocks.getWireBlocks()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityWirelessCharger>> WIRELESS_CHARGER = BLOCK_ENTITIES.register("wireless_charger", () ->
            new BlockEntityType<>(BlockEntityWirelessCharger::new, ElectriBlocks.WIRELESS_CHARGER.get()));

    /**
     * Exposes the FE-speaking blocks through the standard NeoForge block energy capability
     * (the 26.2 transfer-API EnergyHandler) so other mods' cables and machines can connect —
     * they never instanceof-check BEs. The RF battery is sided like 1.7.10: receives on every
     * face but the top, emits only on the top. The charge pad refuses its beam face.
     */
    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, RF_CABLE.get(),
                (be, side) -> be.getEnergyView());
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, RF_BATTERY.get(),
                (be, side) -> side == null ? be : new SidedRFBatteryView(be, side));
        event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, WIRELESS_CHARGER.get(),
                (be, side) -> side == null || be.canConnectEnergy(side.getOpposite()) ? be.getEnergyView() : null);
    }

    /** Receive-anywhere-but-top / extract-only-top view of the RF battery (legacy sidedness). */
    private record SidedRFBatteryView(reika.electricraft.blockentities.modinterface.BlockEntityRFBattery be,
                                      net.minecraft.core.Direction side) implements net.neoforged.neoforge.transfer.energy.EnergyHandler {
        @Override
        public long getAmountAsLong() {
            return be.getAmountAsLong();
        }

        @Override
        public long getCapacityAsLong() {
            return be.getCapacityAsLong();
        }

        @Override
        public int insert(int amt, net.neoforged.neoforge.transfer.transaction.TransactionContext tx) {
            return side == net.minecraft.core.Direction.UP ? 0 : be.insert(amt, tx);
        }

        @Override
        public int extract(int amt, net.neoforged.neoforge.transfer.transaction.TransactionContext tx) {
            return side == net.minecraft.core.Direction.UP ? be.extract(amt, tx) : 0;
        }
    }
}
