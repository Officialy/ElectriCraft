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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.math.Axis;
import org.joml.Vector3f;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityMotor;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriModelLayers;
import reika.rotarycraft.auxiliary.IORenderer;
import reika.rotarycraft.base.blocks.BlockRotaryCraftMachine;
import reika.rotarycraft.modinterface.model.ElecMotorModel;
import reika.rotarycraft.registry.RotaryBlocks;

import java.util.ArrayList;

public class RenderMotor extends ElectriTERenderer<BlockEntityMotor> {
    private final ElecMotorModel elecMotorModel;
    public RenderMotor(BlockEntityRendererProvider.Context context) {
        elecMotorModel = new ElecMotorModel(context.bakeLayer(ElectriModelLayers.MOTOR));
    }

    public void renderBlockEntityMotorAt(BlockEntityMotor tile, PoseStack stack, VertexConsumer bufferSource, int light) {
//        this.setupGL(tile, par2, par4, par6);
//        this.bindTextureByName("/Reika/ElectriCraft/Textures/elecmotortex.png");


        Level level = tile.getLevel();
        boolean flag = level != null;
        BlockState blockstate = flag ? tile.getBlockState() : ElectriBlocks.MOTOR.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);

        float f = blockstate.getValue(BlockElectricMachine.FACING).toYRot();
        stack.pushPose();
        stack.translate(0.5F, 1.5F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(-f));
        stack.mulPose(Axis.ZP.rotationDegrees(180));

        if (tile.isFlipped && tile.getFacing().getStepZ() != 0) {
            stack.mulPose(Axis.YP.rotationDegrees(180));
        }
        VertexConsumer vertexconsumer = bufferSource;
        ArrayList<Object> conditions = new ArrayList<>();
        conditions.add(5);
        int finColor = tile.getFinColor();
        conditions.add(finColor);
        // Power alone does not make the cooling fins luminous. Only the temperature-derived
        // colour transition is rendered full-bright; idle/cool fins retain ordinary world light.
        conditions.add(finColor != 0x515168);
        elecMotorModel.renderAll(stack, vertexconsumer, light, tile, conditions, tile.phi, 0);
        stack.popPose();
//        this.closeGL(tile);
    }

	    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;
        BlockEntity be = level.getBlockEntity(state.blockPos);
        if (!(be instanceof BlockEntityMotor tile) || !this.doRenderModel(poseStack, tile))
            return;

        PoseStack snapped = new PoseStack();
        snapped.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "textures/elecmotortex.png")),
                (pose, vertices) -> this.renderBlockEntityMotorAt(tile, snapped, vertices, state.lightCoords));
        if (tile.isInWorld())
            IORenderer.renderIO(poseStack, collector, tile, tile.getBlockPos());
    }
}
