/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.renders;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.libraries.rendering.ReikaRenderHelper;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriModelLayers;
import reika.electricraft.renders.model.TransformerModel;
import reika.rotarycraft.auxiliary.IORenderer;
import reika.rotarycraft.base.blocks.BlockRotaryCraftMachine;
import reika.rotarycraft.modinterface.model.ElecMotorModel;
import reika.rotarycraft.registry.RotaryBlocks;

public class RenderTransformer extends ElectriTERenderer<BlockEntityTransformer>
{
	private final TransformerModel transformer;
	public RenderTransformer(BlockEntityRendererProvider.Context context) {
		transformer = new TransformerModel(context.bakeLayer(ElectriModelLayers.TRANSFORMER));
	}
	public void renderBlockEntityTransformerAt(BlockEntityTransformer tile, PoseStack stack, VertexConsumer bufferSource, int light)
	{
		Level level = tile.getLevel();
		boolean flag = level != null;
		BlockState blockstate = flag ? tile.getBlockState() : ElectriBlocks.TRANSFORMER.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);

		float f = blockstate.getValue(BlockElectricMachine.FACING).toYRot();
		stack.pushPose();
		stack.translate(0.5F, 1.5F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-f));
		stack.mulPose(Axis.ZP.rotationDegrees(180));

		VertexConsumer vertexconsumer = bufferSource;
		transformer.renderAll(stack, vertexconsumer, light, tile.getN1(), tile.getN2());
		stack.popPose();
//		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(tile.getN1(), tile.getN2()), tile.phi, 0);
	}

	    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;
        BlockEntity be = level.getBlockEntity(state.blockPos);
        if (!(be instanceof BlockEntityTransformer tile) || !this.doRenderModel(poseStack, tile))
            return;

        PoseStack snapped = new PoseStack();
        snapped.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TransformerModel.TEXTURE_LOCATION),
                (pose, vertices) -> this.renderBlockEntityTransformerAt(tile, snapped, vertices, state.lightCoords));
        if (tile.isInWorld())
            IORenderer.renderIO(poseStack, collector, tile, tile.getBlockPos());
        this.renderArrow(tile, poseStack, collector);
    }

    /** Exact V31a transformer-facing pulse, expressed through the 26.2 line pipeline. */
    private void renderArrow(BlockEntityTransformer tile, PoseStack stack, SubmitNodeCollector collector) {
        int alpha = Math.max(0, 512 - tile.getTicksExisted() * 8);
        if (alpha <= 0)
            return;
        Direction facing = tile.getFacing();
        float headX = 0.5F + facing.getStepX() * 0.375F;
        float headZ = 0.5F + facing.getStepZ() * 0.375F;
        int color = (alpha << 24) | 0xFFFFFF;
        collector.submitCustomGeometry(stack, RenderTypes.lines(), (pose, vertices) -> {
            arrowLine(pose, vertices, 0.5F, 1.1F, 0.5F, headX, 1.1F, headZ, color);
            arrowLine(pose, vertices, headX, 1.1F, headZ,
                    headX - facing.getStepX() * 0.125F + facing.getStepZ() * 0.08F, 1.1F,
                    headZ - facing.getStepZ() * 0.125F + facing.getStepX() * 0.08F, color);
            arrowLine(pose, vertices, headX, 1.1F, headZ,
                    headX - facing.getStepX() * 0.125F - facing.getStepZ() * 0.08F, 1.1F,
                    headZ - facing.getStepZ() * 0.125F - facing.getStepX() * 0.08F, color);
        });
    }

    private static void arrowLine(PoseStack.Pose pose, VertexConsumer vertices,
                                  float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        float nx = x2 - x1;
        float ny = y2 - y1;
        float nz = z2 - z1;
        float length = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0) {
            nx /= length;
            ny /= length;
            nz /= length;
        }
        vertices.addVertex(pose, x1, y1, z1).setColor(color).setNormal(pose, nx, ny, nz).setLineWidth(5F);
        vertices.addVertex(pose, x2, y2, z2).setColor(color).setNormal(pose, nx, ny, nz).setLineWidth(5F);
    }
}
