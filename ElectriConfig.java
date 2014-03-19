package Reika.ElectriCraft;

import java.util.ArrayList;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.DragonAPI.Interfaces.IDRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.ElectriCraft.Registry.ElectriOres;

public class ElectriConfig extends ControlledConfig {

	private static final ArrayList<String> entries = ReikaJavaLibrary.getEnumEntriesWithoutInitializing(ElectriOres.class);
	private boolean[] ores = new boolean[entries.size()];

	public ElectriConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] blocks, IDRegistry[] items, IDRegistry[] id, int cfg) {
		super(mod, option, blocks, items, id, cfg);
	}

	@Override
	protected void loadAdditionalData() {
		for (int i = 0; i < entries.size(); i++) {
			String name = entries.get(i);
			ores[i] = config.get("Ore Control", name, true).getBoolean(true);
		}
	}

	public boolean isOreGenEnabled(ElectriOres ore) {
		return ores[ore.ordinal()];
	}

}