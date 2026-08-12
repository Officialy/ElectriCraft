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

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.rendering.ReikaGuiAPI;
import reika.dragonapi.instantiable.gui.ImagedGuiButton;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;
import reika.electricraft.registry.ElectriPackets;
import reika.rotarycraft.gui.container.machine.BlankContainer;

/**
 * Faithful V31a RF cable throughput screen: six paired decimal-step buttons, shift-modified by
 * three orders of magnitude, plus Reset. Every change is sent immediately to the server through
 * the DragonAPI packet channel; zero remains a real zero-throughput limit, matching the source.
 */
public class GuiRFCable extends AbstractContainerScreen<BlankContainer<BlockEntityRFCable>> {

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(
            ElectriCraft.MODID, "textures/gui/rfcablegui.png");
    private static final Identifier BUTTONS = Identifier.fromNamespaceAndPath(
            "rotarycraft", "textures/screen/buttons.png");

    private final BlockEntityRFCable cable;
    private int limit;

    public GuiRFCable(BlankContainer<BlockEntityRFCable> container, Inventory inv, Component title) {
        super(container, inv, title, 197, 103);
        cable = container.tile;
        limit = cable.getRFLimit();
    }

    @Override
    protected void init() {
        super.init();
        int dx = imageWidth / 2 - 12;
        int widthOffset = 50;
        for (int i = 0; i < 6; i++) {
            int y = topPos + 20 + i * 12;
            int positive = i;
            int negative = i == 0 ? -40 : -i;
            addRenderableWidget(new ImagedGuiButton(positive, leftPos + dx - widthOffset, y,
                    24, 12, 18, 54, BUTTONS, button -> adjust(positive)));
            addRenderableWidget(new ImagedGuiButton(negative, leftPos + dx + widthOffset, y,
                    24, 12, 42, 54, BUTTONS, button -> adjust(negative)));
        }
        addRenderableWidget(Button.builder(Component.literal("Reset"), button -> setLimit(0))
                .bounds(leftPos + imageWidth / 2 - 20, topPos + 72, 40, 20).build());
    }

    private void adjust(int id) {
        int amount = id == -40 ? 1 : ReikaMathLibrary.intpow2(10, getPower(id));
        long next = (long)limit + (id < 0 ? -amount : amount);
        setLimit((int)Math.max(0, Math.min(Integer.MAX_VALUE, next)));
    }

    private int getPower(int base) {
        boolean shift = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        return Math.abs(base) + (shift ? 3 : 0);
    }

    private void setLimit(int value) {
        limit = value;
        cable.setRFLimit(limit);
        ReikaPacketHelper.sendPacketToServer(ElectriCraft.packetChannel, ElectriPackets.RFCABLE.ordinal(), cable, limit);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos,
                0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        ReikaGuiAPI api = ReikaGuiAPI.instance;
        int center = imageWidth / 2;
        api.drawCenteredStringNoShadow(graphics, font, "RF Cable Network", center, 6, 4210752);
        api.drawCenteredStringNoShadow(graphics, font, "Network Limit:", center, imageHeight / 2 - 10, 4210752);
        String value = String.format("%.3f %sRF/t",
                ReikaMathLibrary.getThousandBase(limit), ReikaEngLibrary.getSIPrefix(limit));
        api.drawCenteredStringNoShadow(graphics, font, value, center, imageHeight / 2 + 2, 4210752);

        for (int i = 0; i < 6; i++) {
            int amount = ReikaMathLibrary.intpow2(10, getPower(i));
            String plus = String.format("+%d%s", (int)ReikaMathLibrary.getThousandBase(amount), ReikaEngLibrary.getSIPrefix(amount));
            String minus = String.format("-%d%s", (int)ReikaMathLibrary.getThousandBase(amount), ReikaEngLibrary.getSIPrefix(amount));
            int x = imageWidth / 2 - 12;
            int y = 23 + i * 12;
            graphics.text(font, plus, x - 53 - font.width(plus), y, 0, false);
            graphics.text(font, minus, x + 77, y, 0, false);
        }
    }
}
