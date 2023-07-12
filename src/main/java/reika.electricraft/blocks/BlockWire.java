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
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.ReikaEntityHelper;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;
import reika.electricraft.network.WireNetwork;
import reika.electricraft.registry.WireType;
import reika.electricraft.blockentities.BlockEntityWire;
import reika.rotarycraft.RotaryCraft;
import reika.rotarycraft.api.interfaces.Fillable;
import reika.rotarycraft.entities.EntityDischarge;
import reika.rotarycraft.registry.SoundRegistry;

//@Strippable(value = {"mcp.mobius.waila.api.IWailaDataProvider"})
public class BlockWire extends ElectriBlock {//implements IWailaDataProvider {

	private static final int nWires = ReikaJavaLibrary.getEnumLengthWithoutInitializing(WireType.class);

	public BlockWire(Properties properties) {
		super(properties);
		//this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide() ? null : ((pLevel1, pPos, pState1, pBlockEntity) -> {
			((BlockEntityWire) pBlockEntity).updateEntity(pLevel1, pPos);
		});
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BlockEntityWire(pPos, pState);
	}

/*	@Override
	public boolean removedByPlayer(Level world, Player player, int x, int y, int z, boolean harv)
	{
		if (!player.isCreative() && this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, world.getBlockMetadata(x, y, z));
		return world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
	}

	@Override
	public void harvestBlock(Level world, Player ep, int x, int y, int z, int meta) {
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		if (world.getBlock(x, y, z) == this)
			ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, this.getDrops(world, pos, 0));
	}*/

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
		ArrayList<ItemStack> li = new ArrayList<>();
		BlockEntityWire te = (BlockEntityWire)context.getLevel().getBlockEntity(null); //todo NULL BLOCKPOS
		ItemStack is = te.insulated ? te.getWireType().getCraftedInsulatedProduct() : te.getWireType().getCraftedProduct();
		if (/*todo is.getItemDamage()%*/WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
			is.getOrCreateTag().putBoolean("fluid", true);
			is.getOrCreateTag().putInt("lvl", ((Fillable)is.getItem()).getCapacity(is));
		}
		li.add(is);
		return li;
	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);
		BlockEntityWire te = (BlockEntityWire)level.getBlockEntity(pos);
		te.recomputeConnections((Level) level, pos);
		te.checkRiftConnections();
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
		BlockEntityWire te = (BlockEntityWire)world.getBlockEntity(pos);
		te.addToAdjacentConnections(world, pos);
		te.recomputeConnections(world, pos);
	}


//	@Override
	public final AABB getCollisionBoundingBoxFromPool(Level world, int x, int y, int z) {
		double d = 0.25;
		BlockEntityWire te = (BlockEntityWire)world.getBlockEntity(new BlockPos(x, y, z));
		if (te == null)
			return null;
		float minx = te.isConnectedOnSideAt(world, x, y, z, Direction.WEST) ? 0 : 0.3F;
		float maxx = te.isConnectedOnSideAt(world, x, y, z, Direction.EAST) ? 1 : 0.7F;
		float minz = te.isConnectedOnSideAt(world, x, y, z, Direction.SOUTH) ? 0 : 0.3F;
		float maxz = te.isConnectedOnSideAt(world, x, y, z, Direction.NORTH) ? 1 : 0.7F;
		float miny = te.isConnectedOnSideAt(world, x, y, z, Direction.UP) ? 0 : 0.3F;
		float maxy = te.isConnectedOnSideAt(world, x, y, z, Direction.DOWN) ? 1 : 0.7F;
		AABB box = new AABB(x+minx, y+miny, z+minz, x+maxx, y+maxy, z+maxz);
//		this.setBounds(box, x, y, z);
		return box;
	}

	@Override
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity e) {
		if (!world.isClientSide()) {
			if (!(e instanceof Player) || (e instanceof Player && !((Player)e).isCreative())) {
				BlockEntityWire te = (BlockEntityWire)world.getBlockEntity(pos);
				WireNetwork net = te.getNetwork();
				if (!te.insulated && net != null) {
					if (net.isLive()) {
						int v = net.getPointVoltage(te);
						EntityDischarge ed = new EntityDischarge(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, v, e.blockPosition().getX(), e.blockPosition().getY(), e.blockPosition().getZ());
						world.addFreshEntity(ed);
						if (!(e instanceof LivingEntity) || !ReikaEntityHelper.isEntityWearingFullSuitOf((LivingEntity)e, ArmorMaterials.CHAIN)) {
							RotaryCraft.shock.lastMachine = te;
							e.hurt(RotaryCraft.shock, v > 10000 ? 20 : v > 1000 ? 10 : v > 100 ? 5 : v > 10 ? 1 : 0);
						}
						if (e instanceof Creeper) {
							world.explode(e, e.getX(), e.getY(), e.getZ(), 3F, Level.ExplosionInteraction.BLOCK);
							e.hurt(e.damageSources().magic(), Integer.MAX_VALUE);
						}
						if (v > 100) {
							e.setDeltaMovement((e.getX()-pos.getX())/4D, 0.33, (e.getZ()-pos.getZ())/4D);
						}
						SoundRegistry.SPARK.playSoundAtBlock(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5F, 1F);
						te.getNetwork().shortNetwork();
					}
				}
			}
		}	}
	public ItemStack getPickBlock(BlockHitResult target, Level world, int x, int y, int z)
	{
		BlockEntityWire te = (BlockEntityWire)world.getBlockEntity(new BlockPos(x, y, z));
		ItemStack is = te.insulated ? te.getWireType().getCraftedInsulatedProduct() : te.getWireType().getCraftedProduct();
		if (te.getWireType() == WireType.SUPERCONDUCTOR) {
			is.getOrCreateTag().putBoolean("fluid", true);
			is.getOrCreateTag().putInt("lvl", 25);
		}
		return is;
	}
}
