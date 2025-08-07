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

import java.util.HashMap;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import reika.dragonapi.exception.RegistrationException;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.registry.ElectriTiles;

public class ElectriRenderList {

	private static final HashMap<ElectriTiles, ElectriTERenderer> renders = new HashMap<ElectriTiles, ElectriTERenderer>();
	private static final HashMap<ElectriTiles, ElectriTiles> overrides = new HashMap<ElectriTiles, ElectriTiles>();

	public static boolean addRender(ElectriTiles m, ElectriTERenderer r) {
		if (!renders.containsValue(r)) {
			renders.put(m, r);
			return true;
		}
		else {
			ElectriTiles parent = ReikaJavaLibrary.getHashMapKeyByValue(renders, r);
			overrides.put(m, parent);
			return false;
		}
	}

	public static ElectriTERenderer getRenderForMachine(ElectriTiles m) {
		if (overrides.containsKey(m))
			return renders.get(overrides.get(m));
		return renders.get(m);
	}

//	public static String getRenderTexture(ElectriTiles m, RenderFetcher te) {
//		return getRenderForMachine(m).getImageFileName(te);
//	}

	public static ElectriTERenderer instantiateRenderer(ElectriTiles m) {
		try {
			ElectriTERenderer r = (ElectriTERenderer)Class.forName(m.getRenderer()).newInstance();
			if (addRender(m, r))
				return r;
			else
				return renders.get(overrides.get(m));
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException("Tried to call nonexistent render "+m.getRenderer()+"!");
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Tried to call illegal render "+m.getRenderer()+"!");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RegistrationException(ElectriCraft.instance, "No class found for Renderer "+m.getRenderer()+"!");
		}
	}

}
