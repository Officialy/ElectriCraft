package reika.electricraft.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import reika.electricraft.ElectriCraft;

/**
 * 26.1 datagen entry point for ElectriCraft.
 * <p>
 * NeoForge 26.x split {@link GatherDataEvent} into {@link GatherDataEvent.Client} and
 * {@link GatherDataEvent.Server}. Client-side providers (block-/item-models, lang) attach to
 * the client event; data-side providers (recipes, loot tables, tags) attach to the server one.
 */
@EventBusSubscriber(modid = ElectriCraft.MODID)
public final class ElectriDataProviders {

    private ElectriDataProviders() {}

    @SubscribeEvent
    public static void onGatherClient(GatherDataEvent.Client event) {
        event.createProvider(output -> new ElectriLang(output, "en_us"));
        event.createProvider(ElectriModelProvider::new);
    }

    @SubscribeEvent
    public static void onGatherServer(GatherDataEvent.Server event) {
        // 26.1: emit a "drops self" loot-table for every registered ElectriCraft block so the
        // vanilla loot-table validator stops failing with "Missing loottable". Recipes / tags
        // still TODO.
        event.createProvider(ElectriLootProvider::new);
    }
}
