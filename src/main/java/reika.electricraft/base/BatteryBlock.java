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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.registries.RegistryObject;
import reika.dragonapi.base.BlockTEBase;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
import reika.electricraft.registry.ElectriTiles;


//@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public abstract class BatteryBlock extends BlockTEBase{//} implements IWailaDataProvider {

	protected BatteryBlock(Properties prop) {
		super(prop.strength(2, 10));
	}

	public abstract ElectriTiles getTile();
	public abstract RegistryObject<Item> getItem();

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ArrayList li = new ArrayList<>();
		BatteryTile te = (BatteryTile)builder.getLevel().getBlockEntity(null); //todo NULL BLOCKPOS
		long e = te.getStoredEnergy();
		ItemStack is = this.getItem().get().getDefaultInstance();
		is.getOrCreateTag().putLong("nrg", e);
		li.add(is);
		return li;
	}

}
