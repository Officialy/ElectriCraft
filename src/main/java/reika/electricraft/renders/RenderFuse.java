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
import org.joml.Vector3f;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityFuse;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriModelLayers;
import reika.electricraft.renders.model.FuseModel;
import reika.rotarycraft.auxiliary.IORenderer;
import reika.rotarycraft.base.blocks.BlockRotaryCraftMachine;
import reika.rotarycraft.registry.RotaryBlocks;

import static reika.electricraft.ElectriCraft.MODID;

public class RenderFuse extends ElectriTERenderer<BlockEntityFuse> {
    private final FuseModel modelFuse;

    public RenderFuse(BlockEntityRendererProvider.Context context) {
        modelFuse = new FuseModel(context.bakeLayer(ElectriModelLayers.FUSE));
    }

    public void renderBlockEntityFuseAt(BlockEntityFuse tile, PoseStack stack, VertexConsumer bufferSource, int light) {
//		this.setupGL(tile, par2, par4, par6);
//		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        stack.pushPose();

        Level level = tile.getLevel();
        boolean flag = level != null;
        BlockState blockstate = flag ? tile.getBlockState() : ElectriBlocks.FUSE_32A.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);

        float facing = blockstate.getValue(BlockElectricMachine.FACING).toYRot();

        stack.translate(0.5F, 1.5F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
        stack.mulPose(Axis.ZP.rotationDegrees(180));


        Identifier s = Identifier.fromNamespaceAndPath(MODID, "textures/fusetex.png");
        if (tile.isOverloaded()) {
            s = Identifier.fromNamespaceAndPath(MODID,"fusetex-burn.png");
        } else {
            float f = tile.getWireCurrent() / (float) tile.getMaxCurrent();
            if (f >= 0.75) {
                s = Identifier.fromNamespaceAndPath(MODID,"fusetex-hot3.png");
            } else if (f >= 0.5) {
                s = Identifier.fromNamespaceAndPath(MODID,"fusetex-hot2.png");
            } else if (f >= 0.25) {
                s = Identifier.fromNamespaceAndPath(MODID,"fusetex-hot.png");
            }
        }
//		this.bindTextureByName(s);


//		stack.mulPose(var11, 0.0F, 1.0F, 0.0F);
//		modelFuse.renderAll(tile, null, tile.phi);
        VertexConsumer vertexconsumer = bufferSource;
        modelFuse.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        stack.popPose();
//		this.closeGL(tile);
    }

        @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;
        BlockEntity be = level.getBlockEntity(state.blockPos);
        if (!(be instanceof BlockEntityFuse tile) || !this.doRenderModel(poseStack, tile))
            return;

        PoseStack snapped = new PoseStack();
        snapped.last().set(poseStack.last());
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutoutCull(this.getTexture(tile)),
                (pose, vertices) -> this.renderBlockEntityFuseAt(tile, snapped, vertices, state.lightCoords));
        if (tile.isInWorld())
            IORenderer.renderIO(poseStack, collector, tile, tile.getBlockPos());
    }

    private Identifier getTexture(BlockEntityFuse tile) {
        if (tile.isOverloaded())
            return Identifier.fromNamespaceAndPath(MODID, "textures/fusetex-burn.png");
        float fraction = tile.getWireCurrent() / (float)tile.getMaxCurrent();
        if (fraction >= 0.75F)
            return Identifier.fromNamespaceAndPath(MODID, "textures/fusetex-hot3.png");
        if (fraction >= 0.5F)
            return Identifier.fromNamespaceAndPath(MODID, "textures/fusetex-hot2.png");
        if (fraction >= 0.25F)
            return Identifier.fromNamespaceAndPath(MODID, "textures/fusetex-hot.png");
        return Identifier.fromNamespaceAndPath(MODID, "textures/fusetex.png");
    }
}
