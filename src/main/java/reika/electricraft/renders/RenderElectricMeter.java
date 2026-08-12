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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityMeter;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriModelLayers;
import reika.electricraft.renders.model.MeterModel;
import reika.rotarycraft.auxiliary.IORenderer;

public class RenderElectricMeter extends ElectriTERenderer<BlockEntityMeter> {

    private final MeterModel meterModel;

    public RenderElectricMeter(BlockEntityRendererProvider.Context context) {
        meterModel = new MeterModel(context.bakeLayer(ElectriModelLayers.METER));
    }

    public void renderBlockEntityMeterAt(BlockEntityMeter tile, PoseStack stack, VertexConsumer bufferSource, int light) {

        Level level = tile.getLevel();
        boolean flag = level != null;
        BlockState blockstate = flag ? tile.getBlockState() : ElectriBlocks.METER.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);

        float f = blockstate.getValue(BlockElectricMachine.FACING).toYRot();
        stack.pushPose();
        stack.translate(0.5F, 1.5F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(-f));
        stack.mulPose(Axis.ZP.rotationDegrees(180));
//		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
//		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//		this.setupGL(tile, par2, par4, par6);

        VertexConsumer vertexconsumer = bufferSource;
        meterModel.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        stack.popPose();

//		this.closeGL(tile);
    }

        @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;
        BlockEntity be = level.getBlockEntity(state.blockPos);
        if (!(be instanceof BlockEntityMeter tile) || !this.doRenderModel(poseStack, tile))
            return;

        PoseStack snapped = new PoseStack();
        snapped.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(MeterModel.TEXTURE_LOCATION),
                (pose, vertices) -> this.renderBlockEntityMeterAt(tile, snapped, vertices, state.lightCoords));
        if (tile.isInWorld()) {
            this.submitText(tile, poseStack, collector);
            IORenderer.renderIO(poseStack, collector, tile, tile.getBlockPos());
        }
    }

    /** The source meter labels are square-to-camera, snapping every quarter turn. */
    private void submitText(BlockEntityMeter tile, PoseStack stack, SubmitNodeCollector collector) {
        float angle = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.yHeadRot % 360F;
        if (angle < 0)
            angle += 360F;
        int rightAngle = 90 * (int)((angle + 225F) / 90F);
        stack.pushPose();
        stack.translate(0.5, 0.5, 0.5);
        stack.mulPose(Axis.YP.rotationDegrees(rightAngle));
        stack.translate(0, 0.515, 0);
        stack.scale(0.01F, 0.01F, 0.01F);
        stack.mulPose(Axis.XN.rotationDegrees(90));
        submitLine(collector, stack, "Voltage:", -30, -30);
        submitLine(collector, stack, String.format("%dV", tile.getWireVoltage()), -30, -20);
        submitLine(collector, stack, "Current:", -30, 0);
        submitLine(collector, stack, String.format("%dA", tile.getWireCurrent()), -30, 10);
        stack.popPose();
    }

    private static void submitLine(SubmitNodeCollector collector, PoseStack stack, String text, float x, float y) {
        collector.submitText(stack, x, y, Component.literal(text).getVisualOrderText(), false,
                Font.DisplayMode.POLYGON_OFFSET, 0xF000F0, 0xFFFFFFFF, 0, 0);
    }
}
