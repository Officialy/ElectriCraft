/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.base.BlockTEBase;

public abstract class NetworkBlock extends BlockTEBase {

	public NetworkBlock(Properties properties) {
		super(properties.strength(2, 10));
	}

	@Override
	public void destroy(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
		NetworkBlockEntity te = (NetworkBlockEntity)levelAccessor.getBlockEntity(pos);
		if (!levelAccessor.isClientSide() && te != null)
			te.removeFromNetwork();
		super.destroy(levelAccessor, pos, state);
	}
}
