package reika.electricraft.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

/** Stitches the original plural-directory cable/wire sprites into the 26.3 block atlas. */
public final class ElectriLegacyBlockAtlasProvider implements DataProvider {
    private final PackOutput.PathProvider paths;
    public ElectriLegacyBlockAtlasProvider(PackOutput output) {
        paths = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }
    @Override public CompletableFuture<?> run(CachedOutput cache) {
        var atlas = new JsonObject();
        var sources = new JsonArray();
        var legacy = new JsonObject();
        legacy.addProperty("type", "minecraft:directory");
        legacy.addProperty("source", "blocks");
        legacy.addProperty("prefix", "blocks/");
        sources.add(legacy);
        atlas.add("sources", sources);
        return DataProvider.saveStable(cache, atlas, paths.json(Identifier.withDefaultNamespace("blocks")));
    }
    @Override public String getName() { return "ElectriCraft Legacy Block Atlas"; }
}
