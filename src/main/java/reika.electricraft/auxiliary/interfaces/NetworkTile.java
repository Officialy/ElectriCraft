/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary.interfaces;




import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import reika.electricraft.network.WireNetwork;

public interface NetworkTile {

	public void findAndJoinNetwork(Level world, int x, int y, int z);

	public WireNetwork getNetwork();

	public void setNetwork(WireNetwork n);

	public void removeFromNetwork();

	public void resetNetwork();

	public boolean isConnectable();

	public void onNetworkChanged();

	public abstract boolean canNetworkOnSide(Direction dir);

	public Level getWorld();
	public int getX();
	public int getY();
	public int getZ();
}
