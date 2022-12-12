///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft.blocks;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.EntityBlock;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//
//import net.minecraft.world.level.block.state.BlockState;
//import org.jetbrains.annotations.Nullable;
//import reika.electricraft.registry.ElectriTiles;
//
//public class BlockEUSplitter extends Block implements EntityBlock {
//
//	public BlockEUSplitter(Properties m) {
//		super(m.strength(2, 10));
//	}
//
//	@Override
//	public ItemStack getPickBlock(BlockHitResult target, Level world, int x, int y, int z)
//	{
//		return ElectriTiles.getTE(world, x, y, z).getCraftedProduct();
//	}
//
//	@Override
//	public final boolean canSilkHarvest(Level world, Player player, int x, int y, int z, int metadata)
//	{
//		return false;
//	}
//
//	@Nullable
//	@Override
//	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
//		return ElectriTiles.createTEFromIDAndMetadata(this);
//	}
//}
