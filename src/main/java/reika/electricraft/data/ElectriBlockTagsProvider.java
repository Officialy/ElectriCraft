package reika.electricraft.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import reika.electricraft.ElectriCraft;
import reika.electricraft.registry.ElectriBlocks;
import reika.dragonapi.libraries.level.LegacyMotionTags;

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
        for (var ore : reika.electricraft.registry.ElectriOres.oreList) {
            var key = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores/" + ore.name().toLowerCase(java.util.Locale.ROOT)));
            tag(key).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(ore.getBlock()).orElseThrow());
            if (ore.getDeepslateBlock() != null)
                tag(key).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getResourceKey(ore.getDeepslateBlock()).orElseThrow());
            tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                    net.minecraft.resources.Identifier.fromNamespaceAndPath("c", "ores"))).addTag(key);
        }
        // 26.3 made movement blocking, suffocation, fluid blocking and fluid washing tag-driven and
        // NeoForge tags no modded blocks; give every block its 26.2 behaviour (see LegacyMotionTags).
        var motionTag = tag(BlockTags.BLOCKS_MOTION_NO_LEAVES);
        var leafTag = tag(BlockTags.LEAVES);
        var washedTag = tag(BlockTags.WASHED_AWAY_BY_FLUIDS);
        // 1.7.10 ElectriBlocks#getBlockMaterial: rock for ores, iron for everything else (wires included),
        // so every non-fluid block blocked movement, stopped fluids and was never washed away.
        LegacyMotionTags.classifyEntries(ElectriBlocks.BLOCKS.getEntries(), block -> !(block instanceof LiquidBlock),
                motionTag::add, leafTag::add, washedTag::add);

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
