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

import java.io.File;
import java.net.URL;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reika.dragonapi.DragonAPI;
import reika.dragonapi.auxiliary.trackers.CommandableUpdateChecker;
import reika.dragonapi.auxiliary.trackers.TickRegistry;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.instantiable.event.BlockEntityMoveEvent;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.modinteract.lua.LuaMethod;
import reika.electricraft.base.NetworkBlockEntity;
import reika.electricraft.registry.*;
import reika.electricraft.registry.ElectriModelLayers;

@Mod(ElectriCraft.MODID)
public class ElectriCraft extends DragonAPIMod {

    public static final String packetChannel = "ElectriCraftData";
    public static final String MODID = "electricraft";

    public static ElectriCraft instance;

//    public static final Block[] blocks = new Block[ElectriBlocks.blockList.length];
//    public static final Item[] items = new Item[ElectriItems.itemList.length];
	public static ElectriConfig config;

    public static final Logger LOGGER = LogManager.getLogger();

    public ElectriCraft() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        this.startTiming(LoadProfiler.LoadPhase.PRELOAD);

        modEventBus.addListener(this::commonSetup);
//		this.verifyInstallation();
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            // Client setup
            ElectriModelLayers.init(modEventBus);
        });
//		proxy.registerSounds();
//		this.addBlocks();
//		this.addItems();
//		((EnumOreBlock)ElectriBlocks.ORE.get()).register();
//    todo   ElectriTiles.loadMappings();
        ElectriBlocks.BLOCKS.register(modEventBus);
        ElectriBlocks.ITEMS.register(modEventBus);

        ElectriItems.ITEMS.register(modEventBus);
        ElectriBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ElectriPacketCore());

//		CreativeTabSorter.instance.registerCreativeTabAfter(tabElectri, RotaryCraft.ROTARY);

//        InterModComms.sendTo("zzzzzcustomconfigs", "blacklist-mod-as-output", this.getModContainer().getModId());

        this.basicSetup();
        this.finishTiming();
    }

/*	private static void addBlocks() {
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ElectriBlocks.blockList, blocks);
		for (int i = 0; i < ElectriTiles.teList.length; i++) {
			GameRegistry.registerBlockEntity(ElectriTiles.teList[i].getTEClass(), "Electri"+ElectriTiles.teList[i].getName());
			ReikaJavaLibrary.initClass(ElectriTiles.teList[i].getTEClass());
		}
	}*/

