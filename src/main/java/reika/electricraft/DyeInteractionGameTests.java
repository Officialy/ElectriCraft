package reika.electricraft;

import java.util.HashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import reika.dragonapi.libraries.registry.ReikaDyeHelper;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.electricraft.base.BlockEntityResistorBase.ColorBand;
import reika.electricraft.blockentities.BlockEntityMotor;
import reika.electricraft.registry.ElectriBlocks;
import reika.rotarycraft.registry.RotaryItems;

/** Native registry reload and held-item block interactions; synchronous bindings are restored in finally. */
final class DyeInteractionGameTests {
    private static final BlockPos POSITION = new BlockPos(4, 1, 4);

    static void nativeIdentities(GameTestHelper h) {
        for (var color : ReikaDyeHelper.dyes) {
            var stack = color.getStackOf();
            h.assertTrue(ReikaDyeHelper.getColorFromItem(stack) == color, "native dye identity: " + color);
            h.assertTrue(stack.is(color.dye.getTag()), "native colour tag: " + color);
            h.assertTrue(color.getWoolStack().is(net.minecraft.world.level.block.Blocks.WOOL.pick(color.dye).asItem()),
                    "native wool identity: " + color);
        }
        var stack = new ItemStack(Items.APPLE);
        stack.set(DataComponents.DYE, DyeColor.BLACK);
        h.assertTrue(ReikaDyeHelper.getColorFromItem(stack) == ReikaDyeHelper.BLACK, "typed BLACK is included");
        h.succeed();
    }

    static void tagRebinding(GameTestHelper h) {
        var registry = BuiltInRegistries.ITEM;
        var original = new HashMap<TagKey<Item>, List<Holder<Item>>>();
        registry.getTags().forEach(tag -> original.put(tag.key(), List.copyOf(tag.contents())));
        var changed = new HashMap<>(original);
        changed.put(DyeColor.BLACK.getTag(), List.of(Items.STONE.builtInRegistryHolder()));
        changed.put(DyeColor.RED.getTag(), List.of(Items.STONE.builtInRegistryHolder(), Items.DIAMOND.builtInRegistryHolder()));
        try {
            ReikaDyeHelper.buildItemCache();
            registry.prepareTagReload(new TagLoader.LoadResult<>(registry.key(), changed)).apply();
            NeoForge.EVENT_BUS.post(new TagsUpdatedEvent.ClientPacketReceived(h.getLevel().registryAccess(), true));
            h.assertTrue(!ReikaDyeHelper.isDyeItem(new ItemStack(Items.STONE)), "integrated client must not replace server static cache");
            // Native tag apply does not prepare unrelated single-use reload listeners for event-bus replay.
            ReikaDyeHelper.onTagsUpdated(new TagsUpdatedEvent.ServerDataLoad(
                    h.getLevel().getServer().getServerResources().managers(), h.getLevel().registryAccess()));
            var stone = new ItemStack(Items.STONE, 32);
            var colors = ReikaDyeHelper.getColorsFromItem(stone);
            h.assertTrue(colors != null && List.copyOf(colors).equals(List.of(ReikaDyeHelper.BLACK, ReikaDyeHelper.RED)),
                    "all colour memberships survive; ambiguous tags use deterministic original order");
            var heldView = colors;
            changed.put(DyeColor.BLACK.getTag(), List.of());
            changed.put(DyeColor.RED.getTag(), List.of());
            changed.put(DyeColor.BLUE.getTag(), List.of(Items.STONE.builtInRegistryHolder()));
            registry.prepareTagReload(new TagLoader.LoadResult<>(registry.key(), changed)).apply();
            NeoForge.EVENT_BUS.post(new TagsUpdatedEvent.ClientPacketReceived(h.getLevel().registryAccess(), false));
            h.assertTrue(ReikaDyeHelper.getColorFromItem(stone) == ReikaDyeHelper.BLUE,
                    "remote tag update adds/recolours existing cached identities");
            h.assertTrue(!ReikaDyeHelper.isDyeItem(new ItemStack(Items.DIAMOND)), "remote update removes former dyes");
            h.assertTrue(List.copyOf(heldView).equals(List.of(ReikaDyeHelper.BLACK, ReikaDyeHelper.RED)), "old query view is immutable across reload");
            h.assertTrue(ReikaDyeHelper.getColorFromItem(ReikaDyeHelper.RED.getStackOf()) == ReikaDyeHelper.RED,
                    "typed native dye identity survives a missing colour tag");
            // Exercise a tag-only custom pigment through the actual consumer before restoring bindings.
            var resistor = resistor(h, false);
            var player = h.makeMockPlayer(GameType.SURVIVAL);
            h.assertTrue(click(h, player, stone, InteractionHand.MAIN_HAND, .8125) == InteractionResult.SUCCESS,
                    "tag-only pigment reaches resistor interaction");
            h.assertTrue(resistor.getColorBands()[0] == ColorBand.BLUE && stone.getCount() == 31, "tag-only colour and survival consumption");
        } finally {
            registry.prepareTagReload(new TagLoader.LoadResult<>(registry.key(), original)).apply();
            ReikaDyeHelper.onTagsUpdated(new TagsUpdatedEvent.ServerDataLoad(
                    h.getLevel().getServer().getServerResources().managers(), h.getLevel().registryAccess()));
        }
        h.assertTrue(!ReikaDyeHelper.isDyeItem(new ItemStack(Items.STONE)), "restored bindings remove temporary pigments");
        h.succeed();
    }

