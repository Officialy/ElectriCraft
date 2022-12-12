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
import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.APIPacketHandler;
import reika.dragonapi.DragonAPI;
import reika.dragonapi.instantiable.StepTimer;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.io.ReikaSoundHelper;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.mathsci.ReikaPhysicsHelper;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.dragonapi.libraries.registry.ReikaParticleHelper;
import reika.dragonapi.libraries.rendering.ReikaColorAPI;
import reika.electricraft.auxiliary.interfaces.ConversionTile;
import reika.electricraft.base.ElectricalReceiver;
import reika.electricraft.network.WireNetwork;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;
import reika.rotarycraft.api.interfaces.Screwdriverable;
import reika.rotarycraft.api.power.PowerTracker;
import reika.rotarycraft.api.power.ShaftMerger;
import reika.rotarycraft.api.power.ShaftPowerEmitter;
import reika.rotarycraft.api.power.ShaftPowerReceiver;
import reika.rotarycraft.auxiliary.PowerSourceList;
import reika.rotarycraft.auxiliary.interfaces.NBTMachine;
import reika.rotarycraft.auxiliary.interfaces.PowerSourceTracker;
import reika.rotarycraft.registry.EngineType;
import reika.rotarycraft.registry.RotaryItems;
import reika.rotarycraft.registry.SoundRegistry;

public class BlockEntityMotor extends ElectricalReceiver implements Screwdriverable, ShaftPowerEmitter, ConversionTile, NBTMachine, PowerSourceTracker, ShaftMerger {

	private static final int soundtime = (int)(EngineType.DC.getSoundLength()*2.04F);
	private StepTimer soundTimer = new StepTimer(soundtime);

	protected int omega;
	protected int torque;
	protected long power;

	protected int iotick;

	private Direction facing;

	private int maxAmp = 1;

	private float sourceSize;

	public BlockEntityMotor(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.MOTOR.get(), pos, state);
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
		super.updateEntity(world, pos);

		if (iotick > 0)
			iotick -= 8;

		if (!world.isClientSide() && network != null) {
			torque = this.getEffectiveCurrent(world, pos)*WireNetwork.TORQUE_PER_AMP;
			omega = network.getTerminalVoltage(this)/WireNetwork.TORQUE_PER_AMP;
		}
		power = (long)omega*(long)torque;
		if (power > 0) {
			soundTimer.update();
			if (soundTimer.checkCap())
				SoundRegistry.ELECTRIC.playSoundAtBlock(world, pos, this.getSoundVolume(world, pos), 0.333F);
		}
		else {
			torque = omega = 0;
		}
		BlockEntity tg = this.getAdjacentBlockEntity(this.getFacing().getOpposite());
		if (tg instanceof ShaftPowerReceiver) {
			ShaftPowerReceiver rec = (ShaftPowerReceiver)tg;
			rec.setOmega(omega);
			rec.setTorque(torque);
			rec.setPower(power);
		}
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {
		if (!this.isInWorld()) {
			phi = 0;
			return;
		}
		phi += ReikaMathLibrary.doubpow(ReikaMathLibrary.logbase(omega+1, 2), 1.05);
	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public void onNetworkChanged() {
		PowerSourceList pwr = this.getPowerSources(this, null);
		sourceSize = Math.max(network.getNumberSourcesPer(this), pwr.isBedrock() ? 1 : pwr.size()/(float)network.getNumberSinks());
	}

	private int getEffectiveCurrent(Level world, BlockPos pos) {
		int in = network.getTerminalCurrent(this);
		if (network.getNumberSources() <= 1)
			return in;
		float f = Math.min(1, maxAmp/sourceSize);
		int curlim = Math.min(in, network.getTotalCurrent()/network.getNumberSources()*maxAmp);
		if ((sourceSize > maxAmp || in > curlim) && in > 0) { //was (in > max)
			if (rand.nextInt(10) == 0) {
				ReikaSoundHelper.playSoundAtBlock(world, pos, SoundEvents.TNT_PRIMED);
				//ReikaParticleHelper.SMOKE.spawnAroundBlock(world, x, y, z, 2);
				ReikaPacketHelper.sendDataPacketWithRadius(DragonAPI.packetChannel, APIPacketHandler.PacketIDs.PARTICLE.ordinal(), this, 32, ReikaParticleHelper.SMOKE.ordinal(), 1);
			}
		}
		return (int)(curlim*f);
	}

	public boolean upgrade(ItemStack is) {
		boolean flag = false;
		if (ReikaItemHelper.matchStacks(is, RotaryItems.INDUCTIVE_INGOT.get()) && maxAmp < 2) {
			maxAmp = 2;
			flag = true;
		}
		else if (ReikaItemHelper.matchStacks(is, RotaryItems.TUNGSTEN_ALLOY_INGOT.get()) && maxAmp < 4) {
			maxAmp = 4;
			flag = true;
		}
		else if (ReikaItemHelper.matchStacks(is, RotaryItems.BEDROCK_ALLOY_INGOT.get()) && maxAmp < 1000) {
			maxAmp = 1000;
			flag = true;
		}

		if (flag) {
			if (network != null) {
				network.updateWires();
			}
		}
		return flag;
	}

	private float getSoundVolume(Level world, BlockPos pos) {
		if (world.getBlockState(pos.below()).getBlock() == Blocks.WHITE_WOOL && world.getBlockState(pos.above()).getBlock() == Blocks.WHITE_WOOL)
			return 0.1F;
		Direction dir = this.getFacing();
		Direction dir2 = dir.getOpposite();
		for (int i = 0; i < 6; i++) {
			Direction side = dirs[i];
			if (side != dir && side != dir2 && side != Direction.DOWN) {
				int dx = pos.getX()+side.getStepX();
				int dy = pos.getY()+side.getStepY();
				int dz = pos.getZ()+side.getStepZ();
				Block id = world.getBlockState(new BlockPos(dx, dy, dz)).getBlock();
				if (id != Blocks.WHITE_WOOL)
					return 0.36F;
			}
		}
		return 0.1F;
	}

	public final Direction getFacing() {
		return facing != null ? facing : Direction.EAST;
	}

	public void setFacing(Direction dir) {
		facing = dir;
	}

	@Override
	public final int getOmega() {
		return omega;
	}

	@Override
	public final int getTorque() {
		return torque;
	}

	@Override
	public final long getPower() {
		return power;
	}

	@Override
	public final int getIORenderAlpha() {
		return iotick;
	}

	@Override
	public final void setIORenderAlpha(int io) {
		iotick = io;
	}

	@Override
	public void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);

