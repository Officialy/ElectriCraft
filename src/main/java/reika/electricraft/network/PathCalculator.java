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
import java.util.LinkedList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import reika.dragonapi.exception.WorldSanityException;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.electricraft.auxiliary.interfaces.WireEmitter;
import reika.electricraft.auxiliary.interfaces.WireReceiver;
import reika.electricraft.base.BlockEntityWireComponent;
import reika.electricraft.base.WiringTile;
import reika.electricraft.registry.ElectriTiles;

public class PathCalculator {

	private final WireEmitter start;
	private final WireReceiver end;
	private final WireNetwork net;

	private final ArrayList<WirePath> paths = new ArrayList<>();
	private final ArrayList<WorldLocation> rifts = new ArrayList<>();

	public PathCalculator(WireEmitter start, WireReceiver end, WireNetwork w) {
		this.start = start;
		this.end = end;
		net = w;
		this.verify();
		//ReikaJavaLibrary.pConsole(start, end instanceof BlockEntityBattery);
	}

	private void verify() {
		if (start == end)
			throw new IllegalArgumentException("Cannot connect an object to itself!");
		if (start == null || end == null)
			throw new IllegalArgumentException("Cannot connect null points!");
		if (start.getWorld() != end.getWorld())
			;//throw new IllegalArgumentException("Cannot connect points across dimensions!");
	}

	public void calculatePaths() {
		//ReikaJavaLibrary.pConsole("START", Dist.DEDICATED_SERVER);
		int x = start.getX();
		int y = start.getY();
		int z = start.getZ();
		Level world = start.getWorld();
		//li.add(Arrays.asList(x, y, z));
		for (int i = 0; i < 6; i++) {
			Direction dir = WireNetwork.dirs[i];
			LinkedList<WorldLocation> li = new LinkedList();
			if (start.canEmitPowerToSide(dir)) {
				int dx = x+dir.getStepX();
				int dy = y+dir.getStepY();
				int dz = z+dir.getStepZ();
				if (world.hasChunksAt(dx, dy, dz, dx, dy, dz)) {
					if (this.isEnd(world, dx, dy, dz)) {
						if (end.canReceivePower() && end.canReceivePowerFromSide(dir.getOpposite())) {
							//li.addLast(Arrays.asList(dx, dy, dz));
							paths.add(new WirePath(li, start, end, net));
							//li.removeLast();
							return;
						}
					}
					else {
						ElectriTiles t = ElectriTiles.getTE(world, new BlockPos(dx, dy, dz));
						if (t != null && t.isWiringPiece()) {
							WiringTile tile = (WiringTile)world.getBlockEntity(new BlockPos(dx, dy, dz));
							if (tile.canNetworkOnSide(dir.getOpposite()))
								this.recursiveCalculate(world, dx, dy, dz, li);
						}
						else {
							BlockEntity te = world.getBlockEntity(new BlockPos(dx, dy, dz));
					/*		if (te instanceof WorldRift) {
								WorldRift sr = (WorldRift)te;
								rifts.add(new WorldLocation(te));
								WorldLocation loc = sr.getLinkTarget();
								if (loc != null) {
									rifts.add(loc);
									BlockEntity other = sr.getBlockEntityFrom(dir);
									if (other instanceof WiringTile) {
										if (((WiringTile)other).canNetworkOnSide(dir.getOpposite())) {
											World w2 = loc.getWorld();
											dx = loc.xCoord+dir.getStepX();
											dy = loc.yCoord+dir.getStepY();
											dz = loc.zCoord+dir.getStepZ();
											if (w2 != null && w2.hasChunksAt(dx, dy, dz, dx, dy, dz)) {
												this.recursiveCalculate(w2, dx, dy, dz, li);
											}
										}
									}
								}
							}*/
						}
					}
				}
				else {
					return;
				}
			}
		}
		//ReikaJavaLibrary.pConsole(paths, Dist.DEDICATED_SERVER);
	}

