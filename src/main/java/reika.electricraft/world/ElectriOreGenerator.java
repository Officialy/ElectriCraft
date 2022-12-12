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

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import reika.dragonapi.interfaces.OreGenerator;
import reika.electricraft.registry.ElectriOres;

public class ElectriOreGenerator {

	public static final ElectriOreGenerator instance = new ElectriOreGenerator();

	public final ArrayList<OreGenerator> generators = new ArrayList<>();

	private ElectriOreGenerator() {
		generators.add(new BasicElectriOreGenerator());
	}

	public void generate(Random random, int chunkX, int chunkZ, Level world, ChunkAccess chunkgen) {
		for (int i = 0; i < ElectriOres.oreList.length; i++) {
			ElectriOres ore = ElectriOres.oreList[i];
			if (ore.canGenerateInChunk(world, chunkX, chunkZ)) {
				for (OreGenerator gen : generators) {
					gen.generateOre(ore, random, world, chunkX, chunkZ);
				}
			}
		}
	}

}
