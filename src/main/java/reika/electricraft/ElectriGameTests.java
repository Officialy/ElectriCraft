package reika.electricraft;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.blockentities.BlockEntityBattery;
import reika.electricraft.blockentities.BlockEntityFuse;
import reika.electricraft.blockentities.BlockEntityGenerator;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blockentities.BlockEntityWire;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;
import reika.electricraft.data.ElectriTestStructureProvider;
import reika.electricraft.registry.BatteryType;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriItems;
import reika.electricraft.registry.ElectriTabs;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.registry.WireType;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.rotarycraft.base.blocks.BlockRotaryCraftMachine;
import reika.rotarycraft.blockentities.transmission.BlockEntityCreativeCoil;
import reika.rotarycraft.registry.RotaryBlocks;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * Survival-facing, dedicated-server GameTests for ElectriCraft. Start
 * {@code gradlew :ElectriCraft:runServer}, then execute
 * {@code test run electricraft:*} in the dedicated-server console.
 */
public final class ElectriGameTests {

    private ElectriGameTests() {}

    public static final DeferredRegister<MapCodec<? extends GameTestInstance>> TEST_INSTANCE_TYPES =
            DeferredRegister.create(Registries.TEST_INSTANCE_TYPE, ElectriCraft.MODID);

    static {
        TEST_INSTANCE_TYPES.register("direct", () -> DirectInstance.CODEC);
    }

    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> env = event.registerEnvironment(
                Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "default"),
                new TestEnvironmentDefinition.AllOf(List.of()));

        register(event, env, "machine_block_entities", 60, ElectriGameTests::machineBlockEntities);
        register(event, env, "machine_registry_roundtrip", 20, ElectriGameTests::machineRegistryRoundtrip);
        register(event, env, "battery_fuse_identities", 40, ElectriGameTests::batteryFuseIdentities);
        register(event, env, "wire_variants_connect", 40, ElectriGameTests::wireVariantsConnect);
        register(event, env, "transformer_orientation", 40, ElectriGameTests::transformerOrientation);
        register(event, env, "mechanical_to_battery_power", 100, ElectriGameTests::mechanicalToBatteryPower);
        register(event, env, "wire_and_machine_shapes", 40, ElectriGameTests::wireAndMachineShapes);
        register(event, env, "battery_survival_placement", 20, ElectriGameTests::batterySurvivalPlacement);
        register(event, env, "rf_battery_sided_energy", 20, ElectriGameTests::rfBatterySidedEnergy);
        register(event, env, "stateful_block_drops", 20, ElectriGameTests::statefulBlockDrops);
        register(event, env, "machine_survival_break", 20, ElectriGameTests::machineSurvivalBreak);
        register(event, env, "survival_recipe_catalog", 20, ElectriGameTests::survivalRecipeCatalog);
    }

    /** Catches missing/wrong BlockEntityType registrations and first-tick lifecycle crashes. */
    private static void machineBlockEntities(GameTestHelper helper) {
        ElectriTiles[] machines = ElectriTiles.values();
        for (int i = 0; i < machines.length; i++) {
            ElectriTiles machine = machines[i];
            Block block = machine == ElectriTiles.WIRE ? ElectriBlocks.COPPER_WIRE.get() : machine.getBlock();
            BlockPos relative = new BlockPos(1 + (i % 5) * 2, 1, 1 + (i / 5) * 2);
            helper.setBlock(relative, block);

            BlockEntity be = helper.getLevel().getBlockEntity(helper.absolutePos(relative));
            helper.assertTrue(be != null && machine.getTEClass().isInstance(be),
                    machine + " must create " + machine.getTEClass().getSimpleName());
            helper.assertTrue(be.getType().isValid(helper.getLevel().getBlockState(helper.absolutePos(relative))),
                    machine + " BlockEntityType must accept its registered block state");
            helper.assertTrue(ElectriTiles.getTE(helper.getLevel(), helper.absolutePos(relative)) == machine,
                    machine + " must round-trip from its placed block");
        }

        helper.runAfterDelay(20, () -> {
            for (int i = 0; i < machines.length; i++) {
                BlockPos relative = new BlockPos(1 + (i % 5) * 2, 1, 1 + (i / 5) * 2);
                helper.assertTrue(helper.getLevel().getBlockEntity(helper.absolutePos(relative)) != null,
                        machines[i] + " must survive its first server tick");
            }
            helper.succeed();
        });
    }

    /** Every obtainable machine item must resolve to the machine it actually places. */
    private static void machineRegistryRoundtrip(GameTestHelper helper) {
        for (ElectriTiles machine : ElectriTiles.values()) {
            ItemStack stack;
            if (machine == ElectriTiles.BATTERY) {
                stack = BatteryType.REDSTONE.getCraftedProduct();
            } else if (machine == ElectriTiles.RFBATTERY) {
                stack = new ItemStack(ElectriItems.RFBATTERY.get());
            } else if (machine == ElectriTiles.WIRE) {
                stack = new ItemStack(ElectriBlocks.INSULATED_SILVER_WIRE.get());
            } else {
                stack = new ItemStack(machine.getBlock().asItem());
            }
            helper.assertTrue(!stack.isEmpty(), machine + " must have an obtainable item form");
            helper.assertTrue(ElectriTiles.getMachine(stack) == machine,
                    stack.getItem() + " must map back to " + machine);
        }
        helper.succeed();
    }

    /** Battery tiers and fuse ratings are concrete block/item identities, with no metadata aliases. */
    private static void batteryFuseIdentities(GameTestHelper helper) {
        HashSet<Block> batteryBlocks = new HashSet<>();
        HashSet<net.minecraft.world.item.Item> batteryItems = new HashSet<>();
        List<ItemStack> creativeBatteries = ElectriTabs.createBatteryVariants();
        helper.assertTrue(creativeBatteries.size() == BatteryType.batteryList.length * 2,
                "creative inventory must expose empty and filled stacks for every battery tier");

        for (int i = 0; i < BatteryType.batteryList.length; i++) {
            BatteryType tier = BatteryType.batteryList[i];
            Block block = ElectriBlocks.getBatteryBlock(tier).get();
            helper.assertTrue(batteryBlocks.add(block) && batteryItems.add(block.asItem()),
                    tier + " battery must have a unique block and BlockItem");
            BlockPos pos = new BlockPos(2 + i * 2, 1, 2);
            helper.setBlock(pos, block);
            BlockEntityBattery battery = helper.getBlockEntity(pos, BlockEntityBattery.class);
            helper.assertTrue(battery.getBatteryType() == tier,
                    tier + " battery block identity must determine its immutable tier");

            ItemStack empty = creativeBatteries.get(i * 2);
            ItemStack full = creativeBatteries.get(i * 2 + 1);
            helper.assertTrue(empty.is(block.asItem()) && full.is(block.asItem()),
                    tier + " creative variants must use that tier's concrete item");
            helper.assertTrue((!ReikaItemHelper.hasStackTag(empty)
                            || ReikaItemHelper.getStackTag(empty).getLongOr("nrg", 0) == 0)
                            && ReikaItemHelper.getStackTag(full).getLongOr("nrg", -1) == tier.maxCapacity,
                    tier + " creative variants must contain exactly empty and full energy states");
        }

        HashSet<Block> fuseBlocks = new HashSet<>();
        for (int i = 0; i < BlockEntityFuse.TIERS.length; i++) {
            int limit = BlockEntityFuse.TIERS[i];
            Block block = ElectriBlocks.getFuseBlock(limit).get();
            helper.assertTrue(fuseBlocks.add(block), limit + " A fuse must have a unique block identity");
            BlockPos pos = new BlockPos(2 + i * 2, 1, 5);
            helper.setBlock(pos, block);
            helper.assertTrue(helper.getBlockEntity(pos, BlockEntityFuse.class).getMaxCurrent() == limit,
                    limit + " A fuse block identity must determine its immutable rating");
            helper.assertTrue(BuiltInRegistries.BLOCK.getKey(block).getPath().equals("fuse_" + limit + "a"),
                    limit + " A fuse must have a descriptive registry name");
        }
        helper.succeed();
    }

    /** The internal transformer winding axis must follow every horizontal blockstate orientation. */
    private static void transformerOrientation(GameTestHelper helper) {
        Direction[] facings = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (int i = 0; i < facings.length; i++) {
            Direction stateFacing = facings[i];
            BlockPos relative = new BlockPos(2 + i * 3, 1, 3);
            BlockState state = ElectriBlocks.TRANSFORMER.get().defaultBlockState()
                    .setValue(BlockElectricMachine.FACING, stateFacing);
            BlockPos absolute = helper.absolutePos(relative);
            helper.getLevel().setBlock(absolute, state, 3);
            state.getBlock().setPlacedBy(helper.getLevel(), absolute, state,
                    helper.makeMockPlayer(GameType.SURVIVAL), new ItemStack(state.getBlock()));

            BlockEntityTransformer transformer = helper.getBlockEntity(relative, BlockEntityTransformer.class);
            Direction expected = stateFacing.getOpposite().getClockWise();
            helper.assertTrue(transformer.getFacing() == expected,
                    "transformer " + stateFacing + " placement must connect on " + expected.getAxis() + " axis");
            helper.assertTrue(transformer.canNetworkOnSide(expected)
                            && transformer.canNetworkOnSide(expected.getOpposite())
                            && !transformer.canNetworkOnSide(expected.getClockWise()),
                    "transformer " + stateFacing + " must connect only across its winding axis");
        }
        helper.succeed();
    }

    /** Real Rotary shaft output must convert and traverse an ElectriCraft network into a battery. */
    private static void mechanicalToBatteryPower(GameTestHelper helper) {
        BlockPos coilPos = new BlockPos(2, 1, 4);
        BlockPos generatorPos = coilPos.east();
        BlockPos wirePos = generatorPos.east();
        BlockPos batteryPos = wirePos.east();

        helper.setBlock(coilPos, RotaryBlocks.CREATIVE_COIL.get().defaultBlockState()
                .setValue(BlockRotaryCraftMachine.FACING, Direction.EAST));
        helper.setBlock(coilPos.above(), Blocks.REDSTONE_BLOCK);
        BlockEntityCreativeCoil coil = helper.getBlockEntity(coilPos, BlockEntityCreativeCoil.class);
        coil.setReleaseTorque(32);
        coil.setReleaseOmega(64);

        BlockState generatorState = ElectriBlocks.GENERATOR.get().defaultBlockState()
                .setValue(BlockElectricMachine.FACING, Direction.EAST);
        BlockPos generatorAbs = helper.absolutePos(generatorPos);
        helper.getLevel().setBlock(generatorAbs, generatorState, 3);
        generatorState.getBlock().setPlacedBy(helper.getLevel(), generatorAbs, generatorState,
                helper.makeMockPlayer(GameType.SURVIVAL), new ItemStack(generatorState.getBlock()));
        helper.setBlock(wirePos, ElectriBlocks.COPPER_WIRE.get());
        helper.setBlock(batteryPos, ElectriBlocks.REDSTONE_BATTERY.get());

        helper.runAfterDelay(60, () -> {
            BlockEntityGenerator generator = helper.getBlockEntity(generatorPos, BlockEntityGenerator.class);
            BlockEntityBattery battery = helper.getBlockEntity(batteryPos, BlockEntityBattery.class);
            helper.assertTrue(generator.getFacing() == Direction.WEST,
                    "induction generator must read the creative coil on its west shaft face");
            helper.assertTrue(generator.getPower() > 0 && generator.getOmega() == 64 && generator.getTorque() == 32,
                    "induction generator must receive the configured RotaryCraft shaft power");
            helper.assertTrue(generator.getNetwork() != null && generator.getNetwork().getNumberPaths() > 0,
                    "generator, wire, and battery must form a routed electrical network; generator="
                            + generator.getNetwork() + ", wire="
                            + helper.getBlockEntity(wirePos, BlockEntityWire.class).getNetwork()
                            + ", battery=" + battery.getNetwork());
            helper.assertTrue(battery.getStoredEnergy() > 0,
                    "electrical power must flow through the wire and charge the battery");
            helper.succeed();
        });
    }

    /** Modern per-material wire identities must retain their electrical variant and connectivity. */
    private static void wireVariantsConnect(GameTestHelper helper) {
        BlockPos copperPos = new BlockPos(3, 1, 4);
        BlockPos silverPos = copperPos.east();
        helper.setBlock(copperPos, ElectriBlocks.COPPER_WIRE.get());
        helper.setBlock(silverPos, ElectriBlocks.INSULATED_SILVER_WIRE.get());

        BlockEntityWire copper = helper.getBlockEntity(copperPos, BlockEntityWire.class);
        BlockEntityWire silver = helper.getBlockEntity(silverPos, BlockEntityWire.class);
        helper.assertTrue(copper.getWireType() == WireType.COPPER && !copper.insulated,
                "bare copper wire must derive its variant from its block identity");
        helper.assertTrue(silver.getWireType() == WireType.SILVER && silver.insulated,
                "insulated silver wire must derive its variant from its block identity");

        BlockPos copperAbs = helper.absolutePos(copperPos);
        BlockPos silverAbs = helper.absolutePos(silverPos);
        helper.assertTrue(copper.isConnectedOnSideAt(helper.getLevel(), copperAbs.getX(), copperAbs.getY(), copperAbs.getZ(), Direction.EAST),
                "copper wire must connect to the different adjacent wire variant");
        helper.assertTrue(silver.isConnectedOnSideAt(helper.getLevel(), silverAbs.getX(), silverAbs.getY(), silverAbs.getZ(), Direction.WEST),
                "wire connectivity must be reciprocal");

        helper.runAfterDelay(10, () -> {
            helper.assertTrue(helper.getLevel().getBlockEntity(copperAbs) instanceof BlockEntityWire,
                    "connected wire must survive network discovery");
            helper.assertTrue(helper.getLevel().getBlockEntity(silverAbs) instanceof BlockEntityWire,
                    "connected wire must survive network discovery");
            helper.succeed();
        });
    }

    /** Guards the survival interaction bounds that used to regress to invisible full cubes. */
    private static void wireAndMachineShapes(GameTestHelper helper) {
        BlockPos barePos = new BlockPos(2, 1, 2);
        BlockPos insulatedPos = new BlockPos(5, 1, 2);
        helper.setBlock(barePos, ElectriBlocks.COPPER_WIRE.get());
        helper.setBlock(insulatedPos, ElectriBlocks.INSULATED_COPPER_WIRE.get());

        AABB bare = shapeBounds(helper, barePos);
        AABB insulated = shapeBounds(helper, insulatedPos);
        assertNear(helper, bare.getXsize(), .25, "isolated bare wire width");
        assertNear(helper, insulated.getXsize(), 1D / 3D, "isolated insulated wire width");

        helper.setBlock(barePos.east(), ElectriBlocks.COPPER_WIRE.get());
        BlockPos bareAbs = helper.absolutePos(barePos);
        BlockEntityWire bareWire = helper.getBlockEntity(barePos, BlockEntityWire.class);
        bareWire.recomputeConnections(helper.getLevel(), bareAbs);
        AABB connected = shapeBounds(helper, barePos);
        assertNear(helper, connected.maxX, 1, "east-connected wire must reach the east block face");
        helper.assertTrue(connected.getZsize() < .3,
                "one-axis wire connection must not inflate collision into a broad envelope");

        BlockPos resistorPos = new BlockPos(2, 1, 5);
        BlockPos relayPos = new BlockPos(5, 1, 5);
        BlockPos fusePos = new BlockPos(8, 1, 5);
        BlockPos transformerPos = new BlockPos(11, 1, 5);
        helper.setBlock(resistorPos, ElectriBlocks.RESISTOR.get());
        helper.setBlock(relayPos, ElectriBlocks.RELAY.get());
        helper.setBlock(fusePos, ElectriBlocks.FUSE_32A.get());
        helper.setBlock(transformerPos, ElectriBlocks.TRANSFORMER.get());
        assertNear(helper, shapeBounds(helper, resistorPos).maxY, .75, "resistor height");
        assertNear(helper, shapeBounds(helper, relayPos).getZsize(), .75, "relay width");
        assertNear(helper, shapeBounds(helper, fusePos).maxY, .625, "fuse height");
        helper.assertTrue(shapeBounds(helper, transformerPos).getZsize() < .5,
                "transformer collision must follow its narrow winding axis");
        helper.succeed();
    }

    private static AABB shapeBounds(GameTestHelper helper, BlockPos relative) {
        BlockPos absolute = helper.absolutePos(relative);
        var state = helper.getLevel().getBlockState(absolute);
        var outline = state.getShape(helper.getLevel(), absolute);
        var collision = state.getCollisionShape(helper.getLevel(), absolute);
        helper.assertTrue(!outline.isEmpty() && !collision.isEmpty(), state.getBlock() + " must have selectable collision");
        helper.assertTrue(outline.bounds().equals(collision.bounds()),
                state.getBlock() + " selection and collision envelopes must agree");
        return outline.bounds();
    }

    private static void assertNear(GameTestHelper helper, double actual, double expected, String message) {
        helper.assertTrue(Math.abs(actual - expected) < 1.0E-5,
                message + ": expected " + expected + ", got " + actual);
    }

    /** Stateful battery placement must preserve tier/energy and consume one item in survival. */
    private static void batterySurvivalPlacement(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.snapTo(Vec3.atCenterOf(helper.absolutePos(new BlockPos(1, 2, 1))));

        ItemStack stack = new ItemStack(ElectriBlocks.DIAMOND_BATTERY.get(), 2);
        ReikaItemHelper.updateStackTag(stack, tag -> {
            tag.putLong("nrg", 12_345L);
        });
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);

        BlockPos floor = helper.absolutePos(new BlockPos(5, 0, 5));
        BlockHitResult hit = new BlockHitResult(
                new Vec3(floor.getX() + 0.5, floor.getY() + 1, floor.getZ() + 0.5),
                Direction.UP, floor, false);
        InteractionResult result = stack.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));

        helper.assertTrue(result.consumesAction(), "battery item must accept a valid floor placement");
        BlockPos placed = floor.above();
        helper.assertTrue(helper.getLevel().getBlockState(placed).is(ElectriBlocks.DIAMOND_BATTERY.get()),
                "battery placer must put the battery above the clicked floor");
        helper.assertTrue(helper.getLevel().getBlockEntity(placed) instanceof BlockEntityBattery battery,
                "placed battery must create its server block entity");
        BlockEntityBattery battery = (BlockEntityBattery)helper.getLevel().getBlockEntity(placed);
        helper.assertTrue(battery.getBatteryType() == BatteryType.DIAMOND,
                "placed battery must preserve its item tier");
        helper.assertTrue(battery.getStoredEnergy() == 12_345L,
                "placed battery must preserve its stored energy");
        helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).getCount() == 1,
                "survival placement must consume exactly one battery item");
        helper.succeed();
    }

    /** NeoForge FE access must enforce the legacy receive-on-sides / emit-on-top contract. */
    private static void rfBatterySidedEnergy(GameTestHelper helper) {
        BlockPos relative = new BlockPos(5, 1, 5);
        helper.setBlock(relative, ElectriBlocks.RFBATTERY.get());
        BlockPos pos = helper.absolutePos(relative);

        EnergyHandler side = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, pos, Direction.NORTH);
        EnergyHandler top = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, pos, Direction.UP);
        helper.assertTrue(side != null && top != null, "RF battery must expose its standard block energy capability");

        try (Transaction tx = Transaction.openRoot()) {
            helper.assertTrue(side.insert(1_000, tx) == 1_000, "a side face must accept FE");
            tx.commit();
        }
        try (Transaction tx = Transaction.openRoot()) {
            helper.assertTrue(top.insert(1_000, tx) == 0, "the top face must reject FE insertion");
        }
        try (Transaction tx = Transaction.openRoot()) {
            helper.assertTrue(side.extract(250, tx) == 0, "a side face must reject FE extraction");
        }
        try (Transaction tx = Transaction.openRoot()) {
            helper.assertTrue(top.extract(250, tx) == 250, "the top face must provide FE extraction");
            tx.commit();
        }

        BlockEntityRFBattery battery = helper.getBlockEntity(relative, BlockEntityRFBattery.class);
        helper.assertTrue(battery.getStoredEnergy() == 750L, "committed FE transactions must update stored energy");
        helper.succeed();
    }

    /** A small progression snapshot guards the recipes needed to enter and use the power system. */
    private static void survivalRecipeCatalog(GameTestHelper helper) {
        String[] required = {
                "wire_copper", "wire_copper_insulated", "battery_redstone", "battery_diamond",
                "generator", "motor", "resistor", "relay", "meter", "transformer",
                "precise_resistor", "fuse_coal", "rf_cable", "rf_battery", "charge_pad_basic"
        };
        for (String path : required) {
            ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE,
                    Identifier.fromNamespaceAndPath(ElectriCraft.MODID, path));
            helper.assertTrue(helper.getLevel().getServer().getRecipeManager().byKey(key).isPresent(),
                    "survival recipe electricraft:" + path + " must be loaded");
        }
        helper.succeed();
    }

    /** Batteries, configurable machines, and charge pads must survive a break/place cycle. */
    private static void statefulBlockDrops(GameTestHelper helper) {
        BlockPos batteryPos = new BlockPos(2, 1, 2);
        helper.setBlock(batteryPos, ElectriBlocks.STAR_BATTERY.get());
        BlockEntityBattery battery = helper.getBlockEntity(batteryPos, BlockEntityBattery.class);
        ItemStack charged = new ItemStack(ElectriBlocks.STAR_BATTERY.get());
        ReikaItemHelper.updateStackTag(charged, tag -> tag.putLong("nrg", 987_654L));
        battery.setEnergyFromNBT(charged);
        ItemStack batteryDrop = onlyDrop(helper, batteryPos);
        helper.assertTrue(batteryDrop.is(ElectriBlocks.STAR_BATTERY.get().asItem()),
                "battery must drop its concrete tier item");
        helper.assertTrue(ReikaItemHelper.getStackTag(batteryDrop).getLongOr("nrg", -1) == 987_654L,
                "battery drop must preserve tier and energy");

        BlockPos rfPos = new BlockPos(4, 1, 2);
        helper.setBlock(rfPos, ElectriBlocks.RFBATTERY.get());
        BlockEntityRFBattery rfBattery = helper.getBlockEntity(rfPos, BlockEntityRFBattery.class);
        ItemStack rfCharged = new ItemStack(ElectriItems.RFBATTERY.get());
        ReikaItemHelper.updateStackTag(rfCharged, tag -> tag.putLong("nrg", 456_789L));
        rfBattery.setEnergyFromNBT(rfCharged);
        ItemStack rfDrop = onlyDrop(helper, rfPos);
        helper.assertTrue(rfDrop.is(ElectriItems.RFBATTERY.get())
                        && ReikaItemHelper.getStackTag(rfDrop).getLongOr("nrg", -1) == 456_789L,
                "RF battery drop must preserve its stored FE");

        BlockPos fusePos = new BlockPos(6, 1, 2);
        helper.setBlock(fusePos, ElectriBlocks.FUSE_1024A.get());
        ItemStack fuseDrop = onlyDrop(helper, fusePos);
        helper.assertTrue(fuseDrop.is(ElectriBlocks.FUSE_1024A.get().asItem()),
                "fuse drop must preserve its concrete amperage identity");

        BlockPos padPos = new BlockPos(8, 1, 2);
        helper.setBlock(padPos, ElectriBlocks.WIRELESS_CHARGER.get());
        BlockEntityWirelessCharger pad = helper.getBlockEntity(padPos, BlockEntityWirelessCharger.class);
        pad.setTier(BlockEntityWirelessCharger.ChargerTiers.ADVANCED.ordinal());
        ItemStack padDrop = onlyDrop(helper, padPos);
        helper.assertTrue(padDrop.is(ElectriBlocks.WIRELESS_CHARGER.get().asItem())
                        && ReikaItemHelper.getStackTag(padDrop).getIntOr("tier", -1)
                        == BlockEntityWirelessCharger.ChargerTiers.ADVANCED.ordinal(),
                "charge pad drop must preserve its tier");

        BlockPos replaced = new BlockPos(8, 1, 5);
        helper.setBlock(replaced, ElectriBlocks.WIRELESS_CHARGER.get());
        BlockPos replacedAbs = helper.absolutePos(replaced);
        restorePlacedState(helper, replacedAbs, padDrop);
        helper.assertTrue(helper.getLevel().getBlockEntity(replacedAbs) instanceof BlockEntityWirelessCharger replacement
                        && replacement.getTier() == BlockEntityWirelessCharger.ChargerTiers.ADVANCED,
                "charge pad must restore its tier when placed from the recovered item");
        helper.succeed();
    }

    /** Exercises vanilla's post-removal playerDestroy order, where world lookups already return air. */
    private static void machineSurvivalBreak(GameTestHelper helper) {
        BlockPos relative = new BlockPos(5, 1, 5);
        helper.setBlock(relative, ElectriBlocks.GENERATOR.get());
        BlockPos pos = helper.absolutePos(relative);
        var state = helper.getLevel().getBlockState(pos);
        BlockEntity blockEntity = helper.getLevel().getBlockEntity(pos);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack tool = new ItemStack(Items.IRON_PICKAXE);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);

        helper.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        state.getBlock().playerDestroy(helper.getLevel(), player, pos, state, blockEntity, tool);
        int drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2)).stream()
                .filter(entity -> entity.getItem().is(ElectriBlocks.GENERATOR.get().asItem()))
                .mapToInt(entity -> entity.getItem().getCount()).sum();
        helper.assertTrue(drops == 1, "a survival-mined generator must drop exactly one generator item");
        helper.succeed();
    }

    private static ItemStack onlyDrop(GameTestHelper helper, BlockPos relative) {
        BlockPos pos = helper.absolutePos(relative);
        var state = helper.getLevel().getBlockState(pos);
        List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), pos, helper.getLevel().getBlockEntity(pos));
        helper.assertTrue(drops.size() == 1, state.getBlock() + " must produce exactly one recoverable drop");
        return drops.getFirst();
    }

    /** Keeps setPlacedBy in the test while avoiding BlockItem's player-facing placement geometry. */
    private static void restorePlacedState(GameTestHelper helper, BlockPos pos, ItemStack stack) {
        var state = helper.getLevel().getBlockState(pos);
        state.getBlock().setPlacedBy(helper.getLevel(), pos, state,
                helper.makeMockPlayer(GameType.SURVIVAL), stack);
    }

    private static void register(RegisterGameTestsEvent event, Holder<TestEnvironmentDefinition<?>> env,
                                 String name, int maxTicks, Consumer<GameTestHelper> body) {
        Identifier id = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, name);
        TestData<Holder<TestEnvironmentDefinition<?>>> data =
                new TestData<>(env, ElectriTestStructureProvider.ARENA, maxTicks, 0, true, Rotation.NONE);
        event.registerTest(id, new DirectInstance(data, body));
    }

    private static final class DirectInstance extends GameTestInstance {
        static final MapCodec<DirectInstance> CODEC =
                TestData.CODEC.xmap(data -> new DirectInstance(data, helper -> {}), instance -> instance.info);

        private final TestData<Holder<TestEnvironmentDefinition<?>>> info;
        private final Consumer<GameTestHelper> body;

        DirectInstance(TestData<Holder<TestEnvironmentDefinition<?>>> info, Consumer<GameTestHelper> body) {
            super(info);
            this.info = info;
            this.body = body;
        }

        @Override
        public void run(GameTestHelper helper) {
            body.accept(helper);
        }

        @Override
        public MapCodec<? extends GameTestInstance> codec() {
            return CODEC;
        }

        @Override
        protected MutableComponent typeDescription() {
            return Component.literal("electricraft direct test");
        }
    }
}
