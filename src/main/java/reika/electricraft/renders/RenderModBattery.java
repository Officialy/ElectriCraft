package reika.electricraft.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;

/**
 * V31a mod-battery charge display. The fixed RF casing is a generated source-texture model; this
 * BER restores the four logarithmic, colour-darkening energy ladders that sit just above its sides.
 */
public final class RenderModBattery extends ElectriTERenderer<BlockEntityRFBattery> {

    private static final int BANDS = 64;

    public RenderModBattery(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BlockEntityRFBattery battery, BlockEntityRenderState renderState,
                                   float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(battery, renderState, partialTicks, cameraPosition, breakProgress);
        State state = (State)renderState;
        state.energy = battery.getStoredEnergy();
        state.capacity = battery.getMaxEnergy();
        state.color = battery.getEnergyColor();
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        State state = (State)renderState;
        submitBands(poseStack, collector, state.energy, state.capacity, state.color, state.lightCoords);
    }

    /** Shared by the placed battery and its 26.2 special item model. */
    public static void submitBands(PoseStack poseStack, SubmitNodeCollector collector,
                                   long energy, long capacity, int energyColor, int light) {
        if (energy <= 0 || capacity <= 0)
            return;
        // Exact legacy formula: a logarithmic 0..64 ladder, not a linear tank gauge.
        int count = (int)(BANDS * Math.log10(energy) / Math.log10(capacity));
        count = Math.max(0, Math.min(BANDS, count));
        if (count == 0)
            return;
        final int visibleBands = count;

        PoseStack snapped = new PoseStack();
        snapped.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, vertices) -> {
            PoseStack.Pose matrix = snapped.last();
            float inset = 1F / 16F;
            float halfWidth = 3F / 16F;
            float bandHeight = (1F - 2F * inset) / BANDS;
            int bright = 0xFF000000 | (energyColor & 0xFFFFFF);
            int dark = darken(bright, .05F);
            for (int i = 0; i < visibleBands; i++) {
                int color = mix(bright, dark, i / (float)BANDS);
                float y0 = inset + bandHeight * i;
                float y1 = y0 + bandHeight;
                // Legacy geometry used an o=0.0025 exterior offset and a 3/16-wide centred strip.
                float o = .0025F;
                quad(matrix, vertices, .5F-halfWidth, y1, -o, .5F+halfWidth, y1, -o, .5F+halfWidth, y0, -o, .5F-halfWidth, y0, -o, color, 0, 0, -1, light);
                quad(matrix, vertices, .5F-halfWidth, y0, 1+o, .5F+halfWidth, y0, 1+o, .5F+halfWidth, y1, 1+o, .5F-halfWidth, y1, 1+o, color, 0, 0, 1, light);
                quad(matrix, vertices, 1+o, y1, .5F-halfWidth, 1+o, y1, .5F+halfWidth, 1+o, y0, .5F+halfWidth, 1+o, y0, .5F-halfWidth, color, 1, 0, 0, light);
                quad(matrix, vertices, -o, y0, .5F-halfWidth, -o, y0, .5F+halfWidth, -o, y1, .5F+halfWidth, -o, y1, .5F-halfWidth, color, -1, 0, 0, light);
            }
        });
    }

    private static int darken(int color, float multiplier) {
        int r = (int)(((color >>> 16) & 255) * multiplier);
        int g = (int)(((color >>> 8) & 255) * multiplier);
        int b = (int)((color & 255) * multiplier);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    private static int mix(int first, int second, float progress) {
        float a = Math.max(0, Math.min(1, progress));
        int r = (int)(((first >>> 16 & 255) * (1-a)) + ((second >>> 16 & 255) * a));
        int g = (int)(((first >>> 8 & 255) * (1-a)) + ((second >>> 8 & 255) * a));
        int b = (int)(((first & 255) * (1-a)) + ((second & 255) * a));
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer vertices,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             int color, float nx, float ny, float nz, int light) {
        vertex(pose, vertices, x1, y1, z1, color, nx, ny, nz, light);
        vertex(pose, vertices, x2, y2, z2, color, nx, ny, nz, light);
        vertex(pose, vertices, x3, y3, z3, color, nx, ny, nz, light);
        vertex(pose, vertices, x4, y4, z4, color, nx, ny, nz, light);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer vertices, float x, float y, float z,
                               int color, float nx, float ny, float nz, int light) {
        vertices.addVertex(pose.pose(), x, y, z).setColor(color).setLight(light).setNormal(pose, nx, ny, nz);
    }

    public static final class State extends BlockEntityRenderState {
        private long energy;
        private long capacity;
        private int color;
    }
}
