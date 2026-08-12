package reika.electricraft.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import reika.electricraft.ElectriCraft;
import reika.electricraft.registry.ElectriBlocks;
import reika.electricraft.registry.ElectriItems;

import java.util.Locale;

/**
 * en_us translations for ElectriCraft. Iterates the mod's block / item DeferredRegisters and
 * emits a humanised display name for each (e.g. {@code electriingots} → {@code "Electriingots"}).
 */
public class ElectriLang extends LanguageProvider {

    public ElectriLang(PackOutput output, String locale) {
        super(output, ElectriCraft.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("tab.electricraft", "ElectriCraft");

        ElectriBlocks.BLOCKS.getEntries().forEach(holder ->
                addBlock(holder, blockName(holder.getId().getPath())));

        ElectriItems.ITEMS.getEntries().forEach(holder ->
                addItem(holder, itemName(holder.getId().getPath())));
    }

    private static String blockName(String path) {
        if (path.startsWith("fuse_") && path.endsWith("a"))
            return path.substring(5, path.length() - 1) + " A Fuse";
        if (path.equals("rfcable"))
            return "RF Cable";
        if (path.equals("electrirfbattery"))
            return "RF Battery";
        if (path.equals("electrichargepad"))
            return "Wireless Charger";
        return prettify(path);
    }

    private static String itemName(String path) {
        return path.equals("rfbattery") ? "RF Battery" : prettify(path);
    }

    private static String prettify(String path) {
        String[] parts = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(' ');
            String p = parts[i];
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)));
            if (p.length() > 1) sb.append(p.substring(1).toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }
}
