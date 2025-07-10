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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fluids.capability.IFluidHandler;

import reika.dragonapi.instantiable.HybridTank;
import reika.dragonapi.instantiable.StepTimer;
import reika.dragonapi.libraries.ReikaAABBHelper;
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.mathsci.ReikaThermoHelper;
import reika.dragonapi.libraries.level.ReikaWorldHelper;
import reika.electricraft.auxiliary.interfaces.Overloadable;
import reika.electricraft.auxiliary.interfaces.WireEmitter;
import reika.electricraft.auxiliary.interfaces.WireReceiver;
import reika.electricraft.base.NetworkBlockEntity;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;
import reika.rotarycraft.api.interfaces.Screwdriverable;
import reika.rotarycraft.auxiliary.interfaces.PipeConnector;
import reika.rotarycraft.auxiliary.interfaces.TemperatureTE;
import reika.rotarycraft.base.blockentity.BlockEntityPiping;
import reika.rotarycraft.registry.MachineRegistry;
import reika.rotarycraft.registry.RotaryFluids;

import java.util.ArrayList;

public class BlockEntityTransformer extends NetworkBlockEntity implements WireEmitter, WireReceiver, Screwdriverable, TemperatureTE, IFluidHandler, PipeConnector, Overloadable {

	private int Vin = 0;
	private int Ain = 0;

	private int Vout = 0;
	private int Aout = 0;

	private int Vold = 0;
	private int Aold = 0;

	private int n1 = 1;
	private int n2 = 1;

	//private double ratio = 1;

	private Direction facing;

	public static final int MAXTEMP = 1000;
	public static final int MAXCURRENT = 4096;

	private int temperature;
	private final StepTimer tempTimer = new StepTimer(20);

	private final HybridTank tank = new HybridTank("transformer", 200);

	public BlockEntityTransformer(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.TRANSFORMER.get(), pos, state);
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
		super.updateEntity(world, pos);

		if (!world.isClientSide() && network != null) {
			Vin = network.getTerminalVoltage(this);
			Ain = network.getTerminalCurrent(this);
			boolean chg = Vin != Vold || Ain != Aold;
			if (this.getTicksExisted() == 0 || chg) {
				this.calculateOutput();
				network.updateWires();
			}
			Vold = Vin;
			Aold = Ain;
			if (Ain > this.getMaxCurrent() || Aout > this.getMaxCurrent())
				this.overload(Math.max(Ain, Aout));
		}

