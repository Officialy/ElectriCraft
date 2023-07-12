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

	void findAndJoinNetwork(Level world, int x, int y, int z);

	WireNetwork getNetwork();

	void setNetwork(WireNetwork n);

	void removeFromNetwork();

	void resetNetwork();

	boolean isConnectable();

	void onNetworkChanged();

	boolean canNetworkOnSide(Direction dir);

	Level getWorld();
	int getX();
	int getY();
	int getZ();
}
