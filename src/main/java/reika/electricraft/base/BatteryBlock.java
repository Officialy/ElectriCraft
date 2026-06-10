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
import net.minecraft.world.level.storage.loot.LootParams;
import net.neoforged.neoforge.registries.DeferredHolder;
import reika.dragonapi.base.BlockTEBase;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
import reika.electricraft.registry.ElectriTiles;


// 1.21.5: RegistryObject → DeferredHolder<Registry, Type>. ItemStack#getOrCreateTag was removed
// in favour of the CUSTOM_DATA component; persist via ReikaItemHelper.updateStackTag.
public abstract class BatteryBlock extends BlockTEBase{

	protected BatteryBlock(Properties prop) {
		super(prop.strength(2, 10));
	}

	public abstract ElectriTiles getTile();
	public abstract DeferredHolder<Item, ? extends Item> getItem();

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		// 26.1 fix: previously called {@code builder.getLevel().getBlockEntity(null)} — that's
		// an instant NPE when any battery block is broken (the {@code null} BlockPos parameter
		// is dereferenced inside vanilla's chunk lookup). The vanilla loot-context system
		// passes BLOCK_ENTITY as a context parameter for block drops; grab it from there
		// instead. Falls back gracefully if the BE entry isn't set (shouldn't happen for a
		// block-broken loot context, but defensive).
		ArrayList<ItemStack> li = new ArrayList<>();
		net.minecraft.world.level.block.entity.BlockEntity raw = builder.getOptionalParameter(
				net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY);
		ItemStack is = this.getItem().get().getDefaultInstance();
		final long e = (raw instanceof BatteryTile te) ? te.getStoredEnergy() : 0L;
		reika.dragonapi.libraries.registry.ReikaItemHelper.updateStackTag(is, __T__ -> __T__.putLong("nrg", e));
		li.add(is);
		return li;
	}

}
