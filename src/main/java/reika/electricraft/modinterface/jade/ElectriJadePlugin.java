package reika.electricraft.modinterface.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import reika.electricraft.ElectriCraft;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.electricraft.base.ElectriBlockEntity;
import reika.electricraft.base.WiringTile;
import reika.electricraft.blockentities.BlockEntityFuse;
import reika.electricraft.blockentities.BlockEntityGenerator;
import reika.electricraft.blockentities.BlockEntityMotor;
import reika.electricraft.blockentities.BlockEntityRelay;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blockentities.BlockEntityWire;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

import java.util.Locale;

/** Server-authoritative Jade diagnostics for ElectriCraft power networks and storage. */
@WailaPlugin(ElectriCraft.MODID)
public final class ElectriJadePlugin implements IWailaPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "machine_data");
    private static final String PREFIX = "electricraft_";

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ServerData.INSTANCE, Block.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(Tooltip.INSTANCE, Block.class);
    }

    private enum ServerData implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            BlockEntity be = accessor.getBlockEntity();
            if (!(be instanceof ElectriBlockEntity))
                return;
            data.putBoolean(key("present"), true);

            if (be instanceof WiringTile wiring) {
                data.putInt(key("voltage"), wiring.getWireVoltage());
                data.putInt(key("current"), wiring.getWireCurrent());
                data.putLong(key("power"), wiring.getWirePower());
                data.putInt(key("resistance"), wiring.getResistance());
            }
            if (be instanceof BlockEntityWire wire) {
                data.putString(key("wire_material"), wire.getWireType().name());
                data.putBoolean(key("insulated"), wire.insulated);
                data.putInt(key("max_current"), wire.getMaxCurrent());
            }
            if (be instanceof BatteryTile battery) {
                data.putLong(key("energy"), battery.getStoredEnergy());
                data.putLong(key("capacity"), battery.getMaxEnergy());
                data.putString(key("energy_unit"), battery.getUnitName());
            }
            if (be instanceof BlockEntityGenerator generator) {
                putMechanical(data, generator.getTorque(), generator.getOmega(), generator.getPower());
                data.putInt(key("output_voltage"), generator.getGenVoltage());
                data.putInt(key("output_current"), generator.getGenCurrent());
            } else if (be instanceof BlockEntityMotor motor) {
                putMechanical(data, motor.getTorque(), motor.getOmega(), motor.getPower());
            }
            if (be instanceof BlockEntityTransformer transformer) {
                data.putString(key("ratio"), transformer.getRatioForDisplay());
                data.putInt(key("output_voltage"), transformer.getGenVoltage());
                data.putInt(key("output_current"), transformer.getGenCurrent());
            }
            if (be instanceof BlockEntityResistorBase resistor)
                data.putInt(key("current_limit"), resistor.getCurrentLimit());
            if (be instanceof BlockEntityFuse fuse) {
                data.putInt(key("current_limit"), fuse.getMaxCurrent());
                data.putBoolean(key("overloaded"), fuse.isOverloaded());
            }
            if (be instanceof BlockEntityRelay relay)
                data.putBoolean(key("relay_enabled"), relay.isEnabled());
            if (be instanceof BlockEntityWirelessCharger charger) {
                var tier = charger.getTier();
                data.putString(key("charger_tier"), tier.name());
                data.putInt(key("throughput"), tier.maxThroughput);
                data.putFloat(key("efficiency"), tier.efficiency);
            }
        }

        private static void putMechanical(CompoundTag data, int torque, int omega, long power) {
            data.putInt(key("torque"), torque);
            data.putInt(key("omega"), omega);
            data.putLong(key("mechanical_power"), power);
        }

        @Override
        public Identifier getUid() {
            return UID;
        }
    }

    private enum Tooltip implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.getBooleanOr(key("present"), false))
                return;

            if (data.contains(key("wire_material"))) {
                String material = title(data.getStringOr(key("wire_material"), "wire"));
                String insulation = data.getBooleanOr(key("insulated"), false) ? "Insulated " : "Bare ";
                tooltip.add(Component.literal(insulation + material + " wire").withStyle(ChatFormatting.GRAY));
            }
            if (data.contains(key("energy"))) {
                long stored = data.getLongOr(key("energy"), 0L);
                long capacity = data.getLongOr(key("capacity"), 0L);
                String unit = data.getStringOr(key("energy_unit"), "J");
                tooltip.add(label("Energy: ").append(Component.literal(format(stored) + " / " + format(capacity) + " " + unit)
                        .withStyle(ChatFormatting.GREEN)));
            }
            if (data.contains(key("voltage")))
                tooltip.add(label("Voltage: ").append(value(data.getIntOr(key("voltage"), 0) + " V")));
            if (data.contains(key("current")))
                tooltip.add(label("Current: ").append(value(data.getIntOr(key("current"), 0) + " A")));
            if (data.contains(key("power")))
                tooltip.add(label("Electrical power: ").append(value(format(data.getLongOr(key("power"), 0L)) + " W")));
            if (data.contains(key("resistance"))) {
                int resistance = data.getIntOr(key("resistance"), 0);
                if (resistance > 0 && resistance < Integer.MAX_VALUE)
                    tooltip.add(label("Resistance: ").append(value(resistance + " Ω")));
            }
            if (data.contains(key("max_current")))
                tooltip.add(label("Conductor limit: ").append(value(data.getIntOr(key("max_current"), 0) + " A")));
            if (data.contains(key("current_limit")))
                tooltip.add(label("Current limit: ").append(value(data.getIntOr(key("current_limit"), 0) + " A")));
            if (data.contains(key("overloaded")) && data.getBooleanOr(key("overloaded"), false))
                tooltip.add(Component.literal("Blown").withStyle(ChatFormatting.RED));
            if (data.contains(key("relay_enabled")))
                tooltip.add(label("Relay: ").append(Component.literal(
                        data.getBooleanOr(key("relay_enabled"), false) ? "Closed" : "Open").withStyle(
                        data.getBooleanOr(key("relay_enabled"), false) ? ChatFormatting.GREEN : ChatFormatting.RED)));
            if (data.contains(key("ratio")))
                tooltip.add(label("Turns ratio: ").append(value(data.getStringOr(key("ratio"), "1:1"))));
            if (data.contains(key("output_voltage")))
                tooltip.add(label("Output: ").append(value(data.getIntOr(key("output_voltage"), 0) + " V / "
                        + data.getIntOr(key("output_current"), 0) + " A")));
            if (data.contains(key("mechanical_power"))) {
                tooltip.add(label("Torque: ").append(value(data.getIntOr(key("torque"), 0) + " Nm")));
                tooltip.add(label("Speed: ").append(value(data.getIntOr(key("omega"), 0) + " rad/s")));
                tooltip.add(label("Mechanical power: ").append(value(format(data.getLongOr(key("mechanical_power"), 0L)) + " W")));
            }
            if (data.contains(key("charger_tier"))) {
                tooltip.add(label("Tier: ").append(value(title(data.getStringOr(key("charger_tier"), "basic")))));
                int throughput = data.getIntOr(key("throughput"), 0);
                String limit = throughput == Integer.MAX_VALUE ? "unlimited" : format(throughput) + " RF/t";
                tooltip.add(label("Transfer: ").append(value(limit + " @ "
                        + Math.round(data.getFloatOr(key("efficiency"), 0F) * 100F) + "%")));
            }
        }

        @Override
        public Identifier getUid() {
            return UID;
        }
    }

    private static String key(String name) {
        return PREFIX + name;
    }

    private static MutableComponent label(String text) {
        return Component.literal(text).withStyle(ChatFormatting.GRAY);
    }

    private static Component value(String text) {
        return Component.literal(text).withStyle(ChatFormatting.WHITE);
    }

    private static String title(String value) {
        String lower = value.toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String format(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }
}
