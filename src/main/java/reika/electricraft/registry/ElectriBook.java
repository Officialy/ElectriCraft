/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.auxiliary.ElectriBookData;
import reika.electricraft.auxiliary.ElectriDescriptions;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import reika.rotarycraft.auxiliary.interfaces.HandbookEntry;
import reika.rotarycraft.gui.screen.GuiHandbook;

public enum ElectriBook implements HandbookEntry {

    //---------------------TOC--------------------//
    TOC("Table Of Contents", ""),
    INFO("Info", ElectriItems.BOOK.get()),
    CONVERSION("Conversion", ElectriTiles.GENERATOR.getCraftedProduct()),
    TRANSPORT("Transport", WireType.SUPERCONDUCTOR.getCraftedProduct()),
    STORAGE("Storage", BatteryType.STAR.getCraftedProduct()),
    UTILITY("Utility", ElectriTiles.TRANSFORMER.getCraftedProduct()),
    MODINTERFACE("Mod Interaction", ElectriTiles.RF_CABLE.getCraftedProduct()),
    //---------------------INFO--------------------//
    INTRO("Introduction", ""),
    PHYSICS("Electric Physics", Items.BOOK),
    SOURCESINK("Sources and Sinks", WireType.GOLD.getCraftedInsulatedProduct()),
    NETWORKS("Electric Networks", WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct()),
    LIMITS("Limits", WireType.COPPER.getCraftedProduct()),

    //--------------------PROCESSING---------------//
    CONVDESC("Conversion Machines", ""),
    GENERATOR(ElectriTiles.GENERATOR),
    MOTOR(ElectriTiles.MOTOR),

    TRANSDESC("Electrical Transport", ""),
    WIRES(ElectriTiles.WIRE),
    RELAY(ElectriTiles.RELAY),
    RESISTOR(ElectriTiles.RESISTOR),
    PRECISERESISTOR(ElectriTiles.PRECISERESISTOR),

    STORAGEDESC("Electrical Storage", ""),
    BATTERY(ElectriTiles.BATTERY),

    UTILDESC("Utility Blocks", ""),
    TRANSFORMER(ElectriTiles.TRANSFORMER),
    METER(ElectriTiles.METER),

    MODDESC("Mod Interaction Devices", ""),
    RFBATT(ElectriTiles.RFBATTERY),
    RFCABLE(ElectriTiles.RF_CABLE),
    // EU-PORT: the EU splitter/battery need IC2, which has no NeoForge port.
    //EUSPLITTER(ElectriTiles.EUSPLIT),
    //EUBATT(ElectriTiles.EUBATTERY),
    WIRELESSPAD(ElectriTiles.WIRELESSPAD);

    private final ItemStack iconItem;
    private final String pageTitle;
    private boolean isParent = false;
    private ElectriTiles machine;

    public static final ElectriBook[] tabList = values();

    ElectriBook(ElectriTiles r) {
        this(r.getName(), r.getCraftedProduct());
        machine = r;
    }

    ElectriBook(String name, String s) {
        this(name);
        isParent = true;
    }

    ElectriBook(String name) {
        this(name, (ItemStack) null);
    }

    ElectriBook(String name, Item icon) {
        this(name, new ItemStack(icon));
    }

    ElectriBook(String name, Block icon) {
        this(name, new ItemStack(icon));
    }

    ElectriBook(String name, ItemStack icon) {
        iconItem = icon;
        pageTitle = name;
    }

    public static ElectriBook getFromScreenAndPage(int screen, int page) {
        if (screen < INTRO.getScreen())
            return TOC;
        ElectriBook h = ElectriBookData.getMapping(screen, page);
        return h != null ? h : TOC;
    }

    public static List<ElectriBook> getEntriesForScreen(int screen) {
        List<ElectriBook> li = new ArrayList<>();
        for (ElectriBook h : tabList) {
            if (h.getScreen() == screen)
                li.add(h);
        }
        return li;
    }

    public static List<ElectriBook> getTOCTabs() {
        ArrayList<ElectriBook> li = new ArrayList<>();
        for (ElectriBook h : tabList) {
            if (h.isParent && h != TOC)
                li.add(h);
        }
        return li;
    }

    public static List<ElectriBook> getMachineTabs() {
        List<ElectriBook> tabs = new ArrayList<>();
        for (ElectriBook h : tabList) {
            if (h.isMachine() && !h.isParent)
                tabs.add(h);
        }
        return tabs;
    }

