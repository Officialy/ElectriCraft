/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft;

import java.util.ArrayList;

import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.instantiable.io.ControlledConfig;
import reika.dragonapi.interfaces.configuration.ConfigList;
import reika.dragonapi.interfaces.registry.IDRegistry;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.electricraft.registry.ElectriOres;

public class ElectriConfig extends ControlledConfig {

	private static final ArrayList<String> entries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ElectriOres.class);
	private final DataElement<Boolean>[] ores = new DataElement[entries.size()];

	public ElectriConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id) {
		super(mod, option, id);

		for (int i = 0; i < entries.size(); i++) {
			String name = entries.get(i);
			ores[i] = this.registerAdditionalOption("Ore Control", name, true);
		}
	}

	public boolean isOreGenEnabled(ElectriOres ore) {
		return ores[ore.ordinal()].getData();
	}

}