		facing = dirs[NBT.getInt("face")];

		omega = NBT.getInt("omg");
		torque = NBT.getInt("tq");
		power = NBT.getLong("pwr");

		maxAmp = NBT.getInt("amp");

		iotick = NBT.getInt("io");
	}

	@Override
	public void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);

		NBT.putInt("face", this.getFacing().ordinal());

		NBT.putInt("omg", omega);
		NBT.putInt("tq", torque);
		NBT.putLong("pwr", power);

		NBT.putInt("amp", maxAmp);

		NBT.putInt("io", iotick);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.MOTOR;
	}

	@Override
	public boolean canNetworkOnSide(Direction dir) {
		return dir == this.getFacing();
	}

	@Override
	public boolean canWriteTo(Direction from) {
		Direction dir = this.getFacing().getOpposite();
		return dir == from;
	}

	@Override
	public boolean isEmitting() {
		return true;
	}

	@Override
	public boolean canReceivePowerFromSide(Direction dir) {
		return this.canNetworkOnSide(dir);
	}

	@Override
	public boolean canReceivePower() {
		return true;
	}

	@Override
	public BlockPos getEmittingPos(BlockPos pos) {
		return new BlockPos(pos.getX()+ this.getFacing().getOpposite().getStepX(), pos.getY()+this.getFacing().getOpposite().getStepY(), pos.getY()+this.getFacing().getOpposite().getStepZ());
	}

	@Override
	public long getMaxPower() {
		return power;
	}

	@Override
	public long getCurrentPower() {
		return power;
	}

	protected void incrementFacing() {
		int o = this.getFacing().ordinal();
		if (o == 5)
			this.setFacing(dirs[2]);
		else
			this.setFacing(dirs[o+1]);
		this.rebuildNetwork();
	}

	@Override
	public CompoundTag getTagsToWriteToStack() {
		if (maxAmp > 1) {
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("amp", maxAmp);
			return nbt;
		}
		return null;
	}

	@Override
	public void setDataFromItemStackTag(CompoundTag NBT) {
		if (NBT != null && NBT.contains("amp"))
			maxAmp = NBT.getInt("amp");
	}

	@Override
	public ArrayList<CompoundTag> getCreativeModeVariants() {
		ArrayList<CompoundTag> li = new ArrayList<>();
		CompoundTag NBT = new CompoundTag();
		NBT.putInt("amp", 4);
		li.add(NBT);
		return li;
	}

	@Override
	public ArrayList<String> getDisplayTags(CompoundTag NBT) {
		ArrayList<String> li = new ArrayList<>();
		if (NBT != null && NBT.contains("amp")) {
			int amp = NBT.getInt("amp");
			li.add(String.format("Contains a %dx amplifier", amp));
		}
		return li;
	}

	@Override
	public PowerSourceList getPowerSources(PowerSourceTracker io, ShaftMerger caller) {
		//if (!level.isClientSide())
		//	ReikaJavaLibrary.pConsole(this+": "+network.getInputSources(io, caller).size()+":"+network.getInputSources(io, caller));
		return network != null ? network.getInputSources(io, caller, this) : new PowerSourceList();
	}

	@Override
	public void onPowerLooped(PowerTracker pwr) {
		if (power > 0)
			this.fail();
	}

	@Override
	public void fail() {
		this.delete();
	}

	public int getFinColor() {
		if (!this.isInWorld())
			return 0x515168;
		int c = ReikaPhysicsHelper.getColorForTemperature(400*ReikaMathLibrary.logbase2(power)-6500);
		//ReikaJavaLibrary.pConsole(35*ReikaMathLibrary.logbase2(power));
		return c == 0 ? 0x515168 : ReikaColorAPI.mixColors(c, 0x515168, 0.5F);
	}

	@Override
	public final void getAllOutputs(Collection<BlockEntity> c, Direction dir) {
		c.add(this.getAdjacentBlockEntity(this.getFacing().getOpposite()));
	}

	@Override
	public BlockPos getIoOffsetPos() {
		return null;
	}

	@Override
	public boolean onShiftRightClick(Level world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public boolean onRightClick(Level world, BlockPos pos, Direction side) {
		this.incrementFacing();
		return true;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
