package reika.electricraft.base;


import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import reika.electricraft.auxiliary.BatteryTracker;
import reika.electricraft.auxiliary.interfaces.BatteryTile;

public abstract class BatteryTileBase extends ElectriBlockEntity implements BatteryTile {

	private final BatteryTracker tracker = new BatteryTracker();

	public BatteryTileBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void updateEntity(Level world, int x, int y, int z) {
		tracker.update(this);
	}

	@Override
	public final BatteryTracker getTracker() {
		return tracker;
	}

	@Override
	public final int getRedstoneOverride() {
		//return (int)Math.round(15D*ReikaMathLibrary.logbase(this.getStoredEnergy(), 2)/ReikaMathLibrary.logbase(this.getMaxEnergy(), 2));
		return (int)Math.round(15D*Math.pow(this.getStoredEnergy()/(double)this.getMaxEnergy(), 0.1));
	}

	public final void setEnergyFromNBT(ItemStack is) {
		if (is.getItem() == this.getPlacerItem()) {
			if (is.getTag() != null)
				this.setEnergy(is.getTag().getLong("nrg"));
			else
				this.setEnergy(0);
		}
		else {
			this.setEnergy(0);
		}
	}

	protected abstract Item getPlacerItem();

	protected abstract void setEnergy(long val);
}
