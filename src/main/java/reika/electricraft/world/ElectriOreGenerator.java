/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import reika.electricraft.registry.ElectriOres;

/** Combined generator retained for integrations; natural generation registers each ore separately. */
public final class ElectriOreGenerator implements Feature {
    public static final ElectriOreGenerator instance = new ElectriOreGenerator();
    public static final MapCodec<ElectriOreGenerator> CODEC = MapCodec.unit(() -> instance);
    private ElectriOreGenerator() {}
    @Override public MapCodec<ElectriOreGenerator> codec() { return CODEC; }
    @Override public boolean place(WorldGenLevel world, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        boolean placed=false;
        for (ElectriOres ore : ElectriOres.oreList)
            if (ore != ElectriOres.COPPER) placed |= new BasicElectriOreGenerator(ore).place(world,generator,random,origin);
        return placed;
    }
}