    public static ElectriBook[] getInfoTabs() {
        int size = CONVDESC.ordinal() - INTRO.ordinal() - 1;
        ElectriBook[] tabs = new ElectriBook[size];
        System.arraycopy(tabList, INTRO.ordinal() + 1, tabs, 0, size);
        return tabs;
    }

    public static List<ElectriBook> getCategoryTabs() {
        ArrayList<ElectriBook> li = new ArrayList<>();
        for (ElectriBook h : tabList) {
            if (h.isParent && h != TOC)
                li.add(h);
        }
        return li;
    }

    public boolean isMachine() {
        return machine != null;
    }

    public ElectriTiles getMachine() {
        return machine;
    }

    @Override
    public ItemStack getTabIcon() {
        return iconItem;
    }

    @Override
    public String getData() {
        if (this == TOC)
            return ElectriDescriptions.getTOC();
        return ElectriDescriptions.getData(this);
    }

    @Override
    public String getNotes(int subpage) {
        return ElectriDescriptions.getNotes(this);
    }

    @Override
    public boolean sameTextAllSubpages() {
        return false;
    }

    @Override
    public String getTitle() {
        if (this == WIRES)
            return "Wires";
        if (this == BATTERY)
            return "Batteries";
        return pageTitle;
    }

    @Override
    public boolean hasMachineRender() {
        return this.isMachine();
    }

    @Override
    public boolean hasSubpages() {
        return this.isMachine();
    }

    public int getRelativeScreen() {
        int offset = this.ordinal() - this.getParent().ordinal();
        return offset / GuiHandbook.PAGES_PER_SCREEN;
    }

    public ElectriBook getParent() {
        ElectriBook parent = null;
        for (ElectriBook h : tabList) {
            if (h.isParent && this.ordinal() >= h.ordinal())
                parent = h;
        }
        return parent;
    }

    public boolean isParent() {
        return isParent;
    }

    public int getBaseScreen() {
        int sc = 0;
        for (int i = 0; i < this.ordinal(); i++) {
            ElectriBook h = tabList[i];
            if (h.isParent)
                sc += h.getNumberChildren() / GuiHandbook.PAGES_PER_SCREEN + 1;
        }
        return sc;
    }

    public int getNumberChildren() {
        if (!isParent)
            return 0;
        int ch = 0;
        for (int i = this.ordinal() + 1; i < tabList.length; i++) {
            if (tabList[i].isParent)
                return ch;
            ch++;
        }
        return ch;
    }

    public int getRelativePage() {
        return this.ordinal() - this.getParent().ordinal();
    }

    public int getRelativeTabPosn() {
        int offset = this.ordinal() - this.getParent().ordinal();
        return offset - this.getRelativeScreen() * GuiHandbook.PAGES_PER_SCREEN;
    }

    /** u-offset into the shared tab-button texture (RC handbook convention). */
    public int getTabColumn() {
        return (this.getRelativePage() % 12) * 20;
    }

    /** v-offset into the shared tab-button texture. */
    public int getTabRow() {
        return (this.getRelativePage() / 12) * 20;
    }

    @Override
    public int getScreen() {
        return this.getParent().getBaseScreen() + this.getRelativeScreen();
    }

    @Override
    public int getPage() {
        return (this.ordinal() - this.getParent().ordinal()) % GuiHandbook.PAGES_PER_SCREEN;
    }

    @Override
    public boolean isConfigDisabled() {
        return false;
    }

    /** The item stack(s) whose recipe(s) the handbook shows for this machine page. */
    public List<ItemStack> getItems(int subpage) {
        if (this == WIRES) {
            ArrayList<ItemStack> li = new ArrayList<>();
            for (WireType w : WireType.wireList) {
                li.add(w.getCraftedProduct());
                li.add(w.getCraftedInsulatedProduct());
            }
            return li;
        }
        if (this == BATTERY) {
            ArrayList<ItemStack> li = new ArrayList<>();
            for (BatteryType w : BatteryType.batteryList)
                li.add(w.getCraftedProduct());
            return li;
        }
        if (this == WIRELESSPAD) {
            ArrayList<ItemStack> li = new ArrayList<>();
            for (int i = 0; i < BlockEntityWirelessCharger.ChargerTiers.tierList.length; i++) {
                ItemStack is = ElectriTiles.WIRELESSPAD.getCraftedProduct();
                final int tier = i;
                ReikaItemHelper.updateStackTag(is, tag -> tag.putInt("tier", tier));
                li.add(is);
            }
            return li;
        }
        return ReikaJavaLibrary.makeListFrom(this.getMachine().getCraftedProduct());
    }
}
