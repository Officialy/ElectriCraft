///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft;
//
//import org.lwjgl.opengl.GL11;
//
//import net.minecraft.client.renderer.RenderBlocks;
//import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.client.IItemRenderer;
//
//import reika.dragonapi.libraries.io.ReikaTextureHelper;
//import reika.electricraft.auxiliary.interfaces.BatteryTile;
//import reika.electricraft.registry.ElectriTiles;
//import reika.electricraft.registry.WireType;
//import reika.electricraft.tileentities.BlockEntityWire;
//
//public class ElectriItemRenderer implements IItemRenderer {
//
//
//	public ElectriItemRenderer() {
//
//	}
//
//	@Override
//	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
//		return true;
//	}
//
//	@Override
//	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
//		return true;
//	}
//
//	@Override
//	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
//		float a = 0; float b = 0;
//
//		RenderBlocks rb = (RenderBlocks)data[0];
//		if (type == type.ENTITY) {
//			a = -0.5F;
//			b = -0.5F;
//			GL11.glScalef(0.5F, 0.5F, 0.5F);
//		}
//		ElectriTiles machine = ElectriTiles.getMachine(item);
//		if (machine == ElectriTiles.WIRE) {
//			BlockEntityWire wire = (BlockEntityWire)ElectriTiles.WIRE.createTEInstanceForRender();
//			wire.insulated = item.getItemDamage() >= WireType.INS_OFFSET;
//			wire.setBlockMetadata(item.getItemDamage()%WireType.INS_OFFSET);
//			BlockEntityRendererDispatcher.instance.renderBlockEntityAt(wire, a, -0.1D, b, 0.0F);
//		}
//		else if (machine == ElectriTiles.RFBATTERY || machine == ElectriTiles.EUBATTERY) {
//			BatteryTile te = (BatteryTile)machine.createTEInstanceForRender();
//			ReikaTextureHelper.bindTerrainTexture();
//			rb.renderBlockAsItem(machine.get(), item.getItemDamage(), 1);
//			te.setEnergyFromNBT(item);
//			BlockEntityRendererDispatcher.instance.renderBlockEntityAt((BlockEntity)te, -0.5, -0.5, -0.5, 0.0F);
//		}
//		else if (machine.hasRender())
//			BlockEntityRendererDispatcher.instance.renderBlockEntityAt(machine.createTEInstanceForRender(), a, -0.1D, b, 0.0F);
//		else {
//			ReikaTextureHelper.bindTerrainTexture();
//			rb.renderBlockAsItem(machine.get(), item.getItemDamage(), 1);
//		}
//	}
//}
