/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.guis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import reika.dragonapi.instantiable.gui.ImagedGuiButton;
import reika.dragonapi.libraries.rendering.ReikaGuiAPI;
import reika.electricraft.registry.ElectriBook;
import reika.rotarycraft.auxiliary.interfaces.HandbookEntry;
import reika.rotarycraft.gui.screen.GuiHandbook;

/**
 * The ElectriCraft handbook, ported from 1.7.10. Extends RotaryCraft's fully-ported
 * {@link GuiHandbook} base (the Rosetta-Stone handbook GUI), swapping the RotaryCraft
 * {@code HandbookRegistry} for {@link ElectriBook} throughout: text comes from
 * {@code ElectriDescriptions}, the tabs and TOC navigation come from ElectriBook, and each machine
 * page shows the machine's crafting recipe.
 *
 * <p>The right-hand machine preview shows the machine's item icon (the same fallback the
 * RotaryCraft base uses for model-less machines); the spinning 3D BER preview would need a
 * per-machine picture-in-picture render dispatch (ElectriCraft's renderers use a non-standard
 * VertexConsumer signature) and is left as a follow-up.</p>
 */
public class GuiElectriBook extends GuiHandbook {

    private static final Identifier TAB_TEXTURE = Identifier.fromNamespaceAndPath("rotarycraft", "textures/screen/handbook/tabs_toc.png");

    public GuiElectriBook(Player ep, Level world, int s, int p) {
        super(ep, world, s, p);
    }

    @Override
    protected void reloadXMLData() {
        reika.electricraft.auxiliary.ElectriDescriptions.reload();
    }

    @Override
    protected HandbookEntry getEntry() {
        return ElectriBook.getFromScreenAndPage(screen, page);
    }

    @Override
    public boolean isLimitedView() {
        return false;
    }

    @Override
    protected boolean isOnTOC() {
        return this.getEntry() == ElectriBook.TOC;
    }

    @Override
    public int getMaxScreen() {
        return ElectriBook.MODDESC.getScreen() + ElectriBook.MODDESC.getNumberChildren() / GuiHandbook.PAGES_PER_SCREEN;
    }

    @Override
    public int getMaxPage() {
        return ElectriBook.getEntriesForScreen(screen).size() - 1;
    }

    @Override
    public int getMaxSubpage() {
        ElectriBook h = (ElectriBook) this.getEntry();
        return h.isMachine() ? 1 : 0;
    }

    @Override
    protected int getNewScreenByTOCButton(int id) {
        List<ElectriBook> li = ElectriBook.getCategoryTabs();
        if (id < 0 || id >= li.size())
            return 0;
        return li.get(id).getScreen();
    }

    @Override
    protected void addTabButtons(int j, int k) {
        for (ElectriBook h : ElectriBook.getEntriesForScreen(screen)) {
            final ElectriBook tab = h;
            addRenderableWidget(new ImagedGuiButton(h.getPage(), j - 20, k + h.getRelativeTabPosn() * 20, 20, 20,
                    h.getTabColumn(), h.getTabRow(), TAB_TEXTURE, b -> this.onElectriTabClicked(tab)));
        }
    }

    private void onElectriTabClicked(ElectriBook h) {
        if (this.isOnTOC()) {
            screen = this.getNewScreenByTOCButton(h.getPage() + screen * GuiHandbook.PAGES_PER_SCREEN);
            page = 0;
        } else {
            page = h.getPage();
        }
        subpage = 0;
        renderq = 22.5F;
        this.rebuildWidgets();
    }

    @Override
    public List<HandbookEntry> getAllTabsOnScreen() {
        return new ArrayList<>(ElectriBook.getEntriesForScreen(screen));
    }

    @Override
    protected PageType getGuiLayout() {
        ElectriBook h = (ElectriBook) this.getEntry();
        if (this.isOnTOC())
            return PageType.TOC;
        if (h.isParent())
            return PageType.PLAIN;
        if (subpage == 1)
            return PageType.PLAIN;
        if (h.isMachine())
            return PageType.MACHINERENDER;
        return PageType.PLAIN;
    }

    @Override
    protected void doRenderMachine(GuiGraphicsExtractor graphics, int x, int y, HandbookEntry he) {
        //ElectriCraft renderers don't fit the vanilla BER dispatch; show the machine icon (the RC
        //base's own fallback for model-less machines). MACHINE-RENDER-PORT: 3D spin as a follow-up.
        ItemStack icon = he.getTabIcon();
        if (icon != null && !icon.isEmpty())
            ReikaGuiAPI.instance.drawItemStack(graphics, font, icon, x - 8, y - 8);
    }

    @Override
    protected void drawAuxData(GuiGraphicsExtractor graphics, int posX, int posY, int mouseX, int mouseY) {
        ElectriBook h = (ElectriBook) this.getEntry();
        if (!h.isMachine())
            return;
        List<ItemStack> out = h.getItems(subpage);
        if (out == null || out.isEmpty())
            return;
        //On the WIRES page, cycle through the wire types so every recipe is shown over time.
        if (h == ElectriBook.WIRES) {
            out = new ArrayList<>();
            var type = reika.electricraft.registry.WireType.wireList[(int) (System.currentTimeMillis() / 4000) % reika.electricraft.registry.WireType.wireList.length];
            out.add(System.currentTimeMillis() % 4000 >= 2000 ? type.getCraftedInsulatedProduct() : type.getCraftedProduct());
        }
        Collection<Recipe<?>> recipes = getCraftingRecipes();
        ReikaGuiAPI.instance.drawCustomRecipes(graphics, font, out, recipes, posX + 72, posY + 18, posX + 162, posY + 32);
    }

    /** Client-side crafting-recipe list (mirrors RotaryCraft's HandbookAuxData.getWorktable). */
    private static Collection<Recipe<?>> getCraftingRecipes() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null)
                return new ArrayList<>();
            MinecraftServer server = mc.level.getServer();
            if (server == null)
                return new ArrayList<>(); //dedicated client — recipes not iterable
            List<Recipe<?>> result = new ArrayList<>();
            for (RecipeHolder<?> rh : server.getRecipeManager().recipeMap().byType(RecipeType.CRAFTING))
                result.add(rh.value());
            return result;
        } catch (Throwable t) {
            return new ArrayList<>();
        }
    }
}
