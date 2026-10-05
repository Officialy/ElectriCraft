package reika.electricraft.renders.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import reika.electricraft.ElectriCraft;
import reika.electricraft.renders.RenderWire;

import java.util.function.Consumer;

/** Inventory form of the V31a RF cable: an isolated quarter-block conductor core. */
public final class ElectriCableItemRenderer implements NoDataSpecialModelRenderer {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "rf_cable");
    private static final Identifier CENTRE_TEXTURE = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "textures/blocks/rf.png");

    private ElectriCableItemRenderer() { }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay,
                       boolean hasFoil, int outlineColor) {
        PoseStack snapshot = new PoseStack();
        snapshot.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(CENTRE_TEXTURE),
                (ignored, vertices) -> RenderWire.emitBox(snapshot.last(), vertices,
                        .375F, .375F, .375F, .625F, .625F, .625F, light));
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        for (float x : new float[]{.375F, .625F})
            for (float y : new float[]{.375F, .625F})
                for (float z : new float[]{.375F, .625F})
                    output.accept(new Vector3f(x, y, z));
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<? extends NoDataSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ElectriCableItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            // Models bake before the live atlases upload during the initial resource reload.
            // The original conductor uses full 0..1 UVs, like ElectriWireItemRenderer.
            return new ElectriCableItemRenderer();
        }
    }
}
