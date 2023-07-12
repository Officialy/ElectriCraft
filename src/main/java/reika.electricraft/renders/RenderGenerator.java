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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import reika.electricraft.base.ElectriTERenderer;
import reika.electricraft.blockentities.BlockEntityGenerator;
import reika.electricraft.blocks.BlockElectricMachine;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriModelLayers;
import reika.rotarycraft.auxiliary.IORenderer;
import reika.rotarycraft.modinterface.model.GeneratorModel;
import reika.rotarycraft.registry.RotaryBlocks;

public class RenderGenerator extends ElectriTERenderer<BlockEntityGenerator>
{

	private final GeneratorModel generatorModel;
    public RenderGenerator(BlockEntityRendererProvider.Context context) {
        generatorModel = new GeneratorModel(context.bakeLayer(ElectriModelLayers.GENERATOR));
    }
	public void renderBlockEntityGeneratorAt(BlockEntityGenerator tile, PoseStack stack, MultiBufferSource bufferSource, int light)
	{
        BlockState blockstate = tile.getLevel() != null ? tile.getBlockState() : ElectriBlocks.GENERATOR.get().defaultBlockState().setValue(BlockElectricMachine.FACING, Direction.SOUTH);
        float f = blockstate.getValue(BlockElectricMachine.FACING).toYRot();
        stack.pushPose();
        stack.translate(0.5F, 1.5F, 0.5F);
        stack.mulPose(Axis.YP.rotationDegrees(-f));
        stack.mulPose(Axis.ZP.rotationDegrees(180));

        if (tile.isFlipped && tile.getFacing().getStepZ() != 0) {
            stack.mulPose(Axis.ZP.rotationDegrees(180));
        }
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entitySolid((GeneratorModel.TEXTURE_LOCATION)));
        generatorModel.renderToBuffer(stack, vertexconsumer, light, 0, 1, 1, 1, 1);
        stack.popPose();
//		this.closeGL(tile);
	}

    @Override
    public void render(BlockEntityGenerator tile, float p_112308_, PoseStack stack, MultiBufferSource multiBufferSource, int light, int p_112312_) {
		if (this.doRenderModel(stack, tile))
			this.renderBlockEntityGeneratorAt(tile, stack, multiBufferSource, light);
		if (tile.isInWorld()){// && MinecraftForgeClient.getRenderPass() == 1) {
			IORenderer.renderIO(stack, multiBufferSource, tile, tile.getX(), tile.getY(), tile.getZ());
		}
	}

}
