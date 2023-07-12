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

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.base.BlockEntityBase;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.blockentities.BlockEntityBattery;
import reika.electricraft.blockentities.BlockEntityWire;
import reika.rotarycraft.api.interfaces.Transducerable;

public abstract class ElectriBlockEntity extends BlockEntityBase implements Transducerable {

	protected Direction[] dirs = Direction.values();

	public float phi;

	public boolean isFlipped = false;

	public ElectriBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

//	public final TextureFetcher getRenderer() {
//		if (ElectriTiles.TEList[this.getIndex()].hasRender())
//			return ElectriRenderList.getRenderForMachine(ElectriTiles.TEList[this.getIndex()]);
//		else
//			return null;
//	}

	@Override
	public final boolean allowTickAcceleration() {
		return false;
	}

	@Override
	public Block getBlockEntityBlockID() {
		return ElectriTiles.teList[this.getIndex()].getBlock();

	}

	public abstract ElectriTiles getMachine();

	@Override
	protected String getTEName() {
		return ElectriTiles.teList[this.getIndex()].getName();
	}

	public final int getIndex() {
		return this.getMachine().ordinal();
	}

	public int getTextureState(Direction side) {
		return 0;
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT)
	{
		super.writeSyncTag(NBT);
		NBT.putBoolean("flip", isFlipped);
	}

	@Override
	protected void readSyncTag(CompoundTag NBT)
	{
		super.readSyncTag(NBT);
		isFlipped = NBT.getBoolean("flip");
	}

	public boolean isThisTE(Block id, int meta) {
		return id == this.getBlockEntityBlockID() && meta == this.getIndex();
	}

//	@Override
//	public boolean shouldRenderInPass(int pass) {
//		ElectriTiles r = ElectriTiles.TEList[this.getIndex()];
//		return pass == 0 || ((r.renderInPass1() || this instanceof ShaftMachine) && pass == 1);
//	}

	public final ArrayList<String> getMessages(Level world, int x, int y, int z, int side) {
		ArrayList<String> li = new ArrayList<>();
		if (this instanceof BlockEntityWire wire) {
			li.add(String.format("Point Voltage: %dV", wire.getWireVoltage()));
			li.add(String.format("Point Current: %dA", wire.getWireCurrent()));
		}
		if (this instanceof BlockEntityBattery b) {
			double max = b.getMaxEnergy();
			li.add(String.format("Stored Energy: %s/%s", b.getDisplayEnergy(), b.getBatteryType().getFormattedCapacity()));
		}
		return li;
	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

/*	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final Visibility getOCNetworkVisibility() {
		return this.getMachine().isWiring() ? Visibility.None : Visibility.Network;
	}*/
}
