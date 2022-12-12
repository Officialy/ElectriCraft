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

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


import net.minecraft.world.level.Level;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.ElectriItemBase;

public class ItemElectriBook extends ElectriItemBase {

    public ItemElectriBook(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player ep, InteractionHand p_41434_) {
//        ep.openMenu(ElectriCraft.instance, 10, level, 0, 0, 0);
        return InteractionResultHolder.pass(this.getDefaultInstance());
    }

}
