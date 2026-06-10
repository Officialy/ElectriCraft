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
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
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
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriItems;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 26.1 model + blockstate provider for ElectriCraft.
 * <p>
 * Same reflective sink-access pattern as the RotaryCraft / GeoStrata providers (vanilla keeps
 * the single-block helpers private). Emits trivial cube models for every block + flat item
 * models for every non-BlockItem.
 */
public class ElectriModelProvider extends ModelProvider {

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

        java.util.Set<Item> blockItemsHandled = new java.util.HashSet<>();

        // BLOCKS
        for (var holder : ElectriBlocks.BLOCKS.getEntries()) {
            Block block = holder.get();
            Identifier blockModelId = ModelTemplates.CUBE_ALL.create(
                    block, TextureMapping.cube(block), modelOut);
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
            Identifier itemModelId = ModelTemplates.FLAT_ITEM.create(
                    ModelLocationUtils.getModelLocation(item),
                    TextureMapping.layer0(item),
                    modelOut);
            itemModelOut.accept(item, ItemModelUtils.plainModel(itemModelId));
        }
    }

    /** Y-rotation dispatch matching vanilla furnace orientation (N=0°, E=90°, S=180°, W=270°). */
    private static PropertyDispatch<VariantMutator> horizontalFacingDispatch(
            net.minecraft.world.level.block.state.properties.EnumProperty<Direction> property) {
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
