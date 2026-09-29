package reika.electricraft.modinterface.jei;

import java.util.HashSet;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import reika.electricraft.ElectriCraft;
import reika.electricraft.registry.BatteryType;
import reika.electricraft.registry.ElectriBlocks;

/** Distinguishes the charged and empty battery and wireless-charger stacks shown in the creative tab. */
@JeiPlugin
public final class ElectriJEIPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        var registered = new HashSet<Item>();
        for (BatteryType tier : BatteryType.batteryList) {
            Item battery = tier.getCraftedProduct().getItem();
            if (registered.add(battery))
                registration.registerFromDataComponentTypes(battery, DataComponents.CUSTOM_DATA);
        }
        registration.registerFromDataComponentTypes(ElectriBlocks.WIRELESS_CHARGER.get().asItem(),
                DataComponents.CUSTOM_DATA);
    }
}
