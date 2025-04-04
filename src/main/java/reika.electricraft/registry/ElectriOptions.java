/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.registry;

import reika.dragonapi.interfaces.configuration.BooleanConfig;
import reika.dragonapi.interfaces.configuration.IntegerConfig;
import reika.dragonapi.interfaces.configuration.UserSpecificConfig;
import reika.electricraft.ElectriCraft;

public enum ElectriOptions implements IntegerConfig, BooleanConfig, UserSpecificConfig {

	RETROGEN("Retrogenerate Ores", false),
	DISCRETE("Ore Discretization", 1);

	private final String label;
	private boolean defaultState;
	private int defaultValue;
	private final Class type;
	private boolean enforcing = false;

	public static final ElectriOptions[] optionList = ElectriOptions.values();

	ElectriOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	ElectriOptions(String l, boolean d, boolean tag) {
		label = l;
		defaultState = d;
		type = boolean.class;
		enforcing = true;
	}

	ElectriOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}


	public boolean getState() {
		return (Boolean)ElectriCraft.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)ElectriCraft.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return enforcing;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

	@Override
	public boolean isUserSpecific() {
		return false;
	}

}
