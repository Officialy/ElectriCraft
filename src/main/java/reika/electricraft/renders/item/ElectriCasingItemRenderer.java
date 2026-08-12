package reika.electricraft.renders.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;
import reika.electricraft.registry.BatteryType;
import reika.electricraft.renders.LegacyCubeRenderer;
import reika.electricraft.renders.RenderModBattery;

import java.util.Locale;
import java.util.function.Consumer;

/** Stateful special item models for the legacy battery and wireless-charger cubes. */
public final class ElectriCasingItemRenderer implements SpecialModelRenderer<DataComponentMap> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "casing");

    private final Kind kind;
    private final BatteryType batteryType;

    private ElectriCasingItemRenderer(Kind kind, BatteryType batteryType) {
        this.kind = kind;
        this.batteryType = batteryType;
    }

    @Override
    public @Nullable DataComponentMap extractArgument(ItemStack stack) {
        return stack.immutableComponents();
    }

    @Override
    public void submit(@Nullable DataComponentMap components, PoseStack poseStack,
                       SubmitNodeCollector collector, int light, int overlay,
                       boolean hasFoil, int outlineColor) {
        CompoundTag tag = customTag(components);
        switch (kind) {
            case BATTERY -> submitBattery(batteryType, poseStack, collector, light);
            case RF_BATTERY -> submitRfBattery(tag, poseStack, collector, light);
            case WIRELESS_CHARGER -> submitWireless(tag, poseStack, collector, light);
        }
    }

    private static void submitBattery(BatteryType type, PoseStack stack,
                                      SubmitNodeCollector collector, int light) {
        String tier = type.name().toLowerCase(Locale.ROOT);
        Identifier side = batteryTexture(tier);
        submitFace(stack, collector, Direction.DOWN, batteryTexture("_bottom"), light);
        submitFace(stack, collector, Direction.UP, batteryTexture("_top"), light);
        for (Direction face : Direction.Plane.HORIZONTAL)
            submitFace(stack, collector, face, side, light);

        // V31a's BatteryRenderer inventory path always drew this layer with ordinary item light;
        // stored-energy full-bright selection existed only in the placed-block renderer.
        Identifier glow = batteryTexture(tier + "_glow");
        for (Direction face : Direction.Plane.HORIZONTAL)
            LegacyCubeRenderer.submitFace(stack, collector, RenderTypes.entityTranslucent(glow),
                    face, light, 0xFFFFFFFF);
    }

    private static void submitRfBattery(CompoundTag tag, PoseStack stack,
                                        SubmitNodeCollector collector, int light) {
        submitFace(stack, collector, Direction.DOWN, batteryTexture("_bottom"), light);
        submitFace(stack, collector, Direction.UP, batteryTexture("_top"), light);
        Identifier side = batteryTexture("rf");
        for (Direction face : Direction.Plane.HORIZONTAL)
            submitFace(stack, collector, face, side, light);
        RenderModBattery.submitBands(stack, collector, tag.getLongOr("nrg", 0L),
                BlockEntityRFBattery.CAPACITY, 0xFF1111, light);
    }

    private static void submitWireless(CompoundTag tag, PoseStack stack,
                                       SubmitNodeCollector collector, int light) {
        int tier = Math.floorMod(tag.getIntOr("tier", 0),
                BlockEntityWirelessCharger.ChargerTiers.tierList.length);
        submitFace(stack, collector, Direction.DOWN, wirelessTexture("back"), light);
        submitFace(stack, collector, Direction.UP, wirelessTexture("front"), light);
        Identifier side = wirelessTexture("side_" + tier);
        for (Direction face : Direction.Plane.HORIZONTAL)
            submitFace(stack, collector, face, side, light);
    }

    private static void submitFace(PoseStack stack, SubmitNodeCollector collector,
                                   Direction face, Identifier texture, int light) {
        LegacyCubeRenderer.submitFace(stack, collector, RenderTypes.entityCutout(texture),
                face, light, 0xFFFFFFFF);
    }

    private static CompoundTag customTag(@Nullable DataComponentMap components) {
        if (components == null)
            return new CompoundTag();
        CustomData data = components.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : new CompoundTag();
    }

    private static Identifier batteryTexture(String name) {
        return Identifier.fromNamespaceAndPath(ElectriCraft.MODID,
                "textures/blocks/battery/" + name + ".png");
    }

    private static Identifier wirelessTexture(String name) {
        return Identifier.fromNamespaceAndPath(ElectriCraft.MODID,
                "textures/blocks/wireless/" + name + ".png");
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        for (int x = 0; x <= 1; x++)
            for (int y = 0; y <= 1; y++)
                for (int z = 0; z <= 1; z++)
                    output.accept(new Vector3f(x, y, z));
    }

    public record Unbaked(String casing, String variant) implements SpecialModelRenderer.Unbaked<DataComponentMap> {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("casing").forGetter(Unbaked::casing),
                Codec.STRING.optionalFieldOf("variant", "").forGetter(Unbaked::variant)
        ).apply(instance, Unbaked::new));

        public Unbaked(String casing) {
            this(casing, "");
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<DataComponentMap>> type() {
            return MAP_CODEC;
        }

        @Override
        public ElectriCasingItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            try {
                Kind kind = Kind.valueOf(casing.toUpperCase(Locale.ROOT));
                BatteryType type = kind == Kind.BATTERY
                        ? BatteryType.valueOf(variant.toUpperCase(Locale.ROOT))
                        : BatteryType.REDSTONE;
                return new ElectriCasingItemRenderer(kind, type);
            } catch (IllegalArgumentException ex) {
                ElectriCraft.LOGGER.warn("Unknown ElectriCraft casing item model '{}'", casing);
                return null;
            }
        }
    }

    public enum Kind {
        BATTERY,
        RF_BATTERY,
        WIRELESS_CHARGER
    }
}