    static void orientation(GameTestHelper h) {
        for (boolean precise : new boolean[]{false, true}) {
            var resistor = resistor(h, precise);
            for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                for (boolean flipped : new boolean[]{false, true}) {
                    for (int digit = 0; digit < resistor.getColorBands().length; digit++) {
                        for (int i = 1; i <= resistor.getColorBands().length; i++) resistor.setColor(ColorBand.BLACK, i);
                        resistor.setFacing(direction); resistor.isFlipped = flipped;
                        double coordinate = .1875 + .1875 * digit;
                        if (flipped && direction.getStepZ() != 0) coordinate = 1 - coordinate;
                        if (direction == Direction.EAST || direction == Direction.SOUTH) coordinate = 1 - coordinate;
                        var stack = new ItemStack(Items.DYE.red(), 2);
                        var player = h.makeMockPlayer(GameType.SURVIVAL);
                        h.assertTrue(click(h, player, stack, InteractionHand.MAIN_HAND, coordinate) == InteractionResult.SUCCESS,
                                "band hit: precise=" + precise + ", facing=" + direction + ", flipped=" + flipped + ", digit=" + digit);
                        for (int i = 0; i < resistor.getColorBands().length; i++)
                            h.assertTrue(resistor.getColorBands()[i] == (i == digit ? ColorBand.RED : ColorBand.BLACK), "only clicked band changes");
                        int expected = digit == resistor.getColorBands().length - 1 ? 0 : (int)(2 * Math.pow(10, resistor.getColorBands().length - 2 - digit));
                        h.assertTrue(resistor.getCurrentLimit() == expected && stack.getCount() == 1, "current recalculation and one-item consumption");
                    }
                }
            }
        }
        h.succeed();
    }

    static void consumption(GameTestHelper h) {
        var resistor = resistor(h, false);
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 7));
        var offhand = new ItemStack(Items.DYE.lime(), 2);
        h.assertTrue(click(h, player, offhand, InteractionHand.OFF_HAND, .8125) == InteractionResult.SUCCESS, "offhand pigment is used");
        h.assertTrue(offhand.getCount() == 1 && player.getMainHandItem().getCount() == 7 && resistor.getCurrentLimit() == 50,
                "offhand consumes only its dye and maps lime to the original GREEN digit");
        var creative = h.makeMockPlayer(GameType.CREATIVE);
        var white = new ItemStack(Items.DYE.white(), 2);
        h.assertTrue(click(h, creative, white, InteractionHand.MAIN_HAND, .8125) == InteractionResult.SUCCESS && white.getCount() == 2,
                "creative band editing retains dye");
        CompoundTag saved = new CompoundTag(); resistor.writeSyncTag(saved);
        h.assertTrue(saved.getIntOr("band1", -1) == ColorBand.WHITE.ordinal() && saved.getIntOr("sel", -1) == 90,
                "client sync/persistence carries updated band and recalculated current");
        var brown = new ItemStack(Items.DYE.brown(), 2);
        click(h, player, brown, InteractionHand.MAIN_HAND, .625);
        var blue = new ItemStack(Items.DYE.blue(), 2);
        click(h, player, blue, InteractionHand.MAIN_HAND, .4375);
        h.assertTrue(resistor.getCurrentLimit() == 91_000_000 && brown.getCount() == 1 && blue.getCount() == 1,
                "native interactions retain the original decimal digits and power-of-ten multiplier");
        h.succeed();
    }

    static void rejection(GameTestHelper h) {
        for (boolean precise : new boolean[]{false, true}) {
            var resistor = resistor(h, precise);
            var player = h.makeMockPlayer(GameType.SURVIVAL);
            double multiplier = 1 - (.1875 + .1875 * (resistor.getColorBands().length - 1));
            for (var color : new DyeColor[]{DyeColor.GRAY, DyeColor.WHITE}) {
                var stack = new ItemStack(Items.DYE.pick(color), 2);
                h.assertTrue(click(h, player, stack, InteractionHand.MAIN_HAND, multiplier) == InteractionResult.PASS && stack.getCount() == 2,
                        "GRAY/WHITE multiplier is rejected without consumption");
            }
            for (var stack : List.of(new ItemStack(Items.DYE.green(), 2), new ItemStack(Items.DIAMOND, 2))) {
                click(h, player, stack, InteractionHand.MAIN_HAND, .8125);
                h.assertTrue(stack.getCount() == 2 && resistor.getCurrentLimit() == 0, "unsupported colour/non-dye cannot edit");
            }
            var gap = new ItemStack(Items.DYE.red(), 2);
            h.assertTrue(click(h, player, gap, InteractionHand.MAIN_HAND, .71875) == InteractionResult.PASS && gap.getCount() == 2,
                    "between-band hit cannot consume dye");
            h.assertTrue(!resistor.setColor(ReikaDyeHelper.RED, 0) && !resistor.setColor(ReikaDyeHelper.RED, 5), "invalid public digit bounds do not mutate state");
        }
        h.succeed();
    }

    static void motorUpgrade(GameTestHelper h) {
        h.setBlock(POSITION, ElectriBlocks.MOTOR.get());
        var motor = h.getBlockEntity(POSITION, BlockEntityMotor.class);
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        for (var item : List.of(RotaryItems.INDUCTIVE_INGOT.get(), RotaryItems.TUNGSTEN_ALLOY_INGOT.get(), RotaryItems.BEDROCK_ALLOY_INGOT.get())) {
            var stack = new ItemStack(item, 2);
            h.assertTrue(motor.canUpgrade(stack), "client preview accepts a higher tier");
            h.assertTrue(click(h, player, stack, InteractionHand.MAIN_HAND, .5) == InteractionResult.SUCCESS && stack.getCount() == 1,
                    "existing motor upgrade survives native interaction migration");
            h.assertTrue(!motor.canUpgrade(stack), "client preview rejects duplicate or lower tier");
            click(h, player, stack, InteractionHand.MAIN_HAND, .5);
            h.assertTrue(stack.getCount() == 1, "duplicate upgrade is not consumed");
        }
        h.succeed();
    }

    private static BlockEntityResistorBase resistor(GameTestHelper h, boolean precise) {
        h.setBlock(POSITION, net.minecraft.world.level.block.Blocks.AIR);
        h.setBlock(POSITION, precise ? ElectriBlocks.PRECISE_RESISTOR.get() : ElectriBlocks.RESISTOR.get());
        var resistor = h.getBlockEntity(POSITION, BlockEntityResistorBase.class);
        resistor.setFacing(Direction.EAST);
        return resistor;
    }

    private static InteractionResult click(GameTestHelper h, Player player, ItemStack stack, InteractionHand hand, double coordinate) {
        player.setItemInHand(hand, stack);
        BlockPos pos = h.absolutePos(POSITION);
        Vec3 location = new Vec3(pos.getX() + coordinate, pos.getY() + .5, pos.getZ() + coordinate);
        return h.getLevel().getBlockState(pos).useItemOn(stack, h.getLevel(), player, hand,
                new BlockHitResult(location, Direction.UP, pos, false));
    }
}
