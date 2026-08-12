package reika.electricraft.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.phys.Vec3;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriCable;
import reika.electricraft.base.ElectriTERenderer;

/**
 * V31a {@code CableRenderer} port for the RF cable. Like ordinary wires it is a centre conductor
 * with a short exact-texture arm for every live connection; a baked full cube would be incorrect.
 */
public final class RenderCable extends ElectriTERenderer<ElectriCable> {
    private static final Identifier CENTRE_ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/rf");
    private static final Identifier END_ID = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "blocks/rf_end");
    private final TextureAtlasSprite centre;
    private final TextureAtlasSprite end;

    public RenderCable(BlockEntityRendererProvider.Context context) {
        centre = context.sprites().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, CENTRE_ID));
        end = context.sprites().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, END_ID));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ElectriCable cable, BlockEntityRenderState renderState, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(cable, renderState, partialTicks, cameraPosition, breakProgress);
        State state = (State)renderState;
        for (Direction side : Direction.values()) {
            state.connected[side.ordinal()] = cable.getLevel() != null
                    && cable.isConnectedOnSideAt(cable.getLevel(), cable.getBlockPos(), side);
        }
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        State state = (State)renderState;
        PoseStack snapped = new PoseStack();
        snapped.last().set(poseStack.last());
        int light = state.lightCoords;
        float halfWidth = 1F / 8F;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS),
                (pose, vertices) -> RenderWire.emitBox(snapped.last(), centre.wrap(vertices),
                        .5F - halfWidth, .5F - halfWidth, .5F - halfWidth,
                        .5F + halfWidth, .5F + halfWidth, .5F + halfWidth, light));
        if (hasConnection(state.connected)) {
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS), (pose, vertices) -> {
                var wrapped = end.wrap(vertices);
                for (Direction side : Direction.values()) {
                    if (state.connected[side.ordinal()])
                        RenderWire.emitArm(snapped.last(), wrapped, side, halfWidth, false, light);
                }
            });
        }
    }

    private static boolean hasConnection(boolean[] connections) {
        for (boolean connection : connections) {
            if (connection)
                return true;
        }
        return false;
    }

    public static final class State extends BlockEntityRenderState {
        private final boolean[] connected = new boolean[Direction.values().length];
    }
}
