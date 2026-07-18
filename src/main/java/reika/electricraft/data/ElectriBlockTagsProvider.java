package reika.electricraft.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import reika.electricraft.ElectriCraft;
import reika.electricraft.registry.ElectriBlocks;

/**
 * ElectriCraft's ore blocks are registered with {@code requiresCorrectToolForDrops()}; without a
 * block tags provider they carry no {@code mineable/pickaxe} or tier tag and never drop in survival.
 * Iterate the block registry and tag every correct-tool block into {@code mineable/pickaxe} plus its
 * harvest tier, faithful to the 1.7.10 {@code ElectriOres.harvestLevel} (silver/platinum = 2 → iron,
 * the rest = 1 → stone).
 */
public class ElectriBlockTagsProvider extends BlockTagsProvider {

    public ElectriBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, ElectriCraft.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        var stone = tag(BlockTags.NEEDS_STONE_TOOL);
        var iron = tag(BlockTags.NEEDS_IRON_TOOL);
        for (var holder : ElectriBlocks.BLOCKS.getEntries()) {
            Block block = holder.get();
            if (!block.defaultBlockState().requiresCorrectToolForDrops())
                continue;
            pickaxe.add(holder.getKey());
            String name = holder.getId().getPath();
            if (name.contains("silver") || name.contains("platinum"))
                iron.add(holder.getKey());
            else
                stone.add(holder.getKey());
        }
    }
}
