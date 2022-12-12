/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary;


import net.minecraft.world.level.Level;

public class ElectriNetworkEvent {

	public final Level networkWorld;
	public final long worldTime;
	public final long systemTime;

	public ElectriNetworkEvent(Level world) {
		networkWorld = world;
		worldTime = world.getGameTime();
		systemTime = System.currentTimeMillis();
	}

	public static class ElectriNetworkTickEvent extends ElectriNetworkEvent {

		private static int currentIndex = 0;
		public final int tickIndex;

		public ElectriNetworkTickEvent(Level world) {
			super(world);
			tickIndex = currentIndex;
			currentIndex++;
		}

	}

	public static class ElectriNetworkRepathEvent extends ElectriNetworkEvent {

		public ElectriNetworkRepathEvent(Level world) {
			super(world);
		}

	}
}
