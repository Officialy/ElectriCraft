package reika.electricraft.items;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.registry.WireType;

/**
 * The wire BlockItem: displays the type + insulation carried in the stack tag (all wire variants
 * share one item; 1.7.10 used metadata + per-meta names).
 */
public class ItemWire extends BlockItem {

    public ItemWire(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        WireType type = WireType.TIN;
        boolean insulated = false;
        if (ReikaItemHelper.hasStackTag(stack)) {
            var tag = ReikaItemHelper.getStackTag(stack);
            type = WireType.wireList[tag.getIntOr("wtype", WireType.TIN.ordinal()) % WireType.wireList.length];
            insulated = tag.getBooleanOr("insul", false);
        }
        String n = type.name().charAt(0) + type.name().substring(1).toLowerCase(Locale.ROOT);
        return Component.literal(insulated ? "Insulated " + n + " Wire" : n + " Wire");
    }

}
