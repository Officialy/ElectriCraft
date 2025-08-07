/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import reika.dragonapi.libraries.io.ReikaSoundHelper;
import reika.dragonapi.libraries.java.ReikaArrayHelper;
import reika.dragonapi.libraries.registry.ReikaParticleHelper;
import reika.electricraft.api.WrappableWireSource;
import reika.electricraft.auxiliary.WrappedSource;
import reika.electricraft.auxiliary.interfaces.Overloadable;
import reika.electricraft.auxiliary.interfaces.WireEmitter;
import reika.electricraft.auxiliary.interfaces.WireReceiver;
import reika.electricraft.base.WiringTile;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.registry.WireType;

import java.util.ArrayList;

public class BlockEntityWire extends WiringTile implements Overloadable {

	private boolean[] connections = new boolean[6];

	public boolean insulated;

	private boolean shouldMelt;

	public BlockEntityWire(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.WIRE.get(), pos, state);
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
		super.updateEntity(world, pos);

		if (shouldMelt)
			this.melt(world, pos);
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.WIRE;
	}

	@Override
	protected void onJoinNetwork() {
		this.checkForWrappables();
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public void onNetworkChanged() {
		super.onNetworkChanged();
		this.checkForWrappables();
	}

	public boolean isConnectedOnSideAt(Level world, int x, int y, int z, Direction dir) {
		dir = dir.getStepX() == 0 ? dir.getOpposite() : dir;
		int dx = x+dir.getStepX();
		int dy = y+dir.getStepY();
		int dz = z+dir.getStepZ();
		Block b = world.getBlockState(new BlockPos(dx, dy, dz)).getBlock();
		BlockEntity te = world.getBlockEntity(new BlockPos(dx, dy, dz));
		if (b == this.getBlockEntityBlockID())
			return true;//connections[dir.ordinal()] && ((BlockEntityWire)te).connections[dir.getOpposite().ordinal()];
		boolean flag = false;
		if (te instanceof WiringTile) {
			flag = flag || ((WiringTile) te).canNetworkOnSide(dir.getOpposite());
		}
		if (te instanceof WireEmitter) {
			flag = flag || ((WireEmitter)te).canEmitPowerToSide(dir.getOpposite());
		}
		if (te instanceof WireReceiver) {
			flag = flag || ((WireReceiver)te).canReceivePowerFromSide(dir.getOpposite());
		}
//		if (te instanceof WorldRift) {
//			flag = true;
//		}
		if (te instanceof WrappableWireSource) {
			flag = flag || ((WrappableWireSource) te).canConnectToSide(dir.getOpposite());
		}
		return flag;
	}

	@Override
	public final boolean canNetworkOnSide(Direction dir) {
		return true;//connections[dir.ordinal()];
	}

	public WireType getWireType() {
		return WireType.wireList[1]; //todo old meta wire type
	}

	@Override
	protected void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);

		connections = ReikaArrayHelper.booleanFromByteBitflags(NBT.getByte("conn"), 6);

		insulated = NBT.getBoolean("insul");

		shouldMelt = NBT.getBoolean("melt");
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);

		NBT.putByte("conn", ReikaArrayHelper.booleanToByteBitflags(connections));

		NBT.putBoolean("insul", insulated);

		NBT.putBoolean("melt", shouldMelt);
	}

	@Override
	public final AABB getRenderBoundingBox() {
		return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX()+1, worldPosition.getY()+1, worldPosition.getZ()+1);
	}

	public boolean isConnectionValidForSide(Direction dir) {
		if (dir.getStepX() == 0)// && MinecraftForgeClient.getRenderPass() != 1)
			dir = dir.getOpposite();
		return connections[dir.ordinal()];
	}

	public void recomputeConnections(Level world, BlockPos pos) {
		boolean clear = false;
		for (int i = 0; i < 6; i++) {
			boolean flag = this.isConnected(dirs[i]);
			if (flag != connections[i]) {
				clear = true;
			}
			connections[i] = flag;
			world.sendBlockUpdated(new BlockPos(pos.getX()+dirs[i].getStepX(), pos.getY()+dirs[i].getStepY(), pos.getZ()+dirs[i].getStepZ()), getBlockState(), getBlockState(), 3);//todo, 3 is all, i forgot what would be needed here
		}
		this.checkForWrappables();
		//world.markBlockForUpdate(x, y, z);
		world.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3); //todo, 3 is all, i forgot what would be needed here

		if (clear) {
			if (network != null)
				network.removeElement(this);
			//this.findAndJoinNetwork(world, x, y, z);
		}
	}

	public void checkRiftConnections() {
		if (network != null)
			network.checkRiftConnections();
	}

	private void checkForWrappables() {
		if (network == null)
			return;
		for (int i = 0; i < 6; i++) {
			BlockEntity te = this.getAdjacentBlockEntity(dirs[i]);
			if (te instanceof WrappableWireSource) {
				network.addElement(new WrappedSource((WrappableWireSource)te));
			}
		}
	}

	public void addToAdjacentConnections(Level world, BlockPos pos) {
		for (int i = 0; i < 6; i++) {
			Direction dir = dirs[i];
			int dx = pos.getX()+dir.getStepX();
			int dy = pos.getX()+dir.getStepY();
			int dz = pos.getX()+dir.getStepZ();
			ElectriTiles m = ElectriTiles.getTE(world, new BlockPos(dx, dy, dz));
			if (m == this.getMachine()) {
				BlockEntityWire te = (BlockEntityWire)world.getBlockEntity(new BlockPos(dx, dy, dz));
				te.connections[dir.getOpposite().ordinal()] = true;//te.isConnected(dir.getOpposite());
//				world.func_147479_m(dx, dy, dz);
			}
		}
	}

	private boolean isConnected(Direction dir) {
		int x = getX()+dir.getStepX();
		int y = getY()+dir.getStepY();
		int z = getZ()+dir.getStepZ();
		ElectriTiles m = this.getMachine();
		ElectriTiles m2 = ElectriTiles.getTE(level, new BlockPos(x, y, z));
		return m == m2;//connectionCount < 3;
		//certain TEs
	}

	@Override
	public int getMaxCurrent() {
		return this.getWireType().maxCurrent;
	}

	public void melt(Level world, BlockPos pos) {
		ReikaSoundHelper.playSoundAtBlock(world, pos, SoundEvents.TNT_PRIMED);
		ReikaParticleHelper.LAVA.spawnAroundBlock(world, pos, 12);
		world.setBlock(pos, Fluids.FLOWING_LAVA.defaultFluidState().createLegacyBlock(), 3);
	}

	@Override
	public void overload(int current) {
		shouldMelt = true;
	}

	@Override
	public int getResistance() {
		return this.getWireType().resistance;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
