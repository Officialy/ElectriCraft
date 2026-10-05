package reika.electricraft.data;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriItems;

/** New 26.3 milestones for the original ElectriCraft content (upstream had no achievement page). */
public final class ElectriAdvancementProvider extends AdvancementProvider {
    public ElectriAdvancementProvider() {
        super(List.of(Generator::new));
    }

    private enum Milestone {
        ROOT(() -> ElectriItems.BOOK.get(), "ElectriCraft", "Obtain the ElectriCraft Handbook"),
        READ_HANDBOOK(() -> ElectriItems.BOOK.get(), "Electrical Engineering", "Read the ElectriCraft Handbook"),
        GENERATOR(() -> ElectriBlocks.GENERATOR.get(), "From Motion to Current", "Obtain an Induction Generator"),
        COPPER_WIRE(() -> ElectriBlocks.COPPER_WIRE.get(), "Connecting Things", "Obtain Copper Wire"),
        INSULATED_WIRE(() -> ElectriBlocks.INSULATED_COPPER_WIRE.get(), "Covered Conductors", "Obtain Insulated Copper Wire"),
        MOTOR(() -> ElectriBlocks.MOTOR.get(), "Back to Motion", "Obtain an Induction Motor"),
        METER(() -> ElectriBlocks.METER.get(), "Measure Twice", "Obtain an Electric Meter"),
        RESISTOR(() -> ElectriBlocks.RESISTOR.get(), "Current Control", "Obtain a Resistor"),
        PRECISE_RESISTOR(() -> ElectriBlocks.PRECISE_RESISTOR.get(), "Fine Control", "Obtain a Precise Resistor"),
        RELAY(() -> ElectriBlocks.RELAY.get(), "Switching Current", "Obtain a Relay"),
        FUSE(() -> ElectriBlocks.FUSE_32A.get(), "Circuit Protection", "Obtain a 32 A Fuse"),
        TRANSFORMER(() -> ElectriBlocks.TRANSFORMER.get(), "Changing Voltage", "Obtain a Transformer"),
        REDSTONE_BATTERY(() -> ElectriBlocks.REDSTONE_BATTERY.get(), "Stored Current", "Obtain a Redstone Battery"),
        GLOWSTONE_BATTERY(() -> ElectriBlocks.GLOWSTONE_BATTERY.get(), "A Brighter Battery", "Obtain a Glowstone Battery"),
        LAPIS_BATTERY(() -> ElectriBlocks.LAPIS_BATTERY.get(), "Blue Reserve", "Obtain a Lapis Battery"),
        ENDER_BATTERY(() -> ElectriBlocks.ENDER_BATTERY.get(), "An Ender Reserve", "Obtain an Ender Battery"),
        DIAMOND_BATTERY(() -> ElectriBlocks.DIAMOND_BATTERY.get(), "Diamond Reserve", "Obtain a Diamond Battery"),
        STAR_BATTERY(() -> ElectriBlocks.STAR_BATTERY.get(), "Stellar Reserve", "Obtain a Star Battery"),
        SUPERCONDUCTOR(() -> ElectriBlocks.SUPERCONDUCTOR_WIRE.get(), "No Resistance", "Obtain Superconductor Wire"),
        RF_CABLE(() -> ElectriBlocks.RF_CABLE.get(), "Connecting Flux", "Obtain RF Cable"),
        RF_BATTERY(() -> ElectriItems.RFBATTERY.get(), "Flux Reserve", "Obtain an RF Battery"),
        WIRELESS_CHARGER(() -> ElectriBlocks.WIRELESS_CHARGER.get(), "Without a Plug", "Obtain a Wireless Charger");

        final Supplier<ItemLike> icon;
        final String title;
        final String description;

        Milestone(Supplier<ItemLike> icon, String title, String description) {
            this.icon = icon;
            this.title = title;
            this.description = description;
        }

        String key() { return name().toLowerCase(Locale.ROOT); }
    }

    public static void addTranslations(BiConsumer<String, String> add) {
        for (Milestone milestone : Milestone.values()) {
            add.accept("advancements.electricraft." + milestone.key() + ".title", milestone.title);
            add.accept("advancements.electricraft." + milestone.key() + ".description", milestone.description);
        }
    }

    private static final class Generator extends AdvancementSubProvider {
        Generator(BootstrapContext<Advancement> output) { super(output); }

        @Override
        public void generate() {
            AdvancementHolder root = null;
            for (Milestone milestone : Milestone.values()) {
                var icon = milestone.icon.get().asItem();
                var title = Component.translatable("advancements.electricraft." + milestone.key() + ".title");
                var description = Component.translatable("advancements.electricraft." + milestone.key() + ".description");
                var type = milestone == Milestone.STAR_BATTERY || milestone == Milestone.SUPERCONDUCTOR
                        ? AdvancementType.CHALLENGE : AdvancementType.TASK;
                var builder = Advancement.Builder.advancement();
                if (root == null)
                    builder.rootDisplay(icon, title, description, Identifier.withDefaultNamespace("textures/block/copper_block.png"), type, true, true, false);
                else
                    builder.parent(root).display(icon, title, description, type, true, true, false);
                Criterion<?> criterion = milestone == Milestone.READ_HANDBOOK
                        ? new Criterion<>(CriteriaTriggers.IMPOSSIBLE, new ImpossibleTrigger.TriggerInstance())
                        : InventoryChangeTrigger.TriggerInstance.hasItems(icon);
                builder.addCriterion("trigger", criterion);
                var advancement = builder.save(output, "electricraft:" + milestone.key());
                if (root == null) root = advancement;
            }
        }
    }
}
