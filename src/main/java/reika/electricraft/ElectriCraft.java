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

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
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

    public static ElectriConfig config;

    public static final Logger LOGGER = LogManager.getLogger();

    public ElectriCraft(final IEventBus modEventBus, final ModContainer modContainer) {
        instance = this;

        this.startTiming(LoadProfiler.LoadPhase.PRELOAD);

        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ElectriModelLayers.init(modEventBus);
        }

        ElectriBlocks.BLOCKS.register(modEventBus);
        ElectriBlocks.ITEMS.register(modEventBus);

        ElectriItems.ITEMS.register(modEventBus);
        ElectriBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ElectriPacketCore());

        // Register this mod instance on the game bus so non-static @SubscribeEvent handlers (cancelFramez) fire.
        NeoForge.EVENT_BUS.register(this);

        this.basicSetup();
        this.finishTiming();
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        this.startTiming(LoadProfiler.LoadPhase.LOAD);

        config = new ElectriConfig(instance, ElectriOptions.optionList, null);
        config.loadSubfolderedConfigFile();
        config.initProps();

        TickRegistry.instance.registerTickHandler(ElectriNetworkManager.instance);
        LuaMethod.registerMethods("reika.electricraft.auxiliary.lua");

        InterModComms.sendTo("Randomod", "blacklist", () -> this.getModContainer().getModId());

        this.finishTiming();
    }

    @SubscribeEvent
    public void cancelFramez(BlockEntityMoveEvent evt) {
        if (!this.isMovable(evt.tile)) {
            evt.setCanceled(true);
        }
    }

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



