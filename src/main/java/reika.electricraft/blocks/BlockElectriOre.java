/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.blocks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;


import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import reika.electricraft.registry.ElectriOres;

public class BlockElectriOre extends DropExperienceBlock {

	public BlockElectriOre(Properties properties) {
		super(properties);
//		this.setCreativeTab(ElectriCraft.tabElectri);
	}

	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
		ArrayList<ItemStack> li = new ArrayList<>();
		//ItemStack is = new ItemStack(ElectriBlocks.ORE.getBlock(), 1, metadata);
		ElectriOres ore = ElectriOres.getOre(this);
		li.addAll(ore.getOreDrop());
		return li;
	}

	public ItemStack getPickBlock(Level world, BlockPos pos)
	{
		ElectriOres ore = ElectriOres.getOre(world, pos);
		return ore.getOreBlock();
	}

}
