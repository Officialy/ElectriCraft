/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary;

import net.minecraft.ChatFormatting;
import reika.dragonapi.instantiable.math.MovingAverage;
import reika.dragonapi.libraries.mathsci.ReikaDateHelper;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
public class BatteryTracker {

	private final MovingAverage energyStored = new MovingAverage(10);
	private final MovingAverage energyFlow = new MovingAverage(10);

	private double lastEnergy = Double.NaN;

	public void update(BatteryTile t) {
		long energy = t.getStoredEnergy();
		energyStored.addValue(energy);
		if (lastEnergy != Double.NaN) {
			energyFlow.addValue(energy-lastEnergy);
		}
		lastEnergy = energy;
	}

	public String getAverageStorage(int decimals) {
		String format = "%."+decimals+"f";
		return String.format(format, energyStored.getAverage());
	}

	public String getAverageFlow(int decimals) {
		String format = "%s%."+decimals+"f";
		double avg = energyFlow.getAverage();
		return String.format(format, avg > 0 ? ChatFormatting.GREEN+"+" : avg < 0 ? ChatFormatting.RED.toString() : "", avg);
	}

	public String getTimeUntilFullOrEmpty(BatteryTile t) {
		double avg = energyFlow.getAverage();
		if (avg < 0) {
			double ticks = t.getStoredEnergy()/-avg;
			return ReikaDateHelper.getTickAsHMS(Math.round(ticks))+" until empty";
		}
		else if (avg > 0) {
			double limit = t.getMaxEnergy();
			double space = limit-t.getStoredEnergy();
			double ticks = space/avg;
			return ReikaDateHelper.getTickAsHMS(Math.round(ticks))+" until full";
		}
		else {
			return "No net flow";
		}
	}

}
