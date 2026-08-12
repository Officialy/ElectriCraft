package reika.electricraft.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import reika.electricraft.ElectriCraft;
import reika.electricraft.renders.item.ElectriMachineItemRenderer;
import reika.electricraft.renders.item.ElectriCasingItemRenderer;
import reika.electricraft.renders.item.ElectriWireItemRenderer;
import reika.electricraft.renders.item.ElectriCableItemRenderer;

/** Client-only model-pipeline registrations. */
@EventBusSubscriber(modid = ElectriCraft.MODID, value = Dist.CLIENT)
public final class ElectriClientExtensions {

    private ElectriClientExtensions() {
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(ElectriMachineItemRenderer.ID, ElectriMachineItemRenderer.Unbaked.MAP_CODEC);
        event.register(ElectriCasingItemRenderer.ID, ElectriCasingItemRenderer.Unbaked.MAP_CODEC);
        event.register(ElectriWireItemRenderer.ID, ElectriWireItemRenderer.Unbaked.MAP_CODEC);
        event.register(ElectriCableItemRenderer.ID, ElectriCableItemRenderer.Unbaked.MAP_CODEC);
    }
}