/*    protected HashMap<String, String> getDependencies() {
        HashMap map = new HashMap();
        map.put("RotaryCraft", RotaryCraft.currentVersion);
        return map;
    }*/

    public void commonSetup(FMLCommonSetupEvent event) {
        this.startTiming(LoadProfiler.LoadPhase.LOAD);

        config = new ElectriConfig(instance, ElectriOptions.optionList, null);
        config.loadSubfolderedConfigFile();
        config.initProps();
//        proxy.registerRenderers();
//        RetroGenController.instance.addHybridGenerator(ElectriOreGenerator.instance, 0, ElectriOptions.RETROGEN.getState());

//        ItemStackRepository.instance.registerClass(this, ElectriStacks.class);

        TickRegistry.instance.registerTickHandler(ElectriNetworkManager.instance);
        LuaMethod.registerMethods("reika.electricraft.auxiliary.lua");

//        IntegrityChecker.instance.addMod(instance, ElectriBlocks.blockList, ElectriItems.itemList);

//        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ElectriGuiHandler());

//	todo	if (RotaryConfig.COMMON.HANDBOOK.get())
//			PlayerFirstTimeTracker.addTracker(new ElectriBookTracker());

//		ElectriRecipes.addRecipes();

		/*if (ModList.NEI.isLoaded()) {
			for (ElectriBlocks block : ElectriBlocks.blockList)
				if (block != ElectriBlocks.ORE) {
					NEI_DragonAPI_Config.hideBlock(block.get());
				}
		}*/

//     todo   if (FMLLoader.getDist() == Dist.CLIENT)
//            ElectriDescriptions.loadData();

//		PackModificationTracker.instance.addMod(this, config);

        InterModComms.sendTo("Randomod", "blacklist", () -> this.getModContainer().getModId());

        //ReikaEEHelper.blacklistRegistry(ElectriBlocks.blockList);
        //ReikaEEHelper.blacklistRegistry(ElectriItems.itemList);

	/*	SensitiveItemRegistry.instance.registerItem(this, ElectriItems.PLACER.get(), true);
		SensitiveItemRegistry.instance.registerItem(this, ElectriItems.BATTERY.get(), true);
		SensitiveItemRegistry.instance.registerItem(this, ElectriItems.RFBATTERY.get(), true);

		SensitiveItemRegistry.instance.registerItem(this, WireType.SUPERCONDUCTOR.getCraftedProduct(), true);
		SensitiveItemRegistry.instance.registerItem(this, WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct(), true);*/

/*      for (int i = 0; i < ElectriTiles.teList.length; i++) {
			ElectriTiles m = ElectriTiles.teList[i];
			if (ModList.CHROMATICRAFT.isLoaded()) {
				AcceleratorBlacklist.addBlacklist(m.getTEClass(), m.getName(), BlacklistReason.EXPLOIT);
			}
			TimeTorchHelper.blacklistBlockEntity(m.getTEClass());
		}*/

//		ElectriRecipes.addPostLoadRecipes();

		/*if (ModList.THAUMCRAFT.isLoaded()) {
			for (int i = 0; i < ElectriOres.oreList.length; i++) {
				ElectriOres ore = ElectriOres.oreList[i];
				ItemStack block = ore.getOreBlock();
				ItemStack drop = ore.getProduct();
				//ReikaThaumHelper.addAspects(block, Aspect.STONE, 1);
			}

			ReikaThaumHelper.addAspects(ElectriOres.PLATINUM.getOreBlock(), Aspect.GREED, 6, Aspect.METAL, 2);
			ReikaThaumHelper.addAspects(ElectriOres.NICKEL.getOreBlock(), Aspect.METAL, 1);

			ReikaThaumHelper.addAspects(ElectriOres.PLATINUM.getProduct(), Aspect.GREED, 6, Aspect.METAL, 2);
			ReikaThaumHelper.addAspects(ElectriOres.NICKEL.getProduct(), Aspect.METAL, 2);
		}*/

        this.finishTiming();
    }

    @SubscribeEvent
    public void cancelFramez(BlockEntityMoveEvent evt) {
        if (!this.isMovable(evt.tile)) {
            evt.setCanceled(true);
        }
    }

/*	@SubscribeEvent
//	@SideOnly(Dist.CLIENT)
	public void loadTextures(TextureStitchEvent.Pre evt) {
		if (evt.map.getTextureType() == 0) {
			for (int i = 0; i < BatteryType.batteryList.length; i++) {
				BatteryType type = BatteryType.batteryList[i];
				type.loadIcon(evt.map);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	@ClassDependent("WayofTime.alchemicalWizardry.api.event.TeleposeEvent")
	public void noTelepose(TeleposeEvent evt) {
		if (!this.isMovable(evt.getInitialTile()) || !this.isMovable(evt.getFinalTile()))
			evt.setCanceled(true);
	}*/

    private boolean isMovable(BlockEntity te) {
        return !(te instanceof NetworkBlockEntity);
    }

    @Override
    public String getDisplayName() {
        return "ElectriCraft";
    }

    @Override
    public String getModAuthorName() {
        return "Reika";
    }

    @Override
    public URL getDocumentationSite() {
        return DragonAPI.getReikaForumPage();
    }

    @Override
    public URL getBugSite() {
        return DragonAPI.getReikaGithubPage();
    }

    @Override
    public String getUpdateCheckURL() {
        return CommandableUpdateChecker.reikaURL;
    }

    @Override
    public String getModId() {
        return "electricraft";
    }

    @Override
    public File getConfigFolder() {
        return null;//todo config.getConfigFolder();
    }
}
