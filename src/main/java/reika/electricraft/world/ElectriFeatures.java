package reika.electricraft.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ElectriFeatures {
    public static final DeferredRegister<MapCodec<? extends Feature>> TYPES = DeferredRegister.create(Registries.FEATURE_TYPE,"electricraft");
    static {
        TYPES.register("ore", () -> BasicElectriOreGenerator.CODEC);
        TYPES.register("all_ores", () -> ElectriOreGenerator.CODEC);
    }
    private ElectriFeatures() {}
}
