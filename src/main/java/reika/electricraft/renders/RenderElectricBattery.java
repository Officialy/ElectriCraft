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
import reika.electricraft.blockentities.BlockEntityBattery;
import reika.electricraft.registry.BatteryType;

/**
 * Exact V31a ElectriCraft battery cube presentation.
 *
 * <p>Battery material is a real runtime tier, so the source's bottom, top, tier side art and
 * charged-only glowing side overlay cannot be expressed by a single static model. The old port
 * replaced all of them with a fabricated cube texture; this BER is the modern equivalent of the
 * original {@code BatteryRenderer} and keeps the four glow faces full-bright only while charged.</p>
 */
public final class RenderElectricBattery extends ElectriTERenderer<BlockEntityBattery> {

    private static final int WHITE = 0xFFFFFFFF;
    private static final Identifier TOP = texture("_top");
    private static final Identifier BOTTOM = texture("_bottom");

    public RenderElectricBattery(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BlockEntityBattery battery, BlockEntityRenderState renderState,
                                   float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(battery, renderState, partialTicks, cameraPosition, breakProgress);
        State state = (State)renderState;
        state.type = battery.getBatteryType();
        state.charged = battery.getStoredEnergy() > 0;
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        State state = (State)renderState;
        if (state.type == null)
            return;
        int light = state.lightCoords;
        Identifier side = texture(state.type.name().toLowerCase(java.util.Locale.ROOT));
        LegacyCubeRenderer.submitFace(poseStack, collector, RenderTypes.entityCutout(BOTTOM),
                Direction.DOWN, light, WHITE);
        LegacyCubeRenderer.submitFace(poseStack, collector, RenderTypes.entityCutout(TOP),
                Direction.UP, light, WHITE);
        for (Direction face : Direction.Plane.HORIZONTAL)
            LegacyCubeRenderer.submitFace(poseStack, collector, RenderTypes.entityCutout(side),
                    face, light, WHITE);

        // V31a always drew the glow artwork. Charge selected its lightmap (240 when charged,
        // ordinary neighbouring light when empty); omitting the layer while empty loses the
        // coloured side detail baked into that sprite.
        Identifier glow = texture(state.type.name().toLowerCase(java.util.Locale.ROOT) + "_glow");
        for (Direction face : Direction.Plane.HORIZONTAL)
            LegacyCubeRenderer.submitFace(poseStack, collector,
                    state.charged ? RenderTypes.entityTranslucentEmissive(glow) : RenderTypes.entityTranslucent(glow),
                    face, state.charged ? 0xF000F0 : light, WHITE);
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "textures/blocks/battery/" + name + ".png");
    }

    public static final class State extends BlockEntityRenderState {
        private BatteryType type;
        private boolean charged;
    }
}
