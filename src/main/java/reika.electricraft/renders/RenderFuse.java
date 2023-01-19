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
import com.mojang.math.Axis;
import org.joml.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

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
    private FuseModel modelFuse;

    public RenderFuse(BlockEntityRendererProvider.Context context) {
        modelFuse = new FuseModel(context.bakeLayer(ElectriModelLayers.FUSE));
    }

    public void renderBlockEntityFuseAt(BlockEntityFuse tile, PoseStack stack, MultiBufferSource bufferSource, int light) {
//		this.setupGL(tile, par2, par4, par6);
//		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        stack.pushPose();

        Level level = tile.getLevel();
        boolean flag = level != null;
        BlockState blockstate = flag ? tile.getBlockState() : ElectriBlocks.FUSE.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);

        float facing = blockstate.getValue(BlockElectricMachine.FACING).toYRot();

        stack.translate(0.5F, 1.5F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
        stack.mulPose(Axis.ZP.rotationDegrees(180));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ResourceLocation s = new ResourceLocation(MODID, "textures/fusetex.png");
        if (tile.isOverloaded()) {
            s = new ResourceLocation(MODID,"fusetex-burn.png");
        } else {
            float f = tile.getWireCurrent() / (float) tile.getMaxCurrent();
            if (f >= 0.75) {
                s = new ResourceLocation(MODID,"fusetex-hot3.png");
            } else if (f >= 0.5) {
                s = new ResourceLocation(MODID,"fusetex-hot2.png");
            } else if (f >= 0.25) {
                s = new ResourceLocation(MODID,"fusetex-hot.png");
            }
        }
//		this.bindTextureByName(s);


//		stack.mulPose(var11, 0.0F, 1.0F, 0.0F);
        stack.translate(0, -0.1875, 0);
//		modelFuse.renderAll(tile, null, tile.phi);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid(s));
        modelFuse.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        stack.popPose();
//		this.closeGL(tile);
    }

    @Override
    public void render(BlockEntityFuse tile, float p_112308_, PoseStack stack, MultiBufferSource bufferSource, int light, int p_112312_) {

        {
            //if (this.doRenderModel((BlockEntityFuse)tile))
            this.renderBlockEntityFuseAt((BlockEntityFuse) tile, stack, bufferSource, light);
            if (tile.isInWorld()) {// && MinecraftForgeClient.getRenderPass() == 1) {
                IORenderer.renderIO(stack, bufferSource, tile, tile.getX(), tile.getY(), tile.getZ());
            }
        }
    }
}
