/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


import net.minecraft.core.Direction;
import reika.electricraft.base.WiringTile;

public final class NetworkNode {

	public final int x;
	public final int y;
	public final int z;

	private final WiringTile wire;

	private final WireNetwork network;

	private final ArrayList<Direction> connections = new ArrayList<>();

	private final ArrayList<WirePath> paths = new ArrayList<>();

	NetworkNode(WireNetwork w, WiringTile te, Collection<Direction> sides) {
		network = w;
		x = te.getX();
		y = te.getY();
		z = te.getZ();

		wire = te;
		connections.addAll(sides);
	}

	void addPath(WirePath p) {
		paths.add(p);
	}

	void removePath(WirePath p) {
		paths.remove(p);
	}

	public boolean connectsToSide(Direction dir) {
		return connections.contains(dir);
	}

	@Override
	public String toString() {
		return "<"+x+":"+y+":"+z+">";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NetworkNode) {
			NetworkNode n = (NetworkNode)o;
			return n.wire == wire && n.network == network && n.connections.equals(connections);
		}
		return false;
	}

	public Collection<Direction> getDirections() {
		return Collections.unmodifiableCollection(connections);
	}

	public int getPaths() {
		return paths.size();
	}

}
