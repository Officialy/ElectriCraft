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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityRelay;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriModelLayers;
import reika.electricraft.renders.model.RelayModel;
import reika.rotarycraft.auxiliary.IORenderer;
import reika.rotarycraft.base.blocks.BlockRotaryCraftMachine;
import reika.rotarycraft.registry.RotaryBlocks;

public class RenderRelay extends ElectriTERenderer<BlockEntityRelay>
{
	private final RelayModel relay;
	public RenderRelay(BlockEntityRendererProvider.Context context) {
		relay = new RelayModel(context.bakeLayer(ElectriModelLayers.RELAY));
	}
	public void renderBlockEntityRelayAt(BlockEntityRelay tile, PoseStack stack, MultiBufferSource bufferSource, int light)
	{
		Level level = tile.getLevel();
		boolean flag = level != null;
		BlockState blockstate = flag ? tile.getBlockState() : ElectriBlocks.RELAY.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);

		float f = blockstate.getValue(BlockElectricMachine.FACING).toYRot();
		stack.pushPose();
		stack.translate(0.5F, 1.5F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-f));
		stack.mulPose(Axis.ZP.rotationDegrees(180));

		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid((RelayModel.TEXTURE_LOCATION)));
		relay.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
		stack.popPose();
//		stack.mulPose(var11, 0.0F, 1.0F, 0.0F);
//		var14.renderAll(tile, null, tile.phi);
	}

    @Override
    public void render(BlockEntityRelay tile, float p_112308_, PoseStack stack, MultiBufferSource bufferSource, int light, int p_112312_) {
		if (this.doRenderModel(stack, tile))
			this.renderBlockEntityRelayAt(tile, stack, bufferSource, light);
		if (( tile).isInWorld()) {// && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(stack, bufferSource, tile, tile.getX(), tile.getY(), tile.getZ());
		}
	}

}
