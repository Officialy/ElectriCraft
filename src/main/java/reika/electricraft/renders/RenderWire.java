package reika.electricraft.renders;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityWire;
import reika.electricraft.registry.WireType;

/**
 * Dynamic renderer for the original ElectriCraft wire network.
 *
 * <p>The V31a world renderer built a centre conductor and a textured branch for every connected
 * face, selecting one of the exact material/insulation centre and end sprites. The previous port
 * emitted no world geometry at all. This renderer keeps the original dimensions -- bare wires are
 * 1/4 block wide, insulated wires 1/3 block wide -- and snapshots all BE data before the submit
 * phase so no client-world lookup leaks into the deferred render callback.</p>
 */
public final class RenderWire extends ElectriTERenderer<BlockEntityWire> {

    private static final int WHITE = 0xFFFFFFFF;

    public RenderWire(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BlockEntityWire wire, BlockEntityRenderState renderState, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(wire, renderState, partialTicks, cameraPosition, breakProgress);
        State state = (State)renderState;
        state.type = wire.getWireType();
        state.insulated = wire.insulated;
        for (Direction side : Direction.values()) {
            state.connected[side.ordinal()] = wire.getLevel() != null
                    && wire.isConnectedOnSideAt(wire.getLevel(), wire.getX(), wire.getY(), wire.getZ(), side);
        }
    }

    @Override
    public void submit(BlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        State state = (State)renderState;
        if (state.type == null)
            return;

        float halfWidth = state.insulated ? 1F / 6F : 1F / 8F;
        Identifier centreTexture = texture(state.type, state.insulated, false);
        Identifier endTexture = texture(state.type, state.insulated, true);
        PoseStack snapped = new PoseStack();
        snapped.last().set(poseStack.last());
        int light = state.lightCoords;

        // The central conductor retains its centre sprite even when every face is connected.
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(centreTexture),
                (pose, vertices) -> emitBox(snapped.last(), vertices,
                        .5F - halfWidth, .5F - halfWidth, .5F - halfWidth,
                        .5F + halfWidth, .5F + halfWidth, .5F + halfWidth,
                        light));

        // The original renderer switches to the matching *_end art only for arms that join a
        // neighbour. Arms intentionally bridge a little past the block boundary, reproducing the
        // legacy seamless overlap between adjacent wire blocks.
        if (hasConnection(state.connected)) {
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(endTexture),
                    (pose, vertices) -> {
                        for (Direction side : Direction.values()) {
                            if (state.connected[side.ordinal()])
                                emitArm(snapped.last(), vertices, side, halfWidth, state.insulated, light);
                        }
                    });
        }
    }

    public static Identifier texture(WireType type, boolean insulated, boolean end) {
        String name = type.getIconTexture() + (insulated ? "_ins" : "") + (end ? "_end" : "");
        return Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "textures/blocks/wire/" + name + ".png");
    }

    private static boolean hasConnection(boolean[] connections) {
        for (boolean connection : connections) {
            if (connection)
                return true;
        }
        return false;
    }

    static void emitArm(PoseStack.Pose pose, VertexConsumer vertices, Direction side, float halfWidth,
                                boolean insulated, int light) {
        // V31a's connection query deliberately inverted every non-X direction, and its renderer
        // compensated by drawing those enum cases toward the opposite face. The query remains
        // source-faithful in BlockEntityWire/ElectriCable, so preserve that visual compensation;
        // otherwise north/south/up/down branches appear on the wrong side of the block.
        Direction visualSide = side.getStepX() == 0 ? side.getOpposite() : side;
        // V31a's arms are two wire-widths long past the central cube. The insulated end cap closes
        // the outer face; bare conductors deliberately retain the original open joint visual.
        float inner = .5F - halfWidth;
        float outer = .5F + halfWidth + 4F * halfWidth;
        float x0 = .5F - halfWidth, x1 = .5F + halfWidth;
        float y0 = x0, y1 = x1;
        float z0 = x0, z1 = x1;
        switch (visualSide) {
            case DOWN -> emitBox(pose, vertices, x0, -outer + 1F, z0, x1, inner, z1, light);
            case UP -> emitBox(pose, vertices, x0, 1F - inner, z0, x1, outer, z1, light);
            case NORTH -> emitBox(pose, vertices, x0, y0, -outer + 1F, x1, y1, inner, light);
            case SOUTH -> emitBox(pose, vertices, x0, y0, 1F - inner, x1, y1, outer, light);
            case WEST -> emitBox(pose, vertices, -outer + 1F, y0, z0, inner, y1, z1, light);
            case EAST -> emitBox(pose, vertices, 1F - inner, y0, z0, outer, y1, z1, light);
        }
        if (insulated)
            emitInsulationCap(pose, vertices, visualSide, halfWidth, outer, light);
    }

    private static void emitInsulationCap(PoseStack.Pose pose, VertexConsumer vertices, Direction side,
                                          float halfWidth, float outer, int light) {
        float thickness = 1F / 256F; // epsilon cap avoids holes at the end of the solid insulation.
        float a = .5F - halfWidth;
        float b = .5F + halfWidth;
        switch (side) {
            case DOWN -> emitBox(pose, vertices, a, 1F - outer - thickness, a, b, 1F - outer, b, light);
            case UP -> emitBox(pose, vertices, a, outer, a, b, outer + thickness, b, light);
            case NORTH -> emitBox(pose, vertices, a, a, 1F - outer - thickness, b, b, 1F - outer, light);
            case SOUTH -> emitBox(pose, vertices, a, a, outer, b, b, outer + thickness, light);
            case WEST -> emitBox(pose, vertices, 1F - outer - thickness, a, a, 1F - outer, b, b, light);
            case EAST -> emitBox(pose, vertices, outer, a, a, outer + thickness, b, b, light);
        }
    }

    public static void emitBox(PoseStack.Pose pose, VertexConsumer vertices,
                                float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                int light) {
        // Texture coordinates are deliberately full-face, matching the legacy IIcon calls for
        // each arm/cap. Draw both windings: wires are tiny exposed geometry and must retain the
        // source's effectively double-sided look under modern backface culling.
        face(pose, vertices, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, 0, -1, 0, light);
        face(pose, vertices, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ, 0, 1, 0, light);
        face(pose, vertices, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, 0, 0, -1, light);
        face(pose, vertices, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, minX, minY, maxZ, 0, 0, 1, light);
        face(pose, vertices, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ, -1, 0, 0, light);
        face(pose, vertices, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, 1, 0, 0, light);
    }

    private static void face(PoseStack.Pose pose, VertexConsumer vertices,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float nx, float ny, float nz, int light) {
        vertex(pose, vertices, x1, y1, z1, 0, 0, nx, ny, nz, light);
        vertex(pose, vertices, x2, y2, z2, 0, 1, nx, ny, nz, light);
        vertex(pose, vertices, x3, y3, z3, 1, 1, nx, ny, nz, light);
        vertex(pose, vertices, x4, y4, z4, 1, 0, nx, ny, nz, light);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer vertices, float x, float y, float z,
                               float u, float v, float nx, float ny, float nz, int light) {
        vertices.addVertex(pose.pose(), x, y, z).setColor(WHITE).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
    }

    public static final class State extends BlockEntityRenderState {
        private WireType type;
        private boolean insulated;
        private final boolean[] connected = new boolean[Direction.values().length];
    }
}
