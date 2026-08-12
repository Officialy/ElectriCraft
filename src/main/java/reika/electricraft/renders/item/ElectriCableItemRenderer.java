package reika.electricraft.renders.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import reika.electricraft.ElectriCraft;
import reika.electricraft.renders.RenderWire;

import java.util.function.Consumer;

/** Inventory form of the V31a RF cable: an isolated quarter-block conductor core. */
public final class ElectriCableItemRenderer implements NoDataSpecialModelRenderer {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "rf_cable");
    private static final Identifier CENTRE_ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/rf");

    private final TextureAtlasSprite centre;

    private ElectriCableItemRenderer(TextureAtlasSprite centre) {
        this.centre = centre;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay,
                       boolean hasFoil, int outlineColor) {
        PoseStack snapshot = new PoseStack();
        snapshot.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS),
                (ignored, vertices) -> RenderWire.emitBox(snapshot.last(), centre.wrap(vertices),
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
            TextureAtlasSprite sprite = context.sprites().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, CENTRE_ID));
            return new ElectriCableItemRenderer(sprite);
        }
    }
}
