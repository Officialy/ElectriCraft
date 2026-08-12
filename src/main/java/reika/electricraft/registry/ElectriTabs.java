package reika.electricraft.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.BlockEntityFuse;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;

import java.util.ArrayList;
import java.util.List;

/**
 * The ElectriCraft creative tab. Wire material, battery tier, and fuse amperage are concrete modern
 * registry identities; only genuinely mutable state such as stored energy remains stack data.
 */
public final class ElectriTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ElectriCraft.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("electricraft",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("tab.electricraft"))
                    .icon(() -> WireType.COPPER.getCraftedProduct())
                    .displayItems((params, output) -> {
                        for (WireType type : WireType.wireList) {
                            output.accept(type.getCraftedProduct());
                            output.accept(type.getCraftedInsulatedProduct());
                        }
                        for (ItemStack battery : createBatteryVariants())
                            output.accept(battery);
                        for (int limit : BlockEntityFuse.TIERS)
                            output.accept(ElectriBlocks.getFuseBlock(limit).get());
                        output.accept(ElectriBlocks.GENERATOR.get());
                        output.accept(ElectriBlocks.MOTOR.get());
                        output.accept(ElectriBlocks.RELAY.get());
                        output.accept(ElectriBlocks.RESISTOR.get());
                        output.accept(ElectriBlocks.PRECISE_RESISTOR.get());
                        output.accept(ElectriBlocks.METER.get());
                        output.accept(ElectriBlocks.TRANSFORMER.get());
                        output.accept(ElectriBlocks.RF_CABLE.get());
                        output.accept(ElectriItems.RFBATTERY.get());
                        for (int i = 0; i < BlockEntityWirelessCharger.ChargerTiers.tierList.length; i++) {
                            ItemStack is = new ItemStack(ElectriBlocks.WIRELESS_CHARGER.get());
                            final int t = i;
                            ReikaItemHelper.updateStackTag(is, tag -> tag.putInt("tier", t));
                            output.accept(is);
                        }
                        output.accept(ElectriItems.TIN_INGOT.get());
                        output.accept(ElectriItems.SILVER_INGOT.get());
                        output.accept(ElectriItems.NICKEL_INGOT.get());
                        output.accept(ElectriItems.ALUMINUM_INGOT.get());
                        output.accept(ElectriItems.PLATINUM_INGOT.get());
                        output.accept(ElectriBlocks.TIN_ORE.get());
                        output.accept(ElectriBlocks.SILVER_ORE.get());
                        output.accept(ElectriBlocks.NICKEL_ORE.get());
                        output.accept(ElectriBlocks.ALUMINUM_ORE.get());
                        output.accept(ElectriBlocks.PLATINUM_ORE.get());
                        output.accept(ElectriBlocks.DEEPSLATE_SILVER_ORE.get());
                        output.accept(ElectriBlocks.DEEPSLATE_NICKEL_ORE.get());
                        output.accept(ElectriBlocks.DEEPSLATE_PLATINUM_ORE.get());
                        output.accept(ElectriItems.BLUE_DUST.get());
                        output.accept(ElectriItems.DIAMOND_DUST.get());
                        output.accept(ElectriItems.QUARTZ_DUST.get());
                        output.accept(ElectriItems.CRYSTAL_DUST.get());
                        for (BatteryType tier : BatteryType.batteryList) {
                            output.accept(ElectriItems.getCrystal(tier).get());
                        }
                        output.accept(ElectriItems.CRYSTAL_RF.get());
                        output.accept(ElectriItems.BOOK.get());
                    })
                    .build());

    public static void init(IEventBus bus) {
        TABS.register(bus);
    }

    /** Empty and filled presentation stacks for each concrete battery tier. */
    public static List<ItemStack> createBatteryVariants() {
        List<ItemStack> batteries = new ArrayList<>(BatteryType.batteryList.length * 2);
        for (BatteryType tier : BatteryType.batteryList) {
            batteries.add(tier.getCraftedProduct());
            ItemStack full = tier.getCraftedProduct();
            ReikaItemHelper.updateStackTag(full, tag -> tag.putLong("nrg", tier.maxCapacity));
            batteries.add(full);
        }
        return batteries;
    }

    private ElectriTabs() {}

}
