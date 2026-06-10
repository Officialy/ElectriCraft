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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import reika.electricraft.base.ElectriItemBase;

// 1.21.5: InteractionResultHolder was removed; Item#use now returns InteractionResult.
public class ItemElectriBook extends ElectriItemBase {

    public ItemElectriBook(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player ep, InteractionHand hand) {
        return InteractionResult.PASS;
    }
}
