package reika.electricraft.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

/** Shared exact-face cube emission for legacy blocks whose six textures are selected at runtime. */
public final class LegacyCubeRenderer {

    private LegacyCubeRenderer() {
    }

    public static void submitFace(PoseStack stack, SubmitNodeCollector collector, RenderType renderType,
                                  Direction face, int light, int color) {
        PoseStack snapshot = new PoseStack();
        snapshot.last().set(stack.last());
        collector.submitCustomGeometry(stack, renderType,
                (pose, vertices) -> emitFace(snapshot.last(), vertices, face, light, color));
    }

    public static void emitFace(PoseStack.Pose pose, VertexConsumer vertices, Direction face, int light, int color) {
        switch (face) {
            // Vertex order and UV corners intentionally mirror vanilla 26.2 FaceInfo/FaceBakery.
            // This preserves front/back artwork orientation and keeps entityCutout face culling valid.
            case DOWN -> quad(pose, vertices, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, -1, 0, light, color);
            case UP -> quad(pose, vertices, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, light, color);
            case NORTH -> quad(pose, vertices, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, -1, light, color);
            case SOUTH -> quad(pose, vertices, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, light, color);
            case WEST -> quad(pose, vertices, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, -1, 0, 0, light, color);
            case EAST -> quad(pose, vertices, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, light, color);
        }
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer vertices,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float nx, float ny, float nz, int light, int color) {
        vertex(pose, vertices, x1, y1, z1, 0, 0, nx, ny, nz, light, color);
        vertex(pose, vertices, x2, y2, z2, 0, 1, nx, ny, nz, light, color);
        vertex(pose, vertices, x3, y3, z3, 1, 1, nx, ny, nz, light, color);
        vertex(pose, vertices, x4, y4, z4, 1, 0, nx, ny, nz, light, color);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer vertices, float x, float y, float z,
                               float u, float v, float nx, float ny, float nz, int light, int color) {
        vertices.addVertex(pose.pose(), x, y, z).setColor(color).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
    }
}
