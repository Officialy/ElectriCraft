/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.auxiliary;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import reika.dragonapi.instantiable.io.XMLInterface;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;
import reika.electricraft.network.WireNetwork;
import reika.electricraft.registry.BatteryType;
import reika.electricraft.registry.ElectriBook;
import reika.electricraft.registry.ElectriTiles;
import reika.electricraft.registry.WireType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Loads the ElectriCraft handbook text from the {@code assets/electricraft/resources/*.xml} files
 * (category blurbs, per-machine descriptions/notes, and the info pages), formats the numeric data
 * into them, and hands finished strings to {@link ElectriBook}. Ported 1:1 from the 1.7.10 loader,
 * matching the modernised RotaryDescriptions XMLInterface pattern.
 */
public final class ElectriDescriptions {

    private static final String RESOURCE_ROOT = "/assets/electricraft/resources/";
    public static final String DESC_SUFFIX = ":desc";
    public static final String NOTE_SUFFIX = ":note";

    private static String PARENT = getParent(true);

    private static final HashMap<ElectriBook, String> data = new HashMap<>();
    private static final HashMap<ElectriBook, String> notes = new HashMap<>();

    private static final HashMap<ElectriTiles, Object[]> machineData = new HashMap<>();
    private static final HashMap<ElectriTiles, Object[]> machineNotes = new HashMap<>();
    private static final HashMap<ElectriBook, Object[]> miscData = new HashMap<>();

    private static XMLInterface parents;
    private static XMLInterface machines;
    private static XMLInterface infos;
    private static boolean loaded;

    private static XMLInterface loadData(String name) {
        XMLInterface xml = new XMLInterface(ElectriCraft.class, PARENT + name + ".xml", false);
        xml.setFallback(getParent(false) + name + ".xml");
        xml.init();
        return xml;
    }

    private static void reloadData() {
        parents = loadData("categories");
        machines = loadData("machines");
        infos = loadData("info");
    }
    private static String getParent(boolean locale) {
        return locale && FMLEnvironment.getDist() == Dist.CLIENT ? getLocalizedParent() : RESOURCE_ROOT;
    }

    private static String getLocalizedParent() {
        String lang = Minecraft.getInstance().getLanguageManager().getSelected();
        if (!"en_us".equals(lang) && hasLocalizedFor(lang))
            return RESOURCE_ROOT + lang + "/";
        return RESOURCE_ROOT;
    }

    private static boolean hasLocalizedFor(String lang) {
        try (InputStream o = ElectriCraft.class.getResourceAsStream(RESOURCE_ROOT + lang + "/categories.xml")) {
            return o != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getTOC() {
        List<ElectriBook> toctabs = ElectriBook.getTOCTabs();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < toctabs.size(); i++) {
            ElectriBook h = toctabs.get(i);
            sb.append("Page ");
            sb.append(h.getScreen());
            sb.append(" - ");
            sb.append(h.getTitle());
            if (i < toctabs.size() - 1)
                sb.append("\n");
        }
        return sb.toString();
    }

    private static void addData(ElectriTiles m, Object... d) {
        machineData.put(m, d);
    }

    private static void addData(ElectriBook h, Object... d) {
        miscData.put(h, d);
    }

    private static void addNotes(ElectriTiles m, Object... d) {
        machineNotes.put(m, d);
    }

    public static void reload() {
        PARENT = getParent(true);

        data.clear();
        notes.clear();
        machineData.clear();
        machineNotes.clear();
        miscData.clear();

        reloadData();
        loadNumericalData();
        loadData();

        loaded = true;
    }

    private static void ensureLoaded() {
        if (!loaded)
            reload();
    }

    private static void addEntry(ElectriBook h, String sg) {
        data.put(h, sg);
    }

    public static void loadData() {
        if (parents == null || machines == null || infos == null)
            reloadData();

        List<ElectriBook> parenttabs = ElectriBook.getCategoryTabs();
        List<ElectriBook> machinetabs = ElectriBook.getMachineTabs();
        ElectriBook[] infotabs = ElectriBook.getInfoTabs();

        for (ElectriBook h : parenttabs) {
            String desc = parents.getValueAtNode("categories:" + h.name().toLowerCase(Locale.ENGLISH));
            addEntry(h, desc);
        }

        for (ElectriBook h : machinetabs) {
            ElectriTiles m = h.getMachine();
            String node = "machines:" + m.name().toLowerCase(Locale.ENGLISH);
            String desc = machines.getValueAtNode(node + DESC_SUFFIX);
            String aux = machines.getValueAtNode(node + NOTE_SUFFIX);
            desc = format(desc, machineData.get(m));
            aux = format(aux, machineNotes.get(m));

            if (XMLInterface.NULL_VALUE.equals(desc))
                desc = "There is no handbook data for this machine yet.";

            addEntry(h, desc);
            notes.put(h, aux);
        }

        for (ElectriBook h : infotabs) {
            String desc = infos.getValueAtNode("info:" + h.name().toLowerCase(Locale.ENGLISH));
            desc = format(desc, miscData.get(h));
            addEntry(h, desc);
        }
    }

    /** Guards against a null/unmapped value and a mismatched format string. */
    private static String format(String base, Object[] args) {
        if (base == null || XMLInterface.NULL_VALUE.equals(base))
            return base;
        if (args == null || args.length == 0)
            return base;
        try {
            return String.format(base, args);
        } catch (Exception e) {
            return base;
        }
    }

    public static String getData(ElectriBook h) {
        ensureLoaded();
        return data.getOrDefault(h, "");
    }

    public static String getNotes(ElectriBook h) {
        ensureLoaded();
        return notes.getOrDefault(h, "");
    }

    /** (Re)parses the handbook XML on client resource reload, so language changes pick up new text. */
    public static class ReloadListener implements net.minecraft.server.packs.resources.ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(net.minecraft.server.packs.resources.ResourceManager manager) {
            ElectriDescriptions.reload();
        }
    }

    private static void loadNumericalData() {
        addData(ElectriBook.LIMITS, WireType.getLimitsForDisplay());
        addNotes(ElectriTiles.GENERATOR, WireNetwork.TORQUE_PER_AMP, WireNetwork.TORQUE_PER_AMP);
        addData(ElectriTiles.TRANSFORMER, BlockEntityTransformer.MAXTEMP, BlockEntityTransformer.MAXCURRENT);
        addData(ElectriTiles.RFBATTERY, BlockEntityRFBattery.CAPACITY);
        addNotes(ElectriTiles.BATTERY, BatteryType.getDataForDisplay());
        addNotes(ElectriTiles.WIRELESSPAD, BlockEntityWirelessCharger.ChargerTiers.getDataForDisplay());
    }
}
