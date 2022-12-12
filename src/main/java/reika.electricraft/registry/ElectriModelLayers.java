package reika.electricraft.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import reika.electricraft.ElectriCraft;
import reika.electricraft.renders.*;
import reika.electricraft.renders.model.*;
import reika.rotarycraft.modinterface.model.ElecMotorModel;
import reika.rotarycraft.modinterface.model.GeneratorModel;

public class ElectriModelLayers {
    public static final ModelLayerLocation METER = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "meter"), "main");
    public static final ModelLayerLocation RESISTOR_BASE = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "resistor_base"), "main");
    public static final ModelLayerLocation RESISTOR = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "resistor"), "main");
    public static final ModelLayerLocation PRECISE_RESISTOR = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "precise_resistor"), "main");
    public static final ModelLayerLocation FUSE = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "fuse"), "main");
    public static final ModelLayerLocation RELAY = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "relay"), "main");

    public static final ModelLayerLocation TRANSFORMER = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "transformer"), "main");
    public static final ModelLayerLocation MOTOR = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "motor"), "main");
    public static final ModelLayerLocation GENERATOR = new ModelLayerLocation(new ResourceLocation(ElectriCraft.MODID, "generator"), "main");

    public static void init(IEventBus bus) {
        bus.addListener(ElectriModelLayers::registerEntityRenderers);
        bus.addListener(ElectriModelLayers::registerLayerDefinitions);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ElectriBlockEntities.METER.get(), RenderElectricMeter::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.FUSE.get(), RenderFuse::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.RELAY.get(), RenderRelay::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.RESISTOR.get(), RenderResistor::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.PRECISE_RESISTOR.get(), RenderPreciseResistor::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.TRANSFORMER.get(), RenderTransformer::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.MOTOR.get(), RenderMotor::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.GENERATOR.get(), RenderGenerator::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.BATTERY.get(), RenderModBattery::new);
        event.registerBlockEntityRenderer(ElectriBlockEntities.WIRE.get(), RenderWire::new);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RESISTOR_BASE, ResistorBaseModel::createLayer);
        event.registerLayerDefinition(PRECISE_RESISTOR, PreciseResistorModel::createLayer);
        event.registerLayerDefinition(METER, MeterModel::createLayer);
        event.registerLayerDefinition(RESISTOR, ResistorModel::createLayer);
        event.registerLayerDefinition(FUSE, FuseModel::createLayer);
        event.registerLayerDefinition(RELAY, RelayModel::createLayer);
        event.registerLayerDefinition(TRANSFORMER, TransformerModel::createLayer);
        event.registerLayerDefinition(MOTOR, ElecMotorModel::createLayer);
        event.registerLayerDefinition(GENERATOR, GeneratorModel::createLayer);
    }
}
