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
import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.electricraft.auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
import reika.electricraft.auxiliary.interfaces.CurrentThrottle;
import reika.electricraft.auxiliary.interfaces.Overloadable;
import reika.electricraft.auxiliary.interfaces.ToggledConnection;
import reika.electricraft.auxiliary.interfaces.WireEmitter;
import reika.electricraft.auxiliary.interfaces.WireFuse;
import reika.electricraft.auxiliary.interfaces.WireReceiver;
import reika.electricraft.base.WiringTile;

public final class WirePath {

	final LinkedList<WiringTile> nodes = new LinkedList();
	final Collection<WireFuse> fuses = new ArrayList<>();
	final Collection<ToggledConnection> toggles = new ArrayList<>();
	final WireEmitter start;
	final WireReceiver end;
	private final WireNetwork net;

	public final int resistance;
	private final int currentLimit;

	private final HashSet<ResourceKey<Level>> dimensions = new HashSet<>();

	public WirePath(LinkedList<WorldLocation> points, WireEmitter start, WireReceiver end, WireNetwork net) {
		this.start = start;
		this.end = end;
		this.net = net;
		this.verify();
		int maxcurrent = Integer.MAX_VALUE;
		int r = 0;
		for (WorldLocation loc : points) {
			WiringTile te = (WiringTile)loc.getBlockEntity();
			if (te == null)
				continue;
			nodes.addLast(te);
			dimensions.add(loc.getDimension());
			r += te.getResistance();
			if (te instanceof CurrentThrottle) {
				int max = ((CurrentThrottle)te).getCurrentLimit();
				if (max < maxcurrent)
					maxcurrent = max;
			}
			if (te instanceof WireFuse) {
				fuses.add((WireFuse)te);
			}
			if (te instanceof ToggledConnection) {
				toggles.add((ToggledConnection)te);
			}
		}
		resistance = r;
		currentLimit = maxcurrent;
		//ReikaJavaLibrary.pConsole(points, Dist.DEDICATED_SERVER);
	}

	private void verify() {
		if (start == null || end == null)
			throw new IllegalArgumentException("Cannot connect null points!");
		if (start.getWorld() != end.getWorld())
			;//throw new IllegalArgumentException("Cannot connect points across dimensions!");
	}

	public void overload(boolean intersect) {
		for (WiringTile w : nodes) {
			if (intersect || net.getNodeAt(w).getPaths() == 1) {
				if (w instanceof Overloadable)
					((Overloadable)w).overload(this.getPathCurrent());
			}
		}
	}

	public int getLength() {
		return nodes.size();
	}

	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	public int getVoltageAt(WiringTile wire) {
		return start.canEmitPower() ? start.getGenVoltage() > 0 ? Math.max(0, start.getGenVoltage()-this.getResistanceTo(wire)) : 0 : 0;
	}

	private int getResistanceTo(WiringTile wire) {
		return this.getResistanceTo(nodes.indexOf(wire));
	}

	private int getResistanceTo(int index) {
		int r = 0;
		for (int i = 0; i < index; i++) {
			WiringTile wire = nodes.get(i);
			r += wire.getResistance();
		}
		return r;
	}

	@Override
	public String toString() {/*
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		for (int i = 0; i < nodes.size(); i++) {
			WiringTile w = nodes.get(i);
			sb.append("[");
			sb.append(w.xCoord);
			sb.append(":");
			sb.append(w.yCoord);
			sb.append(":");
			sb.append(w.zCoord);
			sb.append("]");
		}
		sb.append(">");
		return sb.toString();*/
		String s1 = start.toString().replaceAll("BlockEntity", "").replaceAll("Tile Entity", "");
		String s2 = end.toString().replaceAll("BlockEntity", "").replaceAll("Tile Entity", "");
		while (s1.charAt(0) == ' ') {
			s1 = s1.substring(1);
		}
		while (s2.charAt(0) == ' ') {
			s2 = s2.substring(1);
		}
		return "("+s1+") > ("+s2+") {"+this.hashCode()+"}";
	}

	public boolean containsBlock(WiringTile te) {
		return nodes.contains(te);
	}

	boolean tick(ElectriNetworkTickEvent evt) {
		int current = this.getPathCurrent();
		boolean flag = false;
		for (WireFuse f : fuses) {
			if (current > f.getMaxCurrent()) {
				f.overload(current);
				flag = true;
			}
		}
		return flag;
	}

	public boolean startsAt(int x, int y, int z) {
		return start.getX() == x && y == start.getY() && z == start.getZ();
	}

	public boolean endsAt(int x, int y, int z) {
		return x == end.getX() && y == end.getY() && z == end.getZ();
	}

	public int getTerminalVoltage() {
		int v = start.getGenVoltage() > 0 ? start.getGenVoltage()-this.getVoltageLoss() : 0;
		return v > 0 ? v : 0;
	}

	public int getVoltageLoss() {
		return resistance;
	}

	public boolean isLimitedCurrent() {
		return currentLimit < Integer.MAX_VALUE;
	}

	public int getPathCurrent() {
		if (!start.canEmitPower())
			return 0;
		int total = start.getGenCurrent();
		int num = net.getNumberPathsStartingAt(start);
		int frac = total/num;
		int bonus = 0;
		int unfilled = 0;
		ArrayList<WirePath> li = net.getPathsStartingAt(start);
		for (WirePath path : li) {
			if (path.isLimitedCurrent()) {
				int max = path.currentLimit;
				if (max <= frac) {
					bonus += frac-max;
				}
				else {
					unfilled++;
				}
			}
			else {
				unfilled++;
			}
		}
		int frac2 = unfilled > 0 ? bonus/unfilled : 0;
		//ReikaJavaLibrary.pConsole(bonus+"/"+num2+"="+frac2, currentLimit == 2000);
		return Math.min(frac+frac2, currentLimit);
	}

	Collection<ResourceKey<Level>> getDimensions() {
		return Collections.unmodifiableSet(dimensions);
	}

	public boolean canConduct() {
		for (ToggledConnection tc : toggles) {
			if (!tc.canConnect())
				return false;
		}
		return true;
	}

}
