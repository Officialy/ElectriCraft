/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

<<<<<<< Updated upstream:ElectriNetworkManager.java
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
=======
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.Level;
import net.neoforged.event.TickEvent;
import reika.dragonapi.auxiliary.trackers.TickRegistry;
import reika.electricraft.auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import reika.electricraft.auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
>>>>>>> Stashed changes:src/main/java/reika.electricraft/ElectriNetworkManager.java

public class ElectriNetworkManager implements TickRegistry.TickHandler {

	public static final ElectriNetworkManager instance = new ElectriNetworkManager();

	private final Collection<NetworkObject> networks = new ArrayList<>();
	private final Collection<NetworkObject> discard = new ArrayList<>();

	private ElectriNetworkManager() {

	}

	@Override
	public void tick(TickRegistry.TickType type, Object... tickData) {
		TickEvent.Phase phase = (TickEvent.Phase) tickData[0];
		Level world = null;//todo DimensionManager.getWorld(0);
		//ReikaJavaLibrary.pConsole(networks.size()+":"+networks);
		if (phase == TickEvent.Phase.START) {
			if (!discard.isEmpty()) {
				networks.removeAll(discard);
				discard.clear();
			}
			if (world != null) {
				ElectriNetworkTickEvent evt = new ElectriNetworkTickEvent(world);
				for (NetworkObject net : networks) {
					net.tick(evt);
				}
			}
		}
		else if (phase == TickEvent.Phase.END) {
			if (world != null) {
				ElectriNetworkRepathEvent evt = new ElectriNetworkRepathEvent(world);
				for (NetworkObject net : networks) {
					net.repath(evt);
				}
			}
		}
	}

	@Override
	public EnumSet<TickRegistry.TickType> getType() {
		return EnumSet.of(TickRegistry.TickType.SERVER);
	}

	@Override
	public boolean canFire(TickEvent.Phase p) {
		return p == TickEvent.Phase.START || p == TickEvent.Phase.END;
	}

	@Override
	public String getLabel() {
		return "Electri Network";
	}

	public void addNetwork(NetworkObject net) {
		networks.add(net);
	}

	public void scheduleNetworkDiscard(NetworkObject net) {
		discard.add(net);
	}

}
