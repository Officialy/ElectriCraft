package reika.electricraft.renders.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import reika.electricraft.ElectriCraft;
import reika.electricraft.registry.WireType;
import reika.electricraft.renders.RenderWire;

import java.util.Locale;
import java.util.function.Consumer;

/** Exact V31a three-segment inventory renderer shared by every wire material. */
public final class ElectriWireItemRenderer implements NoDataSpecialModelRenderer {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "wire");

    private final WireType type;
    private final boolean insulated;

    private ElectriWireItemRenderer(WireType type, boolean insulated) {
        this.type = type;
        this.insulated = insulated;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay,
                       boolean hasFoil, int outlineColor) {
        PoseStack snapshot = new PoseStack();
        snapshot.last().set(poseStack.last());

        collector.submitCustomGeometry(poseStack,
                RenderTypes.entityCutout(RenderWire.texture(type, insulated, false)),
                (ignored, vertices) -> RenderWire.emitBox(snapshot.last(), vertices,
                        .3F, .3F, .3F, .7F, .7F, .7F, light));
        collector.submitCustomGeometry(poseStack,
                RenderTypes.entityCutout(RenderWire.texture(type, insulated, true)),
                (ignored, vertices) -> {
                    RenderWire.emitBox(snapshot.last(), vertices,
                            .3F, -.1F, .3F, .7F, .3F, .7F, light);
                    RenderWire.emitBox(snapshot.last(), vertices,
                            .3F, .7F, .3F, .7F, 1.1F, .7F, light);
                });
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        for (float x : new float[]{.3F, .7F})
            for (float y : new float[]{-.1F, 1.1F})
                for (float z : new float[]{.3F, .7F})
                    output.accept(new Vector3f(x, y, z));
    }

    public record Unbaked(String material, boolean insulated) implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("material").forGetter(Unbaked::material),
                Codec.BOOL.fieldOf("insulated").forGetter(Unbaked::insulated)
        ).apply(instance, Unbaked::new));

        @Override
        public MapCodec<? extends NoDataSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ElectriWireItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            try {
                return new ElectriWireItemRenderer(WireType.valueOf(material.toUpperCase(Locale.ROOT)), insulated);
            } catch (IllegalArgumentException ex) {
                ElectriCraft.LOGGER.warn("Unknown ElectriCraft wire material '{}'", material);
                return null;
            }
        }
    }
}
