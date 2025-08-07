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


import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import reika.dragonapi.DragonOptions;

public class ElectriClient extends ElectriCommon
{
//	public static final ElectriItemRenderer machineItems = new ElectriItemRenderer();

//	public static WireRenderer wire;
	public static CableRenderer cable;
//	public static BatteryRenderer battery;

	@Override
	public void registerSounds() {
		//NeoForge.EVENT_BUS.register(new SoundLoader(RotaryCraft.getInstance(), SoundRegistry.soundList, SoundRegistry.SOUND_FOLDER));
	}

	@Override
	public void registerRenderers() {
		/*wireRender = RenderingRegistry.getNextAvailableRenderId();
		wire = new WireRenderer(wireRender);
		RenderingRegistry.registerBlockHandler(wireRender, wire);

		cableRender = RenderingRegistry.getNextAvailableRenderId();
		cable = new CableRenderer(cableRender);
		RenderingRegistry.registerBlockHandler(cableRender, cable);

		batteryRender = RenderingRegistry.getNextAvailableRenderId();
		battery = new BatteryRenderer(batteryRender);
		RenderingRegistry.registerBlockHandler(batteryRender, battery);*/

		if (DragonOptions.NORENDERS.getState()) {
			ElectriCraft.LOGGER.info("Disabling all machine renders for FPS and lag profiling.");
		}
		else {
			this.loadModels();
		}
	}

	@Override
	public void addArmorRenders() {

	}

	public void loadModels() {
	/*	for (int i = 0; i < ElectriTiles.teList.length; i++) {
			ElectriTiles m = ElectriTiles.teList[i];
			if (m.hasRender()) {
				ElectriTERenderer render = ElectriRenderList.instantiateRenderer(m);
				ClientRegistry.bindBlockEntitySpecialRenderer(m.getTEClass(), render);
			}
		}

		MinecraftForgeClient.registerItemRenderer(ElectriItems.PLACER.get(), machineItems);
		MinecraftForgeClient.registerItemRenderer(ElectriItems.WIRE.get(), machineItems);
		MinecraftForgeClient.registerItemRenderer(ElectriItems.BATTERY.get(), machineItems);
		MinecraftForgeClient.registerItemRenderer(ElectriItems.RFBATTERY.get(), machineItems);
		MinecraftForgeClient.registerItemRenderer(ElectriItems.EUBATTERY.get(), machineItems);*/
	}

	@Override
	public Level getClientWorld()
	{
		return Minecraft.getInstance().level;
	}

}
