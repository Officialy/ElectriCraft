/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.items;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import reika.dragonapi.base.BlockEntityBase;
import reika.dragonapi.libraries.level.ReikaWorldHelper;
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.electricraft.auxiliary.interfaces.BatteryTile;
import reika.electricraft.registry.BatteryType;
import reika.electricraft.registry.ElectriBlocks;

public class ItemBatteryPlacer extends Item {

    public ItemBatteryPlacer(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var side = context.getClickedFace();
        if (!ReikaWorldHelper.softBlocks(world, pos) && ReikaWorldHelper.getMapColor(world, pos) != MapColor.WATER && ReikaWorldHelper.getMapColor(world, pos) != MapColor.FIRE) {  //todo was lava
           /*todo if (side == 0)
                --y;
            if (side == 1)
                ++y;
            if (side == 2)
                --z;
            if (side == 3)
                ++z;
            if (side == 4)
                --x;
            if (side == 5)
                ++x;*/
            if (!ReikaWorldHelper.softBlocks(world, pos) && ReikaWorldHelper.getMapColor(world, pos) != MapColor.WATER && ReikaWorldHelper.getMapColor(world, pos) != MapColor.FIRE) //todo was lava
                return InteractionResult.FAIL;
        }
        if (!this.checkValidBounds(context.getItemInHand(), context.getPlayer(), world, pos))
            return InteractionResult.FAIL;
        AABB box = new AABB(pos, pos.offset(1,1,1));
        List<LivingEntity> inblock = world.getEntitiesOfClass(LivingEntity.class, box);
        if (!inblock.isEmpty())
            return InteractionResult.FAIL;
//todo        if (!ep.InteractionResult.FAIL(x, y, z, 0, is))
//            return false;
        else {
//    todo        if (!ep.isCreative())
//                --is.getCount();
            world.setBlock(pos, this.getPlacingBlock().defaultBlockState(), /*this.getPlacingMeta(is),*/ 3);
        }
        world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, /*todo ElectriTiles.BATTERY.getPlaceSound()*/ null, SoundSource.BLOCKS, 1F, 1.5F, false);
        BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);
        te.setPlacer(context.getPlayer());
        //te.setBlockMetadata(RotaryAux.get4SidedMetadataFromPlayerLook(ep));
        ((BatteryTile) te).setEnergyFromNBT(context.getItemInHand());

        return InteractionResult.SUCCESS;
    }

    protected Block getPlacingBlock() {
        return ElectriBlocks.BATTERY.get();
    }


/*	@Override
	public void getSubItems(Item par1, CreativeModeTab par2CreativeTabs, List par3List) {
		for (int i = 0; i < BatteryType.batteryList.length; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
		ItemStack item = new ItemStack(par1, 1);//todo, BatteryType.STAR.ordinal());
		item.getOrCreateTag().putLong("nrg", BatteryType.STAR.maxCapacity);
		par3List.add(item);
	}*/

    @Override
    public void appendHoverText(ItemStack is, @Nullable Level p_41422_, List<Component> li, TooltipFlag p_41424_) {
        long e = 0;
        if (is.getTag() != null) {
            e = is.getTag().getLong("nrg");
        }
        BatteryType bat = BatteryType.batteryList[1]; //old meta
        long max = bat.maxCapacity;
        String sg = ReikaEngLibrary.getSIPrefix(e);
        String sg2 = ReikaEngLibrary.getSIPrefix(max);
        double b = ReikaMathLibrary.getThousandBase(e);
        double b2 = ReikaMathLibrary.getThousandBase(max);
        li.add(Component.literal(String.format("Stored Energy: %.1f %sJ/%.1f %sJ", b, sg, b2, sg2)));
        int a = bat.outputCurrent;
        int v = bat.outputVoltage;
        long power = (long) a * (long) v;

        String ps = ReikaEngLibrary.getSIPrefix(power);
        double p = ReikaMathLibrary.getThousandBase(power);
        li.add(Component.literal(String.format("Emits %dA at %dV (%.3f%sW)", a, v, p, ps)));
    }

    protected boolean checkValidBounds(ItemStack is, Player ep, Level world, BlockPos pos) {
        return pos.getY() > 0 && pos.getY() < world.getHeight() - 1;
    }

/*	@Override
	public final String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return super.getUnlocalizedName() + "." + String.valueOf(d);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ElectriItems ir = ElectriItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}*/


}
