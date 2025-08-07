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

import java.util.Random;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import reika.dragonapi.interfaces.OreGenerator;
import reika.dragonapi.interfaces.registry.OreEnum;
import reika.electricraft.registry.ElectriOptions;
import reika.electricraft.registry.ElectriOres;

public class BasicElectriOreGenerator implements OreGenerator {

	@Override
	public void generateOre(OreEnum ore, Random random, Level world, int chunkX, int chunkZ) {
		ElectriOres e = (ElectriOres)ore;
		if (random.nextInt(ElectriOptions.DISCRETE.getValue()) == 0) {
			this.generate(e, world, random, chunkX*16, chunkZ*16);
		}
	}

	private void generate(ElectriOres ore, Level world, Random random, int chunkX, int chunkZ) {
		//ReikaJavaLibrary.pConsole("Generating "+ore);
		//ReikaJavaLibrary.pConsole(chunkX+", "+chunkZ);
		Block id = ore.getBlock();
		int passes = ore.perChunk* ElectriOptions.DISCRETE.getValue();
		for (int i = 0; i < passes; i++) {
			int posX = chunkX+random.nextInt(16);
			int posZ = chunkZ+random.nextInt(16);
			int posY = ore.minY+random.nextInt(ore.maxY-ore.minY+1);


		/*	if (ore.canGenAt(world, posX, posY, posZ)) {
				if ((new WorldGenMinable(id, ore.veinSize, ore.getReplaceableBlock())).generate(world, random, posX, posY, posZ))
					;//ReikaJavaLibrary.pConsole(ore+" @ "+posX+", "+posY+", "+posZ, ore == ElectriOres.MAGNETITE);
			}*/
		}
	}

}
