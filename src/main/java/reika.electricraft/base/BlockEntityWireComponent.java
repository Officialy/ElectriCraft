/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import reika.rotarycraft.api.interfaces.Screwdriverable;

public abstract class BlockEntityWireComponent extends WiringTile implements Screwdriverable {

	private Direction facing;

	public BlockEntityWireComponent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public final boolean canNetworkOnSide(Direction dir) {
		return dir == this.getFacing() || dir == this.getFacing().getOpposite();
	}

	public final Direction getFacing() {
		return facing != null ? facing : Direction.EAST;
	}

	public final void setFacing(Direction dir) {
		facing = dir;
	}

	public abstract boolean canConnect();

	public final AABB getAABB() {
		float miny = 0;
		float maxy = this.getHeight();
		float w = this.getWidth()/2F;
		Direction dir = this.getFacing();
		float maxx = dir.getStepX() != 0 ? 1 : 0.5F+w;
		float minx = dir.getStepX() != 0 ? 0 : 0.5F-w;
		float maxz = dir.getStepZ() != 0 ? 1 : 0.5F+w;
		float minz = dir.getStepZ() != 0 ? 0 : 0.5F-w;
		if (isFlipped) {
			miny = 1-this.getHeight();
			maxy = 1;
		}
		return new AABB(worldPosition.getX()+minx, worldPosition.getY()+miny, worldPosition.getZ()+minz, worldPosition.getX()+maxx, worldPosition.getY()+maxy, worldPosition.getZ()+maxz);
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT)
	{
		super.writeSyncTag(NBT);

		NBT.putInt("face", this.getFacing().ordinal());
	}

	@Override
	protected void readSyncTag(CompoundTag NBT)
	{
		super.readSyncTag(NBT);

		this.setFacing(dirs[NBT.getInt("face")]);
	}

	@Override
	public boolean onRightClick(Level world, BlockPos pos, Direction side) {
		this.incrementFacing();
		return true;
	}

	protected void incrementFacing() {
		int o = this.getFacing().ordinal();
		if (o == 5)
			this.setFacing(dirs[2]);
		else
			this.setFacing(dirs[o+1]);
		this.rebuildNetwork();
	}

	public abstract float getHeight();
	public abstract float getWidth();

}
