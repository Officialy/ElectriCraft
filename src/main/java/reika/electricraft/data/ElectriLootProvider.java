package reika.electricraft.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import reika.electricraft.registry.ElectriBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 26.1 block loot-table data provider for ElectriCraft.
 *
 * <p>Same pattern as the RotaryCraft / GeoStrata providers — vanilla datagen fails if any
 * block in the registry is missing a loot-table entry. We iterate {@link ElectriBlocks#BLOCKS}
 * and emit {@code dropSelf} for blocks that have a BlockItem and a no-drop entry for the rest.</p>
 */
public final class ElectriLootProvider extends LootTableProvider {

    public ElectriLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(Blocks::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    private static final class Blocks extends BlockLootSubProvider {

        Blocks(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            for (var holder : ElectriBlocks.BLOCKS.getEntries()) {
                Block block = holder.get();
                if (block.asItem() == Items.AIR) {
                    this.add(block, noDrop());
                } else {
                    this.dropSelf(block);
                }
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            List<Block> blocks = new ArrayList<>();
            ElectriBlocks.BLOCKS.getEntries().forEach(holder -> blocks.add(holder.get()));
            return blocks;
        }
    }
}
