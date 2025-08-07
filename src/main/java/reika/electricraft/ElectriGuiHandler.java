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
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//import reika.dragonapi.base.CoreMenu;
//import reika.electricraft.guis.GuiRFCable;
//import reika.electricraft.guis.GuiTransformer;
//import reika.electricraft.tileentities.BlockEntityTransformer;
//import reika.electricraft.tileentities.ModInterface.BlockEntityRFCable;
//
//public class ElectriGuiHandler implements IGuiHandler {
//
//	@Override
//	public Object getServerGuiElement(int ID, Player player, Level world, int x, int y, int z) {
//		BlockEntity te = world.getBlockEntity(new BlockPos(x, y, z));
//		if (ID == 10) {
//			return null;
//		}
//		if (te instanceof BlockEntityRFCable) {
//			return new CoreMenu(player, te);
//		}
//		if (te instanceof BlockEntityTransformer) {
//			return new CoreMenu(player, te);
//		}
//		return null;
//	}
//
//	@Override
//	public Object getClientGuiElement(int ID, Player player, Level world, int x, int y, int z) {
//		if (ID == 10) {
//			return new GuiElectriBook(player, world, 0, 0);
//		}
//		BlockEntity te = world.getBlockEntity(new BlockPos(x, y, z));
//		if (te instanceof BlockEntityRFCable) {
//			return new GuiRFCable(player, (BlockEntityRFCable)te);
//		}
//		if (te instanceof BlockEntityTransformer) {
//			return new GuiTransformer(player, (BlockEntityTransformer)te);
//		}
//		return null;
//	}
//
//}
