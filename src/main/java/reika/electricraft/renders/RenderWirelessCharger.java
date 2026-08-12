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
import net.minecraft.world.phys.Vec3;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;

/** Runtime six-face model for the V31a front/back/tier wireless-charger casing. */
public final class RenderWirelessCharger extends ElectriTERenderer<BlockEntityWirelessCharger> {

    public RenderWirelessCharger(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BlockEntityWirelessCharger charger, BlockEntityRenderState renderState,
                                   float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(charger, renderState, partialTicks, cameraPosition, breakProgress);
        State state = (State)renderState;
        state.facing = charger.getFacing();
        state.tier = charger.getTier().ordinal();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        State state = (State)renderState;
        Direction front = state.facing != null ? state.facing : Direction.UP;
        Direction back = front.getOpposite();
        for (Direction face : Direction.values()) {
            String name = face == front ? "front" : face == back ? "back" : "side_" + state.tier;
            Identifier texture = Identifier.fromNamespaceAndPath(ElectriCraft.MODID,
                    "textures/blocks/wireless/" + name + ".png");
            LegacyCubeRenderer.submitFace(poseStack, collector, RenderTypes.entityCutout(texture),
                    face, state.lightCoords, 0xFFFFFFFF);
        }
    }

    public static final class State extends BlockEntityRenderState {
        private Direction facing = Direction.UP;
        private int tier;
    }
}
