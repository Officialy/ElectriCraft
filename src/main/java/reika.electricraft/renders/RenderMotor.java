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

public class RenderMotor extends ElectriTERenderer<BlockEntityMotor> {
    private ElecMotorModel elecMotorModel;
    public RenderMotor(BlockEntityRendererProvider.Context context) {
        elecMotorModel = new ElecMotorModel(context.bakeLayer(ElectriModelLayers.MOTOR));
    }

    public void renderBlockEntityMotorAt(BlockEntityMotor tile, PoseStack stack, MultiBufferSource bufferSource, int light) {
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
            stack.mulPose(Axis.ZP.rotationDegrees(180));
        }
        int num = tile.isInWorld() ? 5 : 5;
//        var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(num, tile.getFinColor(), tile.getPower() > 0), tile.phi, 0);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid((new ResourceLocation(ElectriCraft.MODID,"textures/elecmotortex.png"))));
        elecMotorModel.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        stack.popPose();
//        this.closeGL(tile);
    }

	@Override
    public void render(BlockEntityMotor tile, float p_112308_, PoseStack stack, MultiBufferSource multiBufferSource, int light, int p_112312_) {
        if (this.doRenderModel(stack, tile))
            this.renderBlockEntityMotorAt(tile, stack, multiBufferSource, light);
        if (tile.isInWorld()) {// && MinecraftForgeClient.getRenderPass() == 1) {
            IORenderer.renderIO(stack, multiBufferSource, tile, tile.getX(), tile.getY(), tile.getZ());
        }
    }

}