		tempTimer.update();
		if (tempTimer.checkCap()) {
			this.updateTemperature(world, pos);
		}
	}

	@Override
	public int getMaxCurrent() {
		return MAXCURRENT;
	}

	@Override
	public void overload(int current) {
		level.explode(null, worldPosition.getX()+0.5, worldPosition.getY()+0.5, worldPosition.getZ()+0.5, 12, Level.ExplosionInteraction.BLOCK);
	}

	public double getRatio() {
		return (double)n2/n1;//ratio;
	}

	public boolean isStepUp() {
		return n2 > n1;//ratio > 1;
	}

	public int getN1() {
		return n1;
	}

	public int getN2() {
		return n2;
	}

	public void setRatio(int n1, int n2) {
		if (n1 == 0 || n2 == 0)
			return;
		//this.setRatio((double)n2/n1);

		boolean f1 = n1 != this.n1;
		boolean f2 = n2 != this.n2;
		this.n1 = n1;
		this.n2 = n2;
		this.onRatioChanged(f1 || f2);
	}
	/*
	private void setRatio(double r) {
		boolean chg = ratio != r;
		ratio = r;
		this.onRatioChanged(chg);
	}*/

	private void onRatioChanged(boolean full) {
		this.calculateOutput();
		if (full && network != null)
			network.updateWires();
	}

	public String getRatioForDisplay() {/*
		int r1 = 1;
		int r2 = 1;
		double ratio = this.getRatio();
		if (ratio != 0 && ratio != 1) {
			r1 = Math.max(1, (int)(1D/ratio));
			r2 = Math.max(1, (int)(ratio));
		}*/
		return String.format("%d:%d", n1, n2);
	}

	private void calculateOutput() {
		double ratio = this.getRatio();
		if (ratio == 0) {
			Vout = Aout = 0;
		}
		else {
			Vout = (int)(Vin*ratio);
			Aout = (int)(Ain/ratio*this.getEfficiency());
		}
	}

	public double getEfficiency() {
		double r = this.getRatio();
		if (r < 1)
			r = 1D/r;
		return 1/Math.pow(r, 0.1);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.TRANSFORMER;
	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	public void findAndJoinNetwork(Level world, int x, int y, int z) {

	}

	@Override
	public boolean canNetworkOnSide(Direction dir) {
		return dir == this.getFacing() || dir == this.getFacing().getOpposite();
	}

	@Override
	protected void writeSyncTag(CompoundTag NBT)
	{
		super.writeSyncTag(NBT);
		NBT.putInt("volt", Vin);
		NBT.putInt("amp", Ain);
		NBT.putInt("volt2", Vout);
		NBT.putInt("amp2", Aout);

		//NBT.setDouble("ratio", ratio);

		NBT.putInt("n1", n1);
		NBT.putInt("n2", n2);

		NBT.putInt("face", this.getFacing().ordinal());

		NBT.putInt("temp", temperature);
	}

	@Override
	protected void readSyncTag(CompoundTag NBT)
	{
		super.readSyncTag(NBT);
		Vin = NBT.getInt("volt");
		Ain = NBT.getInt("amp");
		Vout = NBT.getInt("volt2");
		Aout = NBT.getInt("amp2");

		n1 = NBT.getInt("n1");
		n2 = NBT.getInt("n2");

		//ratio = NBT.getDouble("ratio");

		this.setFacing(dirs[NBT.getInt("face")]);

		temperature = NBT.getInt("temp");
	}

	@Override
	public boolean canReceivePowerFromSide(Direction dir) {
		return dir == this.getFacing().getOpposite();
	}

	@Override
	public boolean canReceivePower() {
		return true;
	}

	@Override
	public int getGenVoltage() {
		return Vout;
	}

	@Override
	public int getGenCurrent() {
		return Aout;
	}

	@Override
	public boolean canEmitPowerToSide(Direction dir) {
		return dir == this.getFacing();
	}

	@Override
	public boolean canEmitPower() {
		return true;
	}

	public Direction getFacing() {
		return facing != null ? facing : Direction.EAST;
	}

	public final void setFacing(Direction dir) {
		facing = dir;
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

	protected void incrementFacing() {
		int o = this.getFacing().ordinal();
		if (o == 5)
			this.setFacing(dirs[2]);
		else
			this.setFacing(dirs[o+1]);
		this.rebuildNetwork();
	}

	public AABB getAABB() {
		int dx = Math.abs(this.getFacing().getStepX());
		int dz = Math.abs(this.getFacing().getStepZ());
		double d = 0.2875;
		double d2 = 0.0625;
		AABB box = ReikaAABBHelper.getBlockAABB(worldPosition);
		double minX = box.minX;
		double minZ = box.minZ;
		double maxX = box.maxX;
		double maxZ = box.maxZ;

		box.setMinX(minX += dz*d);
		box.setMaxX(maxX -= dz*d);
		box.setMinZ(minZ += dx*d);
		box.setMaxZ(maxZ -= dx*d);
		box.setMinX(minX += dx*d2);
		box.setMaxX(maxX -= dx*d2);
		box.setMinZ(minZ += dz*d2);
		box.setMaxZ(maxZ -= dz*d2);
		return box;
	}

	@Override
	public void updateTemperature(Level world, BlockPos pos) {
		if (Aout > 0 && Vout > 0) {
			double ratio = this.getRatio();
			double r = ratio >= 0 ? ratio : 1/ratio;
			double d = tank.getFluidLevel() >= 50 ? 0.035 : 0.35;
			temperature += d*Math.sqrt(Ain*Vin)+Math.max(0, ReikaMathLibrary.logbase(r+1, 2));
		}
		if (tank.getFluidLevel() >= 50) {
			tank.removeLiquid(50);
			//temperature *= 0.75;
		}
		int Tamb = ReikaWorldHelper.getAmbientTemperatureAt(world, pos);
		if (temperature > Tamb) {
			temperature -= (temperature-Tamb)/5;
		}
		else {
			temperature += (temperature-Tamb)/5;
		}
		if (temperature - Tamb <= 4 && temperature > Tamb)
			temperature--;
		if (temperature > MAXTEMP) {
			temperature = MAXTEMP;
			this.overheat(world, pos);
		}
		if (temperature < Tamb)
			temperature = Tamb;
	}

	@Override
	public void addTemperature(int temp) {
		temperature += temp;
	}

	@Override
	public int getTemperature() {
		return temperature;
	}

	@Override
	public int getThermalDamage() {
		return temperature > 600 ? 8 : temperature > 200 ? 4 : 0;
	}

	@Override
	public void overheat(Level world, BlockPos pos) {
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		level.explode(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 3, true, Level.ExplosionInteraction.BLOCK);
	}

	@Override
	public boolean canConnectToPipe(MachineRegistry m) {
		return m.isStandardPipe();
	}

	@Override
	public boolean canConnectToPipeOnSide(MachineRegistry m, Direction side) {
		return this.canConnectToPipe(m) && this.canIntake(side);
	}

	private boolean canIntake(Direction side) {
		return side != this.getFacing() && side != this.getFacing().getOpposite() && side.getStepY() == 0;
	}

	@Override
	public BlockEntityPiping.Flow getFlowForSide(Direction side) {
		return this.canIntake(side) ? BlockEntityPiping.Flow.INPUT : BlockEntityPiping.Flow.NONE;
	}

	@Override
	public int fillPipe(Direction from, FluidStack resource, FluidAction action) {
		return this.canFill(from, resource.getFluid()) ? tank.fill(resource, action) : 0;
	}

	@Override
	public FluidStack drainPipe(Direction from, int maxDrain, FluidAction doDrain) {
		return null;
	}

	public boolean canFill(Direction from, Fluid fluid) {
		return this.canIntake(from) && fluid.equals(RotaryFluids.LIQUID_NITROGEN.get());
	}

	@Override
	public boolean canBeCooledWithFins() {
		return true;
	}

	public void setTemperature(int temp) {
		temperature = temp;
	}

	@Override
	public boolean allowExternalHeating() {
		return false;
	}

	@Override
	public int getMaxTemperature() {
		return MAXTEMP;
	}

	@Override
	public boolean allowHeatExtraction() {
		return true;
	}

	@Override
	public double heatEnergyPerDegree() {
		return ReikaThermoHelper.STEEL_HEAT*/*todo ?? ReikaBlockHelper.getBlockVolume(level, worldPosition)**/ReikaEngLibrary.rhoiron;
	}

	@Override
	public int getAmbientTemperature() {
		return 0;
	}

	@Override
	public int getTanks() {
		return 0;
	}

	@Override
	public  FluidStack getFluidInTank(int tank) {
		return null;
	}

	@Override
	public int getTankCapacity(int tank) {
		return 0;
	}

	@Override
	public boolean isFluidValid(int tank,  FluidStack stack) {
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public  FluidStack drain(FluidStack resource, FluidAction action) {
		return null;
	}

	@Override
	public  FluidStack drain(int maxDrain, FluidAction action) {
		return null;
	}

	@Override
	public boolean hasAnInventory() {
		return false;
	}

	@Override
	public boolean hasATank() {
		return false;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}
}
