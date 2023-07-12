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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

import net.minecraft.client.gui.Font;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.libraries.rendering.ReikaRenderHelper;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityMeter;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriModelLayers;
import reika.electricraft.renders.model.MeterModel;
import reika.rotarycraft.auxiliary.IORenderer;
import reika.rotarycraft.base.blocks.BlockRotaryCraftMachine;
import reika.rotarycraft.registry.RotaryBlocks;

public class RenderElectricMeter extends ElectriTERenderer<BlockEntityMeter> {

    private final MeterModel meterModel;

    public RenderElectricMeter(BlockEntityRendererProvider.Context context) {
        meterModel = new MeterModel(context.bakeLayer(ElectriModelLayers.METER));
    }

    public void renderBlockEntityMeterAt(BlockEntityMeter tile, PoseStack stack, MultiBufferSource bufferSource, int light) {

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

        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid((MeterModel.TEXTURE_LOCATION)));
        meterModel.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        stack.popPose();
        if (tile.isInWorld())
            this.renderText(tile, stack);

//		this.closeGL(tile);
    }

    private void renderText(BlockEntityMeter tile, PoseStack stack) {
        Font f = this.getFontRenderer();
        String s1 = "Voltage:";
        String s2 = "Current:";
        String s1b = String.format("%dV", tile.getWireVoltage());
        String s2b = String.format("%dA", tile.getWireCurrent());

        String s3 = String.format("Resistance: %d Ohms", tile.getResistance());

        stack.pushPose();
        float s = 0.01F;
        ReikaRenderHelper.disableLighting();
//		stack.glDisable(GL11.GL_LIGHTING);
        ReikaRenderHelper.disableEntityLighting();
//		stack.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//		RenderSystem.depthMask(false);
        float ang = Minecraft.getInstance().player.yHeadRot % 360;
        if (ang < 0)
            ang += 360;
        int ra = 90 * (int) ((ang + 180 + 45) / 90F);

        stack.mulPose(Axis.YP.rotationDegrees(ra));
        stack.mulPose(Axis.XP.rotationDegrees(90));
//        ElectriCraft.LOGGER.info(ang);
        stack.translate(-0.515, -0.515, -0.975);


        stack.scale(s, s, s);

        int dx = -30;
        int dy = -30;

        f.draw(stack, s1, dx, dy, 0xffffff);
        f.draw(stack, s1b, dx, dy + 10, 0xffffff);

        f.draw(stack, s2, dx, dy + 30, 0xffffff);
        f.draw(stack, s2b, dx, dy + 40, 0xffffff);

        stack.popPose();
//		RenderSystem.depthMask(true);
        ReikaRenderHelper.enableEntityLighting();
//		GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public void render(BlockEntityMeter tile, float p_112308_, PoseStack stack, MultiBufferSource bufferSource, int light, int p_112312_) {
        if (this.doRenderModel(stack, tile))
            this.renderBlockEntityMeterAt(tile, stack, bufferSource, light);
        if (tile.isInWorld()) {// && MinecraftForgeClient.getRenderPass() == 1) {
            IORenderer.renderIO(stack, bufferSource, tile, tile.getX(), tile.getY(), tile.getZ());
        }
    }

    @Override
    protected String getModID() {
        return ElectriCraft.MODID;
    }

}
