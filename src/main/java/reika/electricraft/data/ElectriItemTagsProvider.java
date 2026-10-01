package reika.electricraft.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import reika.electricraft.ElectriCraft;
import reika.electricraft.registry.ElectriOres;

/** Publish the original ore/material dictionary entries as common item tags. */
public final class ElectriItemTagsProvider extends ItemTagsProvider {
    public ElectriItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup, ElectriCraft.MODID);
    }
    private static TagKey<Item> common(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
    @Override protected void addTags(HolderLookup.Provider lookup) {
        for (var ore : ElectriOres.oreList) {
            String material = ore.name().toLowerCase(java.util.Locale.ROOT);
            var oreTag = common("ores/" + material);
            tag(oreTag).add(BuiltInRegistries.ITEM.getResourceKey(ore.getBlock().asItem()).orElseThrow());
            if (ore.getDeepslateBlock() != null)
                tag(oreTag).add(BuiltInRegistries.ITEM.getResourceKey(ore.getDeepslateBlock().asItem()).orElseThrow());
            tag(common("ores")).addTag(oreTag);
            var productTag = common("ingots/" + material);
            tag(productTag).add(BuiltInRegistries.ITEM.getResourceKey(ore.getProductItem()).orElseThrow());
            tag(common("ingots")).addTag(productTag);
        }
    }
}
