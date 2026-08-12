package reika.electricraft.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * Block item shared by the eighteen concrete wire registrations. Material and insulation live in
 * the registry identity, not stack NBT, so ordinary vanilla recipe/loot matching remains exact.
 */
public class ItemWire extends BlockItem {

    public ItemWire(Block block, Properties properties) {
        super(block, properties);
    }

}
