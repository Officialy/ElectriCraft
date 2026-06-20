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
		transformer.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
		stack.popPose();
//		var14.renderAll(tile, ReikaJavaLibrary.makeListFrom(tile.getN1(), tile.getN2()), tile.phi, 0);
	}

	// 1.21.5: render -> submit; @Override dropped
	public void render(BlockEntityTransformer tile, float p_112308_, PoseStack stack, VertexConsumer multiBufferSource, int light, int p_112312_) {
		BlockEntityTransformer te = tile;
		if (this.doRenderModel(stack, te))
			this.renderBlockEntityTransformerAt(te, stack, multiBufferSource, light);
		if (te.isInWorld()) {// && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(stack, multiBufferSource, tile, tile.getX(), tile.getY(), tile.getZ());
			this.renderArrow(te, tile.getX(), tile.getY(), tile.getZ());
		}
	}

	private void renderArrow(BlockEntityTransformer te, double par2, double par4, double par6) {
		// TODO: Port to 26.1 rendering API (Tesselator.getBuilder() + vertex().endVertex() + end() all removed)
	}
}

