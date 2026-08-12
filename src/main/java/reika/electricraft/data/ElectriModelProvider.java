package reika.electricraft.data;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.blocks.BlockWire;
import reika.electricraft.blocks.BlockElectricBattery;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.renders.item.ElectriCasingItemRenderer;
import reika.electricraft.renders.item.ElectriMachineItemRenderer;
import reika.electricraft.renders.item.ElectriWireItemRenderer;
import reika.electricraft.renders.item.ElectriCableItemRenderer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 26.2 model + blockstate provider for ElectriCraft.
 * <p>
 * Same reflective sink-access pattern as the RotaryCraft / GeoStrata providers (vanilla keeps
 * the single-block helpers private). Emits trivial cube models for every block + flat item
 * models for ordinary non-BlockItems; legacy custom-render items use the special-model pipeline.
 */
public class ElectriModelProvider extends ModelProvider {

    /** Item-only source-parity models: three small legacy inventory wire segments, not a cube stub. */
    private static final ModelTemplate WIRE_ITEM_BARE = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "block/wire_item_bare")),
            java.util.Optional.empty(), TextureSlot.PARTICLE, TextureSlot.SIDE, TextureSlot.END);
    private static final ModelTemplate WIRE_ITEM_INSULATED = new ModelTemplate(
            java.util.Optional.of(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "block/wire_item_insulated")),
            java.util.Optional.empty(), TextureSlot.PARTICLE, TextureSlot.SIDE, TextureSlot.END);

    public ElectriModelProvider(PackOutput output) {
        super(output, ElectriCraft.MODID);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        Consumer<BlockModelDefinitionGenerator> blockStateOut;
        ItemModelOutput itemModelOut;
        BiConsumer<Identifier, ModelInstance> modelOut;
        try {
            Field bsf = BlockModelGenerators.class.getDeclaredField("blockStateOutput");
            bsf.setAccessible(true);
            blockStateOut = (Consumer<BlockModelDefinitionGenerator>) bsf.get(blockModels);

            Field imf = BlockModelGenerators.class.getDeclaredField("itemModelOutput");
            imf.setAccessible(true);
            itemModelOut = (ItemModelOutput) imf.get(blockModels);

            Field mof = BlockModelGenerators.class.getDeclaredField("modelOutput");
            mof.setAccessible(true);
            modelOut = (BiConsumer<Identifier, ModelInstance>) mof.get(blockModels);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Failed to reflectively access BlockModelGenerators sinks — vanilla shape changed?", e);
        }

        Set<Item> blockItemsHandled = new HashSet<>();

        // 1.7.10's ElectriBlock.registerBlockIcons clad every generic machine in RotaryCraft's steel
        // ("rotarycraft:steel"); only the wire, battery, cable and ore blocks overrode it with art of
        // their own. Reproduce that rather than giving each machine a texture it never had.
        Material steel = new Material(Identifier.fromNamespaceAndPath("rotarycraft", "block/steel"));
        Set<Block> steelClad = new HashSet<>(Set.of(
                ElectriBlocks.GENERATOR.get(), ElectriBlocks.MOTOR.get(), ElectriBlocks.METER.get(),
                ElectriBlocks.PRECISE_RESISTOR.get(), ElectriBlocks.TRANSFORMER.get(),
                ElectriBlocks.RESISTOR.get(), ElectriBlocks.RELAY.get()));
        java.util.Collections.addAll(steelClad, ElectriBlocks.getFuseBlocks());

        // BLOCKS
        for (var holder : ElectriBlocks.BLOCKS.getEntries()) {
            Block block = holder.get();
            if (block instanceof BlockElectricBattery battery) {
                // The six-tier battery is entirely BER-drawn: tier face art and the charged
                // emissive overlay belong to the live block entity, not a generic cube model.
                String tier = battery.getBatteryType().name().toLowerCase(java.util.Locale.ROOT);
                Material particle = new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/battery/" + tier));
                Identifier blockModelId = ModelTemplates.PARTICLE_ONLY.create(block, TextureMapping.particle(particle), modelOut);
                blockStateOut.accept(MultiVariantGenerator.dispatch(block,
                        new MultiVariant(WeightedList.of(new Variant(blockModelId)))));
                Item asItem = block.asItem();
                TextureMapping itemTextures = new TextureMapping()
                        .put(TextureSlot.BOTTOM, new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/battery/_bottom")))
                        .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/battery/_top")))
                        .put(TextureSlot.SIDE, particle);
                Identifier itemBase = ModelTemplates.CUBE_BOTTOM_TOP.create(
                        ModelLocationUtils.getModelLocation(asItem), itemTextures, modelOut);
                itemModelOut.accept(asItem, ItemModelUtils.specialModel(itemBase,
                        new ElectriCasingItemRenderer.Unbaked("battery", battery.getBatteryType().name())));
                blockItemsHandled.add(asItem);
                continue;
            }
            if (block == ElectriBlocks.RFBATTERY.get()) {
                // Mod-interface batteries have one fixed source texture family; their real
                // logarithmic energy bands are supplied by RenderModBattery above this baked shell.
                TextureMapping textures = new TextureMapping()
                        .put(TextureSlot.BOTTOM, new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/battery/_bottom")))
                        .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/battery/_top")))
                        .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/battery/rf")));
                Identifier blockModelId = ModelTemplates.CUBE_BOTTOM_TOP.create(block, textures, modelOut);
                blockStateOut.accept(MultiVariantGenerator.dispatch(block,
                        new MultiVariant(WeightedList.of(new Variant(blockModelId)))));
                continue;
            }
            if (steelClad.contains(block)) {
                // Every ElectriBlock machine returned render type -1 in V31a: its real Techne
                // mesh is emitted by the block-entity renderer, never by a steel cube world model.
                // Keep that world slot particle-only while retaining the source's steel shell for
                // the ordinary inventory BlockItem presentation.
                Identifier blockModelId = ModelTemplates.PARTICLE_ONLY.create(block, TextureMapping.particle(steel), modelOut);
                blockStateOut.accept(MultiVariantGenerator.dispatch(block,
                        new MultiVariant(WeightedList.of(new Variant(blockModelId)))).with(horizontalFacingDispatch(BlockStateProperties.HORIZONTAL_FACING)));
                Item asItem = block.asItem();
                if (asItem != Items.AIR) {
                    Identifier itemModelId = ModelTemplates.CUBE_ALL.create(ModelLocationUtils.getModelLocation(asItem), TextureMapping.cube(steel), modelOut);
                    String renderer = block == ElectriBlocks.GENERATOR.get()
                            ? "generator"
                            : block == ElectriBlocks.MOTOR.get()
                            ? "motor"
                            : ElectriBlocks.isFuse(block)
                            ? "fuse"
                            : BuiltInRegistries.BLOCK.getKey(block).getPath();
                    itemModelOut.accept(asItem, ItemModelUtils.specialModel(itemModelId,
                            new ElectriMachineItemRenderer.Unbaked(renderer)));
                    blockItemsHandled.add(asItem);
                }
                continue;
            }
            if (block == ElectriBlocks.RF_CABLE.get()) {
                // BlockElectriCable used the original dynamic CableRenderer; its real centre and
                // end sprites live in textures/blocks/rf*.png, not the later fabricated cube.
                Material centre = new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/rf"));
                Material end = new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/rf_end"));
                Identifier blockModelId = ModelTemplates.PARTICLE_ONLY.create(block, TextureMapping.particle(centre), modelOut);
                blockStateOut.accept(MultiVariantGenerator.dispatch(block,
                        new MultiVariant(WeightedList.of(new Variant(blockModelId)))));
                Item asItem = block.asItem();
                if (asItem != Items.AIR) {
                    TextureMapping itemTextures = new TextureMapping()
                            .put(TextureSlot.PARTICLE, centre)
                            .put(TextureSlot.SIDE, centre)
                            .put(TextureSlot.END, end);
                    Identifier itemModelId = WIRE_ITEM_BARE.create(ModelLocationUtils.getModelLocation(asItem), itemTextures, modelOut);
                    itemModelOut.accept(asItem, ItemModelUtils.specialModel(itemModelId,
                            new ElectriCableItemRenderer.Unbaked()));
                    blockItemsHandled.add(asItem);
                }
                continue;
            }
            if (block == ElectriBlocks.WIRELESS_CHARGER.get()) {
                Material side = new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID,
                        "blocks/wireless/side_0"));
                Identifier blockModelId = ModelTemplates.PARTICLE_ONLY.create(
                        block, TextureMapping.particle(side), modelOut);
                blockStateOut.accept(MultiVariantGenerator.dispatch(block,
                        new MultiVariant(WeightedList.of(new Variant(blockModelId)))));
                Item asItem = block.asItem();
                if (asItem != Items.AIR) {
                    Identifier itemBase = ModelTemplates.CUBE_ALL.create(
                            ModelLocationUtils.getModelLocation(asItem), TextureMapping.cube(side), modelOut);
                    itemModelOut.accept(asItem, ItemModelUtils.specialModel(itemBase,
                            new ElectriCasingItemRenderer.Unbaked("wireless_charger")));
                    blockItemsHandled.add(asItem);
                }
                continue;
            }
            if (block instanceof BlockWire wire) {
                // Wires are entirely BER-drawn in-world. A particle-only model prevents vanilla
                // from drawing the made-up full cube behind the real connection geometry, while
                // the item receives the exact three-segment legacy silhouette via a reusable
                // datagen template and the material's real centre/end sprites.
                String base = "blocks/wire/" + wire.getWireType().getIconTexture() + (wire.isInsulated() ? "_ins" : "");
                Material centre = new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, base));
                Material end = new Material(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, base + "_end"));
                Identifier blockModelId = ModelTemplates.PARTICLE_ONLY.create(block, TextureMapping.particle(centre), modelOut);
                MultiVariant single = new MultiVariant(WeightedList.of(new Variant(blockModelId)));
                blockStateOut.accept(MultiVariantGenerator.dispatch(block, single));
                Item asItem = block.asItem();
                if (asItem != Items.AIR) {
                    TextureMapping itemTextures = new TextureMapping()
                            .put(TextureSlot.PARTICLE, centre)
                            .put(TextureSlot.SIDE, centre)
                            .put(TextureSlot.END, end);
                    Identifier itemModelId = (wire.isInsulated() ? WIRE_ITEM_INSULATED : WIRE_ITEM_BARE)
                            .create(ModelLocationUtils.getModelLocation(asItem), itemTextures, modelOut);
                    itemModelOut.accept(asItem, ItemModelUtils.specialModel(itemModelId,
                            new ElectriWireItemRenderer.Unbaked(wire.getWireType().name(), wire.isInsulated())));
                    blockItemsHandled.add(asItem);
                }
                continue;
            }
            TextureMapping textures = TextureMapping.cube(block);
            Identifier blockModelId = ModelTemplates.CUBE_ALL.create(
                    block, textures, modelOut);
            MultiVariant single = new MultiVariant(
                    WeightedList.of(new Variant(blockModelId)));

            // Every ElectriBlock subclass carries HorizontalDirectionalBlock.FACING and is placed
            // along the cardinals — emit a 4-way Y-rotation dispatch so the model rotates with the
            // player's placement direction. Non-ElectriBlock blocks (none currently, but cheap to
            // guard) fall back to a single-variant blockstate.
            MultiVariantGenerator gen = MultiVariantGenerator.dispatch(block, single);
            if (block instanceof ElectriBlock) {
                gen = gen.with(horizontalFacingDispatch(BlockStateProperties.HORIZONTAL_FACING));
            }
            blockStateOut.accept(gen);

            Item asItem = block.asItem();
            if (asItem != Items.AIR) {
                itemModelOut.accept(asItem, ItemModelUtils.plainModel(blockModelId));
                blockItemsHandled.add(asItem);
            }
        }

        // STANDALONE ITEMS
        for (var holder : ElectriItems.ITEMS.getEntries()) {
            Item item = holder.get();
            if (blockItemsHandled.contains(item)) continue; // already handled as a BlockItem
            if (item == ElectriItems.RFBATTERY.get()) {
                boolean rf = true;
                TextureMapping textures = new TextureMapping()
                        .put(TextureSlot.BOTTOM, new Material(Identifier.fromNamespaceAndPath(
                                ElectriCraft.MODID, "blocks/battery/_bottom")))
                        .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(
                                ElectriCraft.MODID, "blocks/battery/_top")))
                        .put(TextureSlot.SIDE, new Material(Identifier.fromNamespaceAndPath(
                                ElectriCraft.MODID, "blocks/battery/" + (rf ? "rf" : "redstone"))));
                Identifier itemBase = ModelTemplates.CUBE_BOTTOM_TOP.create(
                        ModelLocationUtils.getModelLocation(item), textures, modelOut);
                itemModelOut.accept(item, ItemModelUtils.specialModel(itemBase,
                        new ElectriCasingItemRenderer.Unbaked(rf ? "rf_battery" : "battery")));
                continue;
            }
            Identifier itemModelId = ModelTemplates.FLAT_ITEM.create(
                    ModelLocationUtils.getModelLocation(item),
                    TextureMapping.layer0(item),
                    modelOut);
            itemModelOut.accept(item, ItemModelUtils.plainModel(itemModelId));
        }
    }

    /** Y-rotation dispatch matching vanilla furnace orientation (N=0°, E=90°, S=180°, W=270°). */
    private static PropertyDispatch<VariantMutator> horizontalFacingDispatch(
            EnumProperty<Direction> property) {
        return PropertyDispatch.modify(property)
                .select(Direction.NORTH, BlockModelGenerators.NOP)
                .select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                .select(Direction.WEST, BlockModelGenerators.Y_ROT_270);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.listElements()
                .filter(h -> h.getKey().identifier().getNamespace().equals(ElectriCraft.MODID));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(h -> h.getKey().identifier().getNamespace().equals(ElectriCraft.MODID));
    }
}
