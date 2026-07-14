package reika.electricraft.registry;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;
import reika.rotarycraft.gui.container.machine.BlankContainer;

/**
 * ElectriCraft menu types. The GUIs (transformer ratio, RF cable throughput limit) carry no
 * inventory — they are settings screens — so both reuse RotaryCraft's slot-less
 * {@link BlankContainer} and push their values to the server through the DragonAPI packet channel.
 */
public interface ElectriMenus {

    DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, ElectriCraft.MODID);

    static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(String id, IContainerFactory<T> factory) {
        return REGISTRY.register(id, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }

    Supplier<MenuType<BlankContainer<BlockEntityTransformer>>> TRANSFORMER = register("transformer", (id, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        BlockEntity te = inv.player.level().getBlockEntity(pos);
        return new BlankContainer<>(ElectriMenus.TRANSFORMER.get(), id, inv, (BlockEntityTransformer) te);
    });

    Supplier<MenuType<BlankContainer<BlockEntityRFCable>>> RF_CABLE = register("rf_cable", (id, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        BlockEntity te = inv.player.level().getBlockEntity(pos);
        return new BlankContainer<>(ElectriMenus.RF_CABLE.get(), id, inv, (BlockEntityRFCable) te);
    });

    static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }

}
