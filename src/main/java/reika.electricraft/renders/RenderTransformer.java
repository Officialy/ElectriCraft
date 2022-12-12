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
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import org.joml.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;


import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.libraries.rendering.ReikaRenderHelper;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriModelLayers;
import reika.electricraft.renders.model.TransformerModel;
import reika.rotarycraft.auxiliary.IORenderer;
import reika.rotarycraft.base.blocks.BlockRotaryCraftMachine;
import reika.rotarycraft.modinterface.model.ElecMotorModel;
import reika.rotarycraft.registry.RotaryBlocks;

public class RenderTransformer extends ElectriTERenderer<BlockEntityTransformer>
{
	private TransformerModel transformer;
	public RenderTransformer(BlockEntityRendererProvider.Context context) {
		transformer = new TransformerModel(context.bakeLayer(ElectriModelLayers.TRANSFORMER));
	}
	public void renderBlockEntityTransformerAt(BlockEntityTransformer tile, PoseStack stack, MultiBufferSource bufferSource, int light)
	{
		Level level = tile.getLevel();
		boolean flag = level != null;
		BlockState blockstate = flag ? tile.getBlockState() : RotaryBlocks.ENGINE.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);

		float f = blockstate.getValue(BlockElectricMachine.FACING).toYRot();
		stack.pushPose();
		stack.translate(0.5F, 1.5F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-f));
		stack.mulPose(Axis.ZP.rotationDegrees(180));

		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid((TransformerModel.TEXTURE_LOCATION)));
		transformer.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		stack.popPose();
//		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(tile.getN1(), tile.getN2()), tile.phi, 0);
	}

	@Override
	public void render(BlockEntityTransformer tile, float p_112308_, PoseStack stack, MultiBufferSource multiBufferSource, int light, int p_112312_) {
		BlockEntityTransformer te = tile;
		if (this.doRenderModel(stack, te))
			this.renderBlockEntityTransformerAt(te, stack, multiBufferSource, light);
		if (te.isInWorld()) {// && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(stack, multiBufferSource, tile, tile.getX(), tile.getY(), tile.getZ());
			this.renderArrow(te, tile.getX(), tile.getY(), tile.getZ());
		}
	}

	private void renderArrow(BlockEntityTransformer te, double par2, double par4, double par6) {
		int a = Math.max(0, 512-te.getTicksExisted()*8);
		if (a > 0) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableTexture();
			ReikaRenderHelper.disableEntityLighting();
			float lw = RenderSystem.getShaderLineWidth();//todo check GL11.glGetFloat(GL11.GL_LINE_WIDTH);
			RenderSystem.lineWidth(5);

			Tesselator tess = Tesselator.getInstance();
			BufferBuilder v5 = tess.getBuilder();
//			v5.setBrightness(240);
			v5.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
//			v5.setColorRGBA_I(0xffffff, a);

			double h = 1.1;
			v5.vertex(0.5, h, 0.5).endVertex();
			double dr = 0.125;
			double r = 0.375;
			double w = 0.08;
			double dx = 0.5+te.getFacing().getStepX()*r;
			double dz = 0.5+te.getFacing().getStepZ()*r;
			v5.vertex(dx, h, dz).endVertex();

			v5.vertex(dx, h, dz).endVertex();
			v5.vertex(dx-te.getFacing().getStepX()*dr+te.getFacing().getStepZ()*w, h, dz-te.getFacing().getStepZ()*dr+te.getFacing().getStepX()*w).endVertex();

			v5.vertex(dx, h, dz).endVertex();
			v5.vertex(dx-te.getFacing().getStepX()*dr-te.getFacing().getStepZ()*w, h, dz-te.getFacing().getStepZ()*dr-te.getFacing().getStepX()*w).endVertex();
			tess.end();
			RenderSystem.lineWidth(lw);
		}
	}
}
