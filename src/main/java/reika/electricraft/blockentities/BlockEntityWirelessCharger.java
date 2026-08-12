/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.blockentities;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import reika.electricraft.base.ElectriBlock;
import reika.electricraft.base.ElectriBlockEntity;
import reika.electricraft.registry.ElectriBlockEntities;
import reika.electricraft.registry.ElectriTiles;

import java.util.ArrayList;
import java.util.Locale;

public class BlockEntityWirelessCharger extends ElectriBlockEntity {
	public BlockEntityWirelessCharger(BlockPos pos, BlockState state) {
		super(ElectriBlockEntities.WIRELESS_CHARGER.get(), pos, state);
	}

	//private Direction facing = Direction.UP;

	public Direction getFacing() {
		//return facing != null ? facing : Direction.UP;
		return switch (getBlockState().getValue(reika.electricraft.blocks.BlockChargePad.FACING)) {
			case WEST -> Direction.WEST;
			case EAST -> Direction.EAST;
			case NORTH -> Direction.NORTH;
			case SOUTH -> Direction.SOUTH;
			case UP -> Direction.UP;
			case DOWN -> Direction.DOWN;
		};
	}

	private EnergyHandler getTargetHandler() {
		//Bridges FE two blocks ahead of the facing; modern machines only expose capabilities.
		Direction dir = this.getFacing();
		BlockPos target = worldPosition.relative(dir, 2);
		return level == null ? null : level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, target, dir.getOpposite());
	}

	//1.7.10 stored the tier as block metadata; the port stores it on the BE, set at placement
	//from the item's "tier" stack tag.
	private int tier;

	public ChargerTiers getTier() {
		return ChargerTiers.tierList[tier % ChargerTiers.tierList.length];
	}

	public void setTier(int t) {
		tier = Math.floorMod(t, ChargerTiers.tierList.length);
	}

	@Override
	public ElectriTiles getMachine() {
		return ElectriTiles.WIRELESSPAD;
	}

	@Override
	public void updateEntity(Level world, BlockPos pos) {
	    /* 26.1-lifecycle */ super.updateEntity(); // 26.1: drive BlockEntityBase lifecycle (ticksExisted++, onFirstTick → recompute/sync).

	}

	@Override
	protected void animateWithTick(Level world, BlockPos pos) {

	}

	@Override
	protected void writeSyncTag(CompoundTag NBT) {
		super.writeSyncTag(NBT);

		NBT.putInt("tier", tier);
	}

	@Override
	protected void readSyncTag(CompoundTag NBT) {
		super.readSyncTag(NBT);

		tier = NBT.getIntOr("tier", 0);
	}

	public boolean canConnectEnergy(Direction from) {
		return from != this.getFacing(); //legacy: no connection on the beam face
	}

	/**
	 * The FE face of the pad: relays inserted energy to the block two ahead of the facing, taxed
	 * by tier efficiency and capped by tier throughput. Fully transactional — the relayed insert
	 * happens inside the caller's transaction.
	 */
	private final EnergyHandler energyView = new EnergyHandler() {
		@Override
		public long getAmountAsLong() {
			EnergyHandler target = getTargetHandler();
			return target != null ? target.getAmountAsLong() : 0;
		}

		@Override
		public long getCapacityAsLong() {
			EnergyHandler target = getTargetHandler();
			return target != null ? target.getCapacityAsLong() : 0;
		}

		@Override
		public int insert(int amt, TransactionContext tx) {
			EnergyHandler target = getTargetHandler();
			if (target == null)
				return 0;
			float f = getTier().efficiency;
			amt = Math.min(amt, getTier().maxThroughput);
			return (int)(target.insert((int)(amt * f), tx) / f);
		}

		@Override
		public int extract(int amt, TransactionContext tx) {
			return 0;
		}
	};

	public EnergyHandler getEnergyView() {
		return energyView;
	}

	@Override
	public ArrayList<String> getMessages(Level world, BlockPos pos, Direction side) {
		return null;
	}

	public enum ChargerTiers {
		BASIC(0.4F, 80),
		IMPROVED(0.6F, 800),
		ADVANCED(0.8F, 6000),
		HIGHTECH(0.9F, 40000),
		SUPERCONDUCTING(1F, Integer.MAX_VALUE);

		public final float efficiency;
		public final int maxThroughput;

		public static final ChargerTiers[] tierList = values();

		ChargerTiers(float f, int th) {
			efficiency = f;
			maxThroughput = th;
		}

		public String getLocalizedName() {
			return I18n.get("electrichargepad."+this.name().toLowerCase(Locale.ENGLISH));
		}

		public static String getDataForDisplay() {
			StringBuilder sb = new StringBuilder();
			for (ChargerTiers type : tierList) {
				if (type == ChargerTiers.SUPERCONDUCTING)
					sb.append(type.getLocalizedName()).append(": No limit @ 100% efficiency");
				else
					sb.append(String.format("%s: Max %d RF/t @ %.0f%% efficiency", type.getLocalizedName(), type.maxThroughput, 100 * type.efficiency));
				sb.append("\n\n");
			}
			return sb.toString();
		}
	}

}
