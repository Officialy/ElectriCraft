package reika.electricraft.data;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import reika.electricraft.registry.ElectriOres;
import reika.electricraft.world.BasicElectriOreGenerator;

public final class ElectriWorldGenProvider implements DataProvider {
    private final PackOutput.PathProvider paths;
    public ElectriWorldGenProvider(PackOutput output) { paths=output.createPathProvider(PackOutput.Target.DATA_PACK,"neoforge/biome_modifier"); }
    private static Identifier id(ElectriOres ore) { return Identifier.fromNamespaceAndPath("electricraft",ore.name().toLowerCase(Locale.ROOT)+"_ore"); }
    public static RegistrySetBuilder buildRegistrySet() {
        return new RegistrySetBuilder().add(Registries.FEATURE,context -> {
            for (var ore:ElectriOres.oreList) if (ore!=ElectriOres.COPPER)
                context.register(ResourceKey.create(Registries.FEATURE,id(ore)),new BasicElectriOreGenerator(ore));
        }).add(Registries.PLACED_FEATURE,context -> {
            var features=context.lookup(Registries.FEATURE);
            for (var ore:ElectriOres.oreList) if (ore!=ElectriOres.COPPER)
                context.register(ResourceKey.create(Registries.PLACED_FEATURE,id(ore)),new PlacedFeature(features.getOrThrow(ResourceKey.create(Registries.FEATURE,id(ore))),List.of()));
        });
    }
    @Override public CompletableFuture<?> run(CachedOutput cache) {
        var tasks=new ArrayList<CompletableFuture<?>>();
        for (var ore:ElectriOres.oreList) if (ore!=ElectriOres.COPPER) {
            JsonObject json=new JsonObject(); json.addProperty("type","neoforge:add_features");
            json.addProperty("biomes","#minecraft:is_overworld");json.addProperty("features",id(ore).toString());json.addProperty("step","underground_ores");
            tasks.add(DataProvider.saveStable(cache,json,paths.json(id(ore))));
        }
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }
    @Override public String getName() { return "ElectriCraft ore biome modifiers"; }
}
