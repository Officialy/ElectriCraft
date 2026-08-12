package reika.electricraft.renders.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;
import reika.electricraft.ElectriCraft;
import reika.electricraft.renders.model.FuseModel;
import reika.electricraft.renders.model.MeterModel;
import reika.electricraft.renders.model.PreciseResistorModel;
import reika.electricraft.renders.model.RelayModel;
import reika.electricraft.renders.model.ResistorModel;
import reika.electricraft.renders.model.TransformerModel;
import reika.electricraft.registry.ElectriModelLayers;
import reika.rotarycraft.modinterface.model.ElecMotorModel;
import reika.rotarycraft.modinterface.model.GeneratorModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Modern inventory renderer for the eight legacy Techne-machine BlockItems.
 *
 * <p>V31a routed these items through their tile renderers. A baked cube therefore is not a
 * faithful fallback: it hides every original mesh. The 26.2 special-model pipeline is the direct
 * replacement and lets the item share the exact model layer and unmodified V31a texture with its
 * block-entity renderer.</p>
 */
public final class ElectriMachineItemRenderer implements NoDataSpecialModelRenderer {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "machine");

    private final Kind kind;
    private final Model<?> model;
    private final Identifier texture;

    private ElectriMachineItemRenderer(Kind kind, Model<?> model, Identifier texture) {
        this.kind = kind;
        this.model = model;
        this.texture = texture;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay,
                       boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(.5D, 1.5D, .5D);
        poseStack.scale(-1F, -1F, 1F);
        poseStack.mulPose(Axis.YN.rotationDegrees(90F));

        PoseStack snapshot = new PoseStack();
        snapshot.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack,
                kind == Kind.FUSE ? RenderTypes.entityCutoutCull(texture) : RenderTypes.entityCutout(texture),
                (ignored, vertices) -> render(snapshot, vertices, light, overlay));
        poseStack.popPose();
    }

    private void render(PoseStack poseStack, VertexConsumer vertices, int light, int overlay) {
        switch (kind) {
            case GENERATOR -> model.renderToBuffer(poseStack, vertices, light, overlay, 0xFFFFFFFF);
            case MOTOR -> {
                ArrayList<Object> conditions = new ArrayList<>();
                conditions.add(5);          // exact V31a inventory coil count
                conditions.add(0x515168);   // unpowered motor-fin colour
                conditions.add(false);      // fins are not full-bright while unpowered
                ((ElecMotorModel)model).renderAll(poseStack, vertices, light, null, conditions, 0, 0);
            }
            case METER, FUSE, RELAY -> model.renderToBuffer(poseStack, vertices, light, overlay, 0xFFFFFFFF);
            case RESISTOR -> ((ResistorModel)model).renderAll(
                    poseStack, vertices, light, null, blackBands(4), 0, 0);
            case PRECISE_RESISTOR -> ((PreciseResistorModel)model).renderAll(
                    poseStack, vertices, light, null, blackBands(5), 0, 0);
            case TRANSFORMER -> ((TransformerModel)model).renderAll(poseStack, vertices, light, 1, 1);
        }
    }

    private static ArrayList<Object> blackBands(int count) {
        ArrayList<Object> bands = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
            bands.add(reika.electricraft.base.BlockEntityResistorBase.ColorBand.BLACK);
        return bands;
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked(String machine) implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("machine").forGetter(Unbaked::machine)
        ).apply(instance, Unbaked::new));

        @Override
        public MapCodec<? extends NoDataSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ElectriMachineItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            final Kind kind;
            try {
                kind = Kind.valueOf(machine.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                ElectriCraft.LOGGER.warn("Unknown ElectriCraft special item model '{}'", machine);
                return null;
            }

            Model<?> model = switch (kind) {
                case GENERATOR -> new GeneratorModel(context.entityModelSet().bakeLayer(ElectriModelLayers.GENERATOR));
                case MOTOR -> new ElecMotorModel(context.entityModelSet().bakeLayer(ElectriModelLayers.MOTOR));
                case METER -> new MeterModel(context.entityModelSet().bakeLayer(ElectriModelLayers.METER));
                case PRECISE_RESISTOR -> new PreciseResistorModel(context.entityModelSet().bakeLayer(ElectriModelLayers.PRECISE_RESISTOR));
                case TRANSFORMER -> new TransformerModel(context.entityModelSet().bakeLayer(ElectriModelLayers.TRANSFORMER));
                case FUSE -> new FuseModel(context.entityModelSet().bakeLayer(ElectriModelLayers.FUSE));
                case RESISTOR -> new ResistorModel(context.entityModelSet().bakeLayer(ElectriModelLayers.RESISTOR));
                case RELAY -> new RelayModel(context.entityModelSet().bakeLayer(ElectriModelLayers.RELAY));
            };
            return new ElectriMachineItemRenderer(kind, model, kind.texture());
        }
    }

    public enum Kind {
        GENERATOR("generatortex"),
        MOTOR("elecmotortex"),
        METER("metertex"),
        PRECISE_RESISTOR("resistor"),
        TRANSFORMER("transformertex"),
        FUSE("fusetex"),
        RESISTOR("resistor"),
        RELAY("relay");

        private final String texture;

        Kind(String texture) {
            this.texture = texture;
        }

        private Identifier texture() {
            return Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "textures/" + texture + ".png");
        }
    }
}
