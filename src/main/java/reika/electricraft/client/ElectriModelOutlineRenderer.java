package reika.electricraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.renders.model.FuseModel;
import reika.electricraft.renders.model.MeterModel;
import reika.electricraft.renders.model.PreciseResistorModel;
import reika.electricraft.renders.model.RelayModel;
import reika.electricraft.renders.model.ResistorModel;
import reika.electricraft.renders.model.TransformerModel;
import reika.rotarycraft.modinterface.model.ElecMotorModel;
import reika.rotarycraft.modinterface.model.GeneratorModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Polygon-accurate hover/mining outlines for ElectriCraft's Techne block models. */
public final class ElectriModelOutlineRenderer implements CustomBlockOutlineRenderer {

    private static final int NORMAL_COLOR = 0x66000000;
    private static final int HIGH_CONTRAST_COLOR = -11010079;
    private static final Map<Block, ModelPart> MODELS = new IdentityHashMap<>();
    private static final Map<OutlineKey, List<Line>> OUTLINES = new java.util.HashMap<>();

    private final BlockPos pos;
    private final List<Line> lines;

    private ElectriModelOutlineRenderer(BlockPos pos, List<Line> lines) {
        this.pos = pos;
        this.lines = lines;
    }

    public static void extract(ExtractBlockOutlineRenderStateEvent event) {
        ModelPart model = modelFor(event.getBlockState().getBlock());
        if (model == null)
            return;
        Direction facing = event.getBlockState().getValue(ElectriBlock.FACING);
        List<Line> lines = OUTLINES.computeIfAbsent(new OutlineKey(event.getBlockState().getBlock(), facing),
                key -> modelLines(model, key.facing));
        if (!lines.isEmpty())
            event.addCustomRenderer(new ElectriModelOutlineRenderer(event.getBlockPos(), lines));
    }

    private static ModelPart modelFor(Block block) {
        ModelPart cached = MODELS.get(block);
        if (cached != null)
            return cached;
        ModelPart created;
        if (block == ElectriBlocks.GENERATOR.get())
            created = GeneratorModel.createLayer().bakeRoot();
        else if (block == ElectriBlocks.MOTOR.get())
            created = ElecMotorModel.createLayer().bakeRoot();
        else if (block == ElectriBlocks.METER.get())
            created = MeterModel.createLayer().bakeRoot();
        else if (block == ElectriBlocks.PRECISE_RESISTOR.get())
            created = PreciseResistorModel.createLayer().bakeRoot();
        else if (block == ElectriBlocks.TRANSFORMER.get())
            created = TransformerModel.createLayer().bakeRoot();
        else if (ElectriBlocks.isFuse(block))
            created = FuseModel.createLayer().bakeRoot();
        else if (block == ElectriBlocks.RESISTOR.get())
            created = ResistorModel.createLayer().bakeRoot();
        else if (block == ElectriBlocks.RELAY.get())
            created = RelayModel.createLayer().bakeRoot();
        else
            return null;
        MODELS.put(block, created);
        return created;
    }

    private static List<Line> modelLines(ModelPart model, Direction facing) {
        PoseStack modelPose = new PoseStack();
        modelPose.translate(.5F, 1.5F, .5F);
        modelPose.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        modelPose.mulPose(Axis.ZP.rotationDegrees(180F));
        LineCollector collector = new LineCollector();
        model.visit(modelPose, (pose, path, cubeIndex, cube) -> {
            for (ModelPart.Polygon polygon : cube.polygons) {
                ModelPart.Vertex[] vertices = polygon.vertices();
                for (int vertex = 0; vertex < vertices.length; vertex++) {
                    collector.add(transform(pose, vertices[vertex]),
                            transform(pose, vertices[(vertex + 1) % vertices.length]));
                }
            }
        });
        return collector.finish();
    }

    private static Vector3f transform(PoseStack.Pose pose, ModelPart.Vertex vertex) {
        return pose.pose().transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), new Vector3f());
    }

    @Override
    public boolean render(BlockOutlineRenderState renderState, SubmitNodeCollector collector,
                          PoseStack poseStack, LevelRenderState levelRenderState) {
        float normalWidth = Minecraft.getInstance().gameRenderer.gameRenderState()
                .windowRenderState.appropriateLineWidth;
        if (renderState.highContrast())
            submit(collector, poseStack, levelRenderState, RenderTypes.secondaryBlockOutline(), 0xFF000000, 7F);
        int color = renderState.highContrast() ? HIGH_CONTRAST_COLOR : NORMAL_COLOR;
        submit(collector, poseStack, levelRenderState, RenderTypes.lines(), color, normalWidth);
        return true;
    }

    private void submit(SubmitNodeCollector collector, PoseStack poseStack, LevelRenderState levelRenderState,
                        RenderType renderType, int color, float width) {
        var camera = levelRenderState.cameraRenderState.pos;
        poseStack.pushPose();
        poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
        collector.submitCustomGeometry(poseStack, renderType, (pose, vertices) -> {
            Vector3f normal = new Vector3f();
            for (Line line : lines) {
                normal.set(line.to).sub(line.from).normalize();
                vertices.addVertex(pose, line.from.x, line.from.y, line.from.z)
                        .setColor(color).setNormal(pose, normal).setLineWidth(width);
                vertices.addVertex(pose, line.to.x, line.to.y, line.to.z)
                        .setColor(color).setNormal(pose, normal).setLineWidth(width);
            }
        });
        poseStack.popPose();
    }

    private static final class LineCollector {
        private final List<Line> lines = new ArrayList<>();
        private final Set<EdgeKey> edges = new HashSet<>();

        void add(Vector3fc from, Vector3fc to) {
            if (from.distanceSquared(to) <= 1.0E-10F)
                return;
            EdgeKey key = EdgeKey.of(from, to);
            if (edges.add(key))
                lines.add(new Line(new Vector3f(from), new Vector3f(to)));
        }

        List<Line> finish() {
            return List.copyOf(lines);
        }
    }

    private record Line(Vector3f from, Vector3f to) {}

    private record OutlineKey(Block block, Direction facing) {}

    private record PointKey(int x, int y, int z) implements Comparable<PointKey> {
        static PointKey of(Vector3fc point) {
            return new PointKey(Float.floatToIntBits(point.x()), Float.floatToIntBits(point.y()),
                    Float.floatToIntBits(point.z()));
        }

        @Override
        public int compareTo(PointKey other) {
            int compare = Integer.compareUnsigned(x, other.x);
            if (compare == 0) compare = Integer.compareUnsigned(y, other.y);
            if (compare == 0) compare = Integer.compareUnsigned(z, other.z);
            return compare;
        }
    }

    private record EdgeKey(PointKey first, PointKey second) {
        static EdgeKey of(Vector3fc from, Vector3fc to) {
            PointKey a = PointKey.of(from);
            PointKey b = PointKey.of(to);
            return a.compareTo(b) <= 0 ? new EdgeKey(a, b) : new EdgeKey(b, a);
        }
    }
}
