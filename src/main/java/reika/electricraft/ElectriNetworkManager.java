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

import net.minecraft.world.level.Level;
import reika.dragonapi.auxiliary.trackers.TickRegistry;
import reika.electricraft.auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import reika.electricraft.auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;

// 1.21.5: net.neoforged.neoforge.event.TickEvent and its inner Phase enum are gone;
// DragonAPI's TickRegistry now hosts its own Phase enum and dispatches ServerTickEvent.Pre/Post.
public class ElectriNetworkManager implements TickRegistry.TickHandler {

	public static final ElectriNetworkManager instance = new ElectriNetworkManager();

	private final Collection<NetworkObject> networks = new ArrayList<>();
	private final Collection<NetworkObject> discard = new ArrayList<>();

	private ElectriNetworkManager() {

	}

	@Override
	public void tick(TickRegistry.TickType type, Object... tickData) {
		TickRegistry.Phase phase = (TickRegistry.Phase) tickData[0];
		Level world = null;//todo plumb the current server level through; DimensionManager.getWorld(0) is gone in 1.21.5
		if (phase == TickRegistry.Phase.START) {
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
		else if (phase == TickRegistry.Phase.END) {
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
	public boolean canFire(TickRegistry.Phase p) {
		return p == TickRegistry.Phase.START || p == TickRegistry.Phase.END;
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

