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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import reika.dragonapi.libraries.level.LegacyOreVeins;
import reika.electricraft.registry.ElectriOres;
import reika.electricraft.registry.ElectriOptions;

/** Original per-ore scatter, configured by generated feature data. Vanilla owns copper. */
public record BasicElectriOreGenerator(ElectriOres ore, int count, int size, int minY, int maxY) implements Feature {
    public static final MapCodec<BasicElectriOreGenerator> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.xmap(ElectriOres::valueOf, ElectriOres::name).fieldOf("ore").forGetter(BasicElectriOreGenerator::ore),
            Codec.intRange(1,1024).fieldOf("count").forGetter(BasicElectriOreGenerator::count),
            Codec.intRange(1,128).fieldOf("size").forGetter(BasicElectriOreGenerator::size),
            Codec.INT.fieldOf("min_y").forGetter(BasicElectriOreGenerator::minY),
            Codec.INT.fieldOf("max_y").forGetter(BasicElectriOreGenerator::maxY)
    ).apply(i,BasicElectriOreGenerator::new));

    public BasicElectriOreGenerator(ElectriOres ore) { this(ore,ore.perChunk,ore.veinSize,ore.minY,ore.maxY); }
    @Override public MapCodec<BasicElectriOreGenerator> codec() { return CODEC; }

    @Override public boolean place(WorldGenLevel world, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        if (ore == ElectriOres.COPPER || !ore.isValidDimension(world.getLevel().dimension()) || !ore.isOreEnabled()) return false;
        int discrete = Math.max(1,ElectriOptions.DISCRETE.getValue());
        if (random.nextInt(discrete) != 0) return false;
        boolean placed = false;
        for (int i=0; i<count*discrete; i++) {
            int x=origin.getX()+random.nextInt(16), z=origin.getZ()+random.nextInt(16);
            int y=reika.dragonapi.libraries.level.WorldgenHeight.randomBetween(world,random,minY,maxY);
            var stoneOre = ore.getBlock().defaultBlockState();
            var deepBlock = ore.getDeepslateBlock();
            placed |= LegacyOreVeins.place(world,random,stoneOre,
                    deepBlock == null ? stoneOre : deepBlock.defaultBlockState(),size,0,x,y,z);
        }
        return placed;
    }
}
