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

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.APIPacketHandler;
import reika.dragonapi.DragonAPI;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.io.ReikaSoundHelper;
import reika.electricraft.auxiliary.interfaces.WireFuse;
import reika.electricraft.base.BlockEntityWireComponent;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;
import reika.rotarycraft.auxiliary.interfaces.NBTMachine;

public class BlockEntityFuse extends BlockEntityWireComponent implements WireFuse, NBTMachine {

	private boolean overloaded;

	private int currentLimit;

	public static final int[] TIERS = {
		32,
		128,
		1024,
		8192
	};

	public BlockEntityFuse(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.FUSE.get(), pos, state);
	}
	public BlockEntityFuse(int limit, BlockPos pos, BlockState state) {
		super(null, pos, state);
		currentLimit = limit;
	}

	@Override
	public int getResistance() {
		return !this.isOverloaded() ? 0 : Integer.MAX_VALUE;
	}

	@Override
	public int getMaxCurrent() {
		return currentLimit;
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.FUSE;
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	public boolean isOverloaded() {
		return overloaded;
	}

	@Override
	public float getHeight() {
		return 0.625F;
	}

	@Override
	public float getWidth() {
		return 0.75F;
	}

	@Override
	public boolean canConnect() {
		return !this.isOverloaded();
	}

	@Override
	public void overload(int current) {
		overloaded = true;
		ReikaSoundHelper.playBreakSound(level, worldPosition, Blocks.GLASS);
		ReikaSoundHelper.playSoundAtBlock(level, worldPosition, SoundEvents.TNT_PRIMED, 2, 1);
		ReikaPacketHelper.sendDataPacketWithRadius(DragonAPI.packetChannel, APIPacketHandler.PacketIDs.COLOREDPARTICLE.ordinal(), this, 24, 1, 1, 1, 32, 0);
	}

	@Override
	public void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);

		overloaded = NBT.getBoolean("overload");
		currentLimit = NBT.getInt("limit");
	}

	@Override
	public void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);


		NBT.putBoolean("overload", overloaded);
		NBT.putInt("limit", currentLimit);
	}

	@Override
	public CompoundTag getTagsToWriteToStack() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("currentlim", currentLimit);
		return tag;
	}

	@Override
	public void setDataFromItemStackTag(CompoundTag NBT) {
		if (NBT != null) {
			currentLimit = NBT.getInt("currentlim");
		}
	}

	@Override
	public ArrayList<CompoundTag> getCreativeModeVariants() {
		ArrayList<CompoundTag> li = new ArrayList<>();
		for (int i = 0; i < TIERS.length; i++) {
			CompoundTag tag = new CompoundTag();
			tag.putInt("currentlim", TIERS[i]);
			li.add(tag);
		}
		return li;
	}

	@Override
	public ArrayList<String> getDisplayTags(CompoundTag NBT) {
		ArrayList<String> li = new ArrayList<>();
		if (NBT != null) {
			li.add("Current Limit: "+NBT.getInt("currentlim")+"A");
		}
		return li;
	}

	@Override
	public boolean onShiftRightClick(Level world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