	//check direction code!!
	private void recursiveCalculate(Level world, int x, int y, int z, LinkedList<WorldLocation> li) {
		if (li.contains(new WorldLocation(world, x, y, z))) {
			return;
		}
		//ReikaJavaLibrary.pConsole(x+", "+y+", "+z, Dist.DEDICATED_SERVER);
		li.addLast(new WorldLocation(world, x, y, z));
		WiringTile te = (WiringTile)world.getBlockEntity(new BlockPos(x, y, z));
		if (te == null)
			throw new WorldSanityException("ElC pathfinding via "+this.debugPath(li)+" arrived at a null tile!");
		//ReikaJavaLibrary.pConsole("<<"+li.size()+">>"+(x+0.5)+","+(y+0.5)+","+(z+0.5));
		for (int i = 0; i < 6; i++) {
			Direction dir = WireNetwork.dirs[i];
			if (te.canNetworkOnSide(dir)) {
				int dx = x+dir.getStepX();
				int dy = y+dir.getStepY();
				int dz = z+dir.getStepZ();
				if (world.hasChunksAt(dx, dy, dz, dx, dy, dz)) {
					if (this.isEnd(world, dx, dy, dz)) {
						if (end.canReceivePower() && end.canReceivePowerFromSide(dir.getOpposite())) {
							//li.addLast(Arrays.asList(dx, dy, dz));
							paths.add(new WirePath(li, start, end, net));
							//ReikaJavaLibrary.pConsole(li.size(), Dist.DEDICATED_SERVER);
							//ReikaJavaLibrary.pConsole("Create("+li.size()+")", Dist.DEDICATED_SERVER);
							//li.removeLast();
							return;
						}
					}
					else {
						ElectriTiles t = ElectriTiles.getTE(world, new BlockPos(dx, dy, dz));
						if (t != null && t.isWiringPiece()) {
							WiringTile tile = (WiringTile)world.getBlockEntity(new BlockPos(dx, dy, dz));
							if (this.tileCanConnect(tile) && tile.canNetworkOnSide(dir.getOpposite()))
								this.recursiveCalculate(world, dx, dy, dz, li);
							//ReikaJavaLibrary.pConsole(dir+"@"+x+","+y+","+z+" :"+li.size(), Dist.DEDICATED_SERVER);
						}
						else {
							BlockEntity te2 = world.getBlockEntity(new BlockPos(dx, dy, dz));
						/*	if (te2 instanceof WorldRift) {
								WorldRift sr = (WorldRift)te2;
								WorldLocation loc = sr.getLinkTarget();
								if (loc != null) {
									BlockEntity other = sr.getBlockEntityFrom(dir);
									if (other instanceof WiringTile) {
										if (((WiringTile)other).canNetworkOnSide(dir.getOpposite())) {
											World w2 = loc.getWorld();
											dx = loc.xCoord+dir.getStepX();
											dy = loc.yCoord+dir.getStepY();
											dz = loc.zCoord+dir.getStepZ();
											if (w2 != null && w2.hasChunksAt(dx, dy, dz, dx, dy, dz)) {
												this.recursiveCalculate(w2, dx, dy, dz, li);
											}
										}
									}
								}
							}*/
						}
					}
				}
			}
		}
		li.removeLast();
		//ReikaJavaLibrary.pConsole(">>"+li.size()+"<<"+(x+0.5)+","+(y+0.5)+","+(z+0.5));
	}

	private String debugPath(LinkedList<WorldLocation> li) {
		li.addFirst(new WorldLocation(start.getWorld(), start.getX(), start.getY(), start.getZ()));
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < li.size(); i++) {
			WorldLocation loc = li.get(i);
			sb.append("[");
			sb.append(loc.getBlockEntity());
			sb.append(" @ ");
			sb.append(loc);
			sb.append("]");
			if (i < li.size()-1)
				sb.append(" > ");
		}
		sb.append("}");
		return sb.toString();
	}

	private boolean tileCanConnect(WiringTile tile) {
		return !(tile instanceof BlockEntityWireComponent) || ((BlockEntityWireComponent) tile).canConnect();
	}

	private boolean isEnd(Level world, int x, int y, int z) {
		return end.getWorld() == world && x == end.getX() && y == end.getY() && z == end.getZ();
	}

	Collection<WirePath> getCalculatedPaths() {
		return Collections.unmodifiableCollection(paths);
	}

	Collection<WorldLocation> getRifts() {
		return Collections.unmodifiableCollection(rifts);
	}

	public WirePath getShortestPath() {
		int index = -1;
		int length = Integer.MAX_VALUE;
		for (int i = 0; i < paths.size(); i++) {
			WirePath path = paths.get(i);
			int l = path.getLength();
			if (l < length) {
				length = l;
				index = i;
			}
		}
		return index >= 0 ? paths.get(index) : null;
	}

}
